package com.football.mapper;

import com.football.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {

    // 根据用户名查询用户信息
    User selectByUsername(@Param("username") String username);

    // 根据手机号查询用户信息
    User selectByPhone(@Param("phone") String phone);

    // 根据用户ID查询用户信息
    User selectById(@Param("id") Long id);

    // 新增用户
    int insert(User user);

    // 更新用户信息
    int updateById(User user);
}
