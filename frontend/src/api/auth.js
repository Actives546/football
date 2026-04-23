import request from '@/utils/request'

export function sendSmsCode(data) {
  return request({
    url: '/auth/sendCode',
    method: 'post',
    data
  })
}

export function verifyCode(phone, code) {
  return request({
    url: '/auth/verifyCode',
    method: 'post',
    params: { phone, code }
  })
}

export function loginByUsername(username, password) {
  return request({
    url: '/auth/login',
    method: 'post',
    data: { username, password }
  })
}
