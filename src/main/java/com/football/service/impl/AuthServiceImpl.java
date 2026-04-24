package com.football.service.impl;

import cn.hutool.crypto.digest.BCrypt;
import com.football.common.BusinessException;
import com.football.common.Result;
import com.football.dto.LoginDTO;
import com.football.dto.RegisterDTO;
import com.football.entity.User;
import com.football.mapper.UserMapper;
import com.football.service.AuthService;
import com.football.util.JwtUtil;
import com.football.util.PasswordEncoder;
import com.football.util.RedisUtil;
import com.football.util.VerifyCodeUtil;
import com.football.vo.LoginVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import org.springframework.beans.factory.annotation.Autowired;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    // 注入Redis工具类，用于操作Redis缓存
    @Autowired
    private RedisUtil redisUtil;

    // 注入验证码工具类，用于生成和发送验证码
    @Autowired
    private VerifyCodeUtil verifyCodeUtil;

    // 注入JWT工具类，用于生成和验证Token
    @Autowired
    private JwtUtil jwtUtil;

    // 注入用户Mapper，用于操作用户数据库
    @Autowired
    private UserMapper userMapper;

    // 从配置文件读取验证码过期时间，默认300秒（5分钟）
    @Value("${football.verify-code.expire-time:300}")
    private long expireTime;

    // 从配置文件读取验证码长度，默认6位
    @Value("${football.verify-code.length:6}")
    private int codeLength;

    // Redis中存储验证码的key前缀
    private static final String CODE_PREFIX = "verify:code:";

    // 登录类型：手机号验证码登录
    private static final String LOGIN_TYPE_PHONE = "phone";

    // 登录类型：用户名密码登录
    private static final String LOGIN_TYPE_PASSWORD = "password";

    @Override
    public Result<Map<String, Object>> sendSmsCode(String phone) {
        // 1. 调用验证码工具类生成指定长度的随机数字验证码
        String code = verifyCodeUtil.generateCode(codeLength);
        // 2. 拼接 Redis 缓存的 key，格式为：verify:code:手机号
        String key = CODE_PREFIX + phone;

        // 3. 将验证码存入 Redis，设置过期时间（单位：秒）
        redisUtil.set(key, code, expireTime, TimeUnit.SECONDS);
        // 4. 调用验证码工具类发送短信验证码（实际项目中这里会调用第三方短信服务）
        verifyCodeUtil.sendSmsCode(phone, code);

        // 5. 记录日志，方便调试和排查问题
        log.info("短信验证码已发送到 {}，验证码：{}，有效期 {} 秒", phone, code, expireTime);
        
        // 6. 创建返回数据的 Map 集合
        Map<String, Object> data = new HashMap<>();
        // 7. 将手机号放入返回数据中
        data.put("phone", phone);
        // 8. 将验证码放入返回数据中（开发环境调试用，生产环境应移除）
        data.put("code", code);
        
        // 9. 返回成功响应，包含提示消息和数据
        return Result.success("验证码发送成功", data);
    }

    @Override
    public boolean verifyCode(String phone, String code) {
        // 1. 拼接Redis中存储验证码的key
        String key = CODE_PREFIX + phone;
        // 2. 从Redis中获取缓存的验证码
        Object cachedCode = redisUtil.get(key);

        // 3. 如果缓存中不存在验证码，说明验证码已过期或不存在
        if (cachedCode == null) {
            // 4. 抛出业务异常，提示验证码已过期
            throw new BusinessException("验证码已过期或不存在");
        }

        // 5. 比较用户输入的验证码与缓存中的验证码是否一致
        if (!cachedCode.toString().equals(code)) {
            // 6. 如果不一致，抛出业务异常，提示验证码错误
            throw new BusinessException("验证码错误");
        }

        // 7. 验证成功后，删除Redis中的验证码，防止重复使用
        redisUtil.delete(key);
        // 8. 返回验证成功
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<LoginVO> login(LoginDTO loginDTO) {
        // 1. 获取登录类型
        String loginType = loginDTO.getLoginType();
        
        // 2. 声明用户对象，用于存储登录成功后的用户信息
        User user = null;

        // 3. 根据登录类型执行不同的登录逻辑
        if (LOGIN_TYPE_PHONE.equals(loginType)) {
            // 4. 如果是手机号登录，调用手机号登录方法
            user = loginByPhone(loginDTO);
        } else if (LOGIN_TYPE_PASSWORD.equals(loginType)) {
            // 5. 如果是密码登录，调用密码登录方法
            user = loginByPassword(loginDTO);
        } else {
            // 6. 如果登录类型不合法，抛出业务异常
            throw new BusinessException("不支持的登录类型");
        }

        // 7. 检查用户状态是否正常（1-正常，0-禁用）
        if (user.getStatus() != 1) {
            // 8. 如果用户被禁用，抛出业务异常
            throw new BusinessException("账户已被禁用，请联系管理员");
        }

        // 9. 更新用户的最后登录时间
        user.setLastLoginTime(LocalDateTime.now());
        // 10. 调用Mapper更新用户信息到数据库
        userMapper.updateById(user);

        // 11. 调用JWT工具类生成Token，传入用户ID、用户名、手机号
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getPhone());

        // 12. 生成Redis中存储Token的key，格式为：user:token:用户ID
        String tokenKey = jwtUtil.getTokenKey(user.getId());
        // 13. 计算Token的过期时间（转换为秒）
        long tokenExpireSeconds = jwtUtil.getExpiration() / 1000;
        // 14. 将Token存入Redis，设置过期时间
        redisUtil.set(tokenKey, token, tokenExpireSeconds, TimeUnit.SECONDS);

        // 15. 记录登录成功的日志
        log.info("用户登录成功，用户ID：{}，用户名：{}，登录类型：{}", user.getId(), user.getUsername(), loginType);

        // 16. 构建登录响应对象LoginVO
        LoginVO loginVO = buildLoginVO(user, token);

        // 17. 返回登录成功的响应
        return Result.success("登录成功", loginVO);
    }

    // 手机号验证码登录方法
    private User loginByPhone(LoginDTO loginDTO) {
        // 1. 获取用户输入的手机号
        String phone = loginDTO.getPhone();
        // 2. 获取用户输入的验证码
        String code = loginDTO.getCode();

        // 3. 校验手机号是否为空
        if (!StringUtils.hasText(phone)) {
            // 4. 如果手机号为空，抛出业务异常
            throw new BusinessException("手机号不能为空");
        }

        // 5. 校验验证码是否为空
        if (!StringUtils.hasText(code)) {
            // 6. 如果验证码为空，抛出业务异常
            throw new BusinessException("验证码不能为空");
        }

        // 7. 调用验证码验证方法，验证验证码是否正确
        verifyCode(phone, code);

        // 8. 根据手机号查询用户信息
        User user = userMapper.selectByPhone(phone);

        // 9. 如果用户不存在，自动注册新用户
        if (user == null) {
            // 10. 创建新用户对象
            user = new User();
            // 11. 设置手机号
            user.setPhone(phone);
            // 12. 生成默认用户名，格式为：user_手机号后6位
            user.setUsername("user_" + phone.substring(phone.length() - 6));
            // 13. 设置默认昵称
            user.setNickname("足球迷" + phone.substring(phone.length() - 4));
            // 14. 设置用户状态为正常（1-正常）
            user.setStatus(1);
            // 15. 插入新用户到数据库
            userMapper.insert(user);
            // 16. 记录新用户注册的日志
            log.info("新用户注册成功，手机号：{}，用户ID：{}", phone, user.getId());
        }

        // 17. 返回用户对象
        return user;
    }

    // 用户名密码登录方法
    private User loginByPassword(LoginDTO loginDTO) {
        // 1. 获取用户输入的用户名
        String username = loginDTO.getUsername();
        // 2. 获取用户输入的密码
        String password = loginDTO.getPassword();

        // 3. 校验用户名是否为空
        if (!StringUtils.hasText(username)) {
            // 4. 如果用户名为空，抛出业务异常
            throw new BusinessException("用户名不能为空");
        }

        // 5. 校验密码是否为空
        if (!StringUtils.hasText(password)) {
            // 6. 如果密码为空，抛出业务异常
            throw new BusinessException("密码不能为空");
        }

        // 7. 根据用户名查询用户信息
        User user = userMapper.selectByUsername(username);

        // 8. 如果用户不存在，抛出业务异常
        if (user == null) {
            // 9. 抛出用户不存在的异常
            throw new BusinessException("用户名或密码错误");
        }

        // 10. 验证密码是否正确（使用BCrypt加密验证）
        if (!BCrypt.checkpw(password, user.getPassword())) {
            // 11. 如果密码错误，抛出业务异常
            throw new BusinessException("用户名或密码错误");
        }

        // 12. 返回用户对象
        return user;
    }

    // 构建登录响应对象
    private LoginVO buildLoginVO(User user, String token) {
        // 1. 创建LoginVO对象
        LoginVO loginVO = new LoginVO();
        // 2. 设置Token
        loginVO.setToken(token);
        // 3. 设置用户ID
        loginVO.setUserId(user.getId());
        // 4. 设置用户名
        loginVO.setUsername(user.getUsername());
        // 5. 设置手机号
        loginVO.setPhone(user.getPhone());
        // 6. 设置昵称
        loginVO.setNickname(user.getNickname());
        // 7. 设置头像
        loginVO.setAvatar(user.getAvatar());
        // 8. 返回构建好的LoginVO对象
        return loginVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> register(RegisterDTO registerDTO) {
        // 1. 校验两次密码是否一致
        if (!registerDTO.getPassword().equals(registerDTO.getConfirmPassword())) {
            throw new BusinessException("两次输入的密码不一致");
        }

        // 2. 校验验证码
        verifyCode(registerDTO.getPhone(), registerDTO.getCode());

        // 3. 检查用户名是否已存在
        User existUserByUsername = userMapper.selectByUsername(registerDTO.getUsername());
        if (existUserByUsername != null) {
            throw new BusinessException("用户名已存在，请更换用户名");
        }

        // 4. 检查手机号是否已注册
        User existUserByPhone = userMapper.selectByPhone(registerDTO.getPhone());
        if (existUserByPhone != null) {
            throw new BusinessException("该手机号已注册，请直接登录");
        }

        // 5. 创建新用户对象
        User user = new User();
        // 6. 设置用户名
        user.setUsername(registerDTO.getUsername());
        // 7. 设置手机号
        user.setPhone(registerDTO.getPhone());
        // 8. 密码加密存储
        user.setPassword(PasswordEncoder.encode(registerDTO.getPassword()));
        // 9. 设置默认昵称
        user.setNickname("足球迷" + registerDTO.getPhone().substring(registerDTO.getPhone().length() - 4));
        // 10. 设置用户状态为正常（1-正常）
        user.setStatus(1);

        // 11. 插入新用户到数据库
        userMapper.insert(user);

        // 12. 记录注册成功的日志
        log.info("用户注册成功，用户名：{}，手机号：{}，用户ID：{}", 
                user.getUsername(), user.getPhone(), user.getId());

        // 13. 返回注册成功
        return Result.success("注册成功", true);
    }
}
