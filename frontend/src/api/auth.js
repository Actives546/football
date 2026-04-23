// 1. 导入封装好的 Axios 请求实例
import request from '@/utils/request'

// 2. 发送短信验证码接口
// 参数 data: 包含手机号的对象 { phone: '手机号' }
// 返回值: Promise 对象，包含后端返回的验证码数据
export function sendSmsCode(data) {
  // 3. 调用 request 实例发送 POST 请求
  return request({
    // 4. 请求 URL，拼接在 baseURL 后面（完整路径: /api/auth/sendCode）
    url: '/auth/sendCode',
    // 5. 请求方法为 POST
    method: 'post',
    // 6. 请求体数据，会被序列化为 JSON
    data
  })
}

// 7. 验证短信验证码接口
// 参数 phone: 手机号
// 参数 code: 用户输入的验证码
// 返回值: Promise 对象，包含验证结果
export function verifyCode(phone, code) {
  // 8. 调用 request 实例发送 POST 请求
  return request({
    // 9. 请求 URL
    url: '/auth/verifyCode',
    // 10. 请求方法为 POST
    method: 'post',
    // 11. URL 查询参数，会拼接到 URL 后面
    params: { phone, code }
  })
}

// 12. 用户名密码登录接口
// 参数 username: 用户名
// 参数 password: 密码
// 返回值: Promise 对象，包含登录结果
export function loginByUsername(username, password) {
  // 13. 调用 request 实例发送 POST 请求
  return request({
    // 14. 请求 URL
    url: '/auth/login',
    // 15. 请求方法为 POST
    method: 'post',
    // 16. 请求体数据
    data: { username, password }
  })
}
