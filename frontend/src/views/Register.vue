<template>
  <div class="register-container">
    <div class="register-bg"></div>
    <div class="register-content">
      <div class="register-header">
        <h1 class="register-title">足球赛事管理系统</h1>
        <p class="register-subtitle">创建账户，开启您的足球之旅</p>
      </div>
      
      <div class="register-card">
        <el-form 
          ref="registerFormRef" 
          :model="registerForm" 
          :rules="registerRules" 
          class="register-form"
        >
          <el-form-item prop="username">
            <el-input 
              v-model="registerForm.username" 
              placeholder="请输入用户名" 
              prefix-icon="User"
              class="register-input"
            />
          </el-form-item>
          
          <el-form-item prop="phone">
            <el-input 
              v-model="registerForm.phone" 
              placeholder="请输入手机号" 
              prefix-icon="Phone"
              class="register-input"
            />
          </el-form-item>
          
          <el-form-item prop="password">
            <el-input 
              v-model="registerForm.password" 
              type="password"
              placeholder="请输入密码" 
              prefix-icon="Lock"
              class="register-input"
              show-password
            />
          </el-form-item>
          
          <el-form-item prop="confirmPassword">
            <el-input 
              v-model="registerForm.confirmPassword" 
              type="password"
              placeholder="请确认密码" 
              prefix-icon="Lock"
              class="register-input"
              show-password
            />
          </el-form-item>
          
          <el-form-item prop="code">
            <el-input 
              v-model="registerForm.code" 
              placeholder="请输入验证码" 
              prefix-icon="Key"
              class="register-input code-input-group"
            >
              <template #append>
                <el-button
                  :disabled="isSending"
                  :loading="isSending"
                  @click="handleSendCode"
                  class="code-button"
                >
                  {{ isSending ? `${countdown}s` : '获取验证码' }}
                </el-button>
              </template>
            </el-input>
          </el-form-item>
          
          <el-form-item>
            <el-button 
              type="primary" 
              size="large" 
              @click="handleRegister"
              class="register-button"
              :loading="registerLoading"
            >
              注册
            </el-button>
          </el-form-item>
          
          <div class="login-link">
            <span>已有账户？</span>
            <router-link to="/login" class="text-link">立即登录</router-link>
          </div>
        </el-form>
      </div>
      
      <div class="register-footer">
        <p>© 2024 足球赛事管理系统 - 专业的赛事管理平台</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { sendSmsCode, register } from '@/api/auth'

const router = useRouter()

const registerFormRef = ref(null)

const isSending = ref(false)
const countdown = ref(60)
const countdownTimer = ref(null)
const registerLoading = ref(false)

const registerForm = reactive({
  username: '',
  phone: '',
  password: '',
  confirmPassword: '',
  code: ''
})

const validateUsername = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请输入用户名'))
  } else if (value.length < 3) {
    callback(new Error('用户名长度不能少于3位'))
  } else if (value.length > 20) {
    callback(new Error('用户名长度不能超过20位'))
  } else if (!/^[a-zA-Z0-9_]+$/.test(value)) {
    callback(new Error('用户名只能包含字母、数字和下划线'))
  } else {
    callback()
  }
}

const validatePhone = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请输入手机号'))
  } else if (!/^1[3-9]\d{9}$/.test(value)) {
    callback(new Error('请输入正确的手机号格式'))
  } else {
    callback()
  }
}

const validatePassword = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请输入密码'))
  } else if (value.length < 6) {
    callback(new Error('密码长度不能少于6位'))
  } else if (value.length > 20) {
    callback(new Error('密码长度不能超过20位'))
  } else {
    callback()
  }
}

const validateConfirmPassword = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请确认密码'))
  } else if (value !== registerForm.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const validateCode = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请输入验证码'))
  } else if (!/^\d{6}$/.test(value)) {
    callback(new Error('验证码为6位数字'))
  } else {
    callback()
  }
}

const registerRules = reactive({
  username: [
    { validator: validateUsername, trigger: 'blur' }
  ],
  phone: [
    { validator: validatePhone, trigger: 'blur' }
  ],
  password: [
    { validator: validatePassword, trigger: 'blur' }
  ],
  confirmPassword: [
    { validator: validateConfirmPassword, trigger: 'blur' }
  ],
  code: [
    { validator: validateCode, trigger: 'blur' }
  ]
})

const startCountdown = () => {
  isSending.value = true
  countdown.value = 60
  countdownTimer.value = setInterval(() => {
    countdown.value--
    if (countdown.value <= 0) {
      isSending.value = false
      clearInterval(countdownTimer.value)
      countdownTimer.value = null
    }
  }, 1000)
}

const handleSendCode = async () => {
  if (isSending.value) return
  
  const valid = await registerFormRef.value.validateField('phone').catch(() => false)
  if (!valid) return
  
  try {
    const response = await sendSmsCode({ phone: registerForm.phone })
    console.log('发送验证码响应：', JSON.stringify(response, null, 2))
    const { data } = response
    ElMessage.success(`验证码发送成功！验证码：${data?.code || '已发送至手机'}`)
    startCountdown()
  } catch (error) {
    console.error('发送验证码失败：', error)
    ElMessage.error(error.message || '验证码发送失败')
  }
}

const handleRegister = async () => {
  const valid = await registerFormRef.value.validate().catch(() => false)
  if (!valid) return
  
  registerLoading.value = true
  try {
    const response = await register(registerForm)
    console.log('注册响应：', response)
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  } catch (error) {
    console.error('注册失败：', error)
    ElMessage.error(error.message || '注册失败')
  } finally {
    registerLoading.value = false
  }
}

onUnmounted(() => {
  if (countdownTimer.value) {
    clearInterval(countdownTimer.value)
  }
})
</script>

<style scoped>
.register-container {
  width: 100%;
  height: 100%;
  position: relative;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e8ec 100%);
}

.register-bg {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: 
    radial-gradient(ellipse at 20% 80%, rgba(255, 107, 107, 0.1) 0%, transparent 50%),
    radial-gradient(ellipse at 80% 20%, rgba(78, 205, 196, 0.1) 0%, transparent 50%),
    radial-gradient(ellipse at 40% 40%, rgba(199, 125, 255, 0.05) 0%, transparent 50%);
  z-index: 1;
}

.register-content {
  position: relative;
  z-index: 2;
  width: 100%;
  max-width: 480px;
  padding: 20px;
}

.register-header {
  text-align: center;
  margin-bottom: 30px;
}

.register-title {
  font-size: 32px;
  font-weight: 700;
  color: #1a1a2e;
  margin: 0 0 12px 0;
  letter-spacing: -0.5px;
}

.register-subtitle {
  font-size: 15px;
  color: #6b7280;
  margin: 0;
  font-weight: 400;
}

.register-card {
  background: #ffffff;
  border-radius: 20px;
  box-shadow: 
    0 1px 3px rgba(0, 0, 0, 0.04),
    0 4px 12px rgba(0, 0, 0, 0.06),
    0 20px 60px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  transition: box-shadow 0.3s ease;
  padding: 32px;
}

.register-card:hover {
  box-shadow: 
    0 1px 3px rgba(0, 0, 0, 0.05),
    0 8px 24px rgba(0, 0, 0, 0.08),
    0 30px 80px rgba(0, 0, 0, 0.1);
}

.register-form {
  padding: 0 8px;
}

.register-input {
  border-radius: 8px;
}

.register-input :deep(.el-input__wrapper) {
  border-radius: 8px;
  padding: 8px 12px;
  background: #fafbfc;
  border: 1px solid #e5e7eb;
  box-shadow: none;
  transition: all 0.2s ease;
}

.register-input :deep(.el-input__wrapper:hover) {
  border-color: #d1d5db;
  background: #ffffff;
}

.register-input :deep(.el-input__wrapper.is-focus) {
  border-color: #16a34a;
  background: #ffffff;
  box-shadow: none;
}

.register-input :deep(.el-input__inner) {
  font-size: 14px;
  color: #374151;
}

.register-input :deep(.el-input__inner::placeholder) {
  color: #9ca3af;
}

.register-input :deep(.el-input__prefix-inner) {
  color: #9ca3af;
  margin-right: 6px;
}

.register-input :deep(.el-input__wrapper.is-focus .el-input__prefix-inner) {
  color: #16a34a;
}

.code-input-group :deep(.el-input-group__append) {
  background: transparent;
  border: none;
  padding: 0;
  margin: 0;
}

.code-input-group :deep(.el-input__wrapper) {
  border-radius: 8px 0 0 8px;
  border-right: none;
  padding: 6px 12px;
}

.code-input-group :deep(.el-input__wrapper.is-focus) {
  border-right: none;
}

.code-input-group :deep(.el-input__wrapper:hover) {
  border-right: none;
}

.code-input-group :deep(.el-input-group__append .el-button) {
  border-radius: 0 8px 8px 0;
  border-left: none;
  margin: 0;
  padding: 0;
}

.code-button {
  border-radius: 0 8px 8px 0;
  height: 36px;
  min-width: 90px;
  padding: 0 12px;
  font-size: 12px;
  font-weight: 500;
  background: #f0fdf4;
  border: 1px solid #bbf7d0;
  border-left: none;
  color: #16a34a;
  transition: all 0.2s ease;
  white-space: nowrap;
}

.code-button:hover:not(:disabled) {
  background: #dcfce7;
  border-color: #86efac;
  color: #15803d;
}

.code-button:disabled {
  background: #f3f4f6;
  border-color: #e5e7eb;
  color: #9ca3af;
}

.register-button {
  width: 100%;
  height: 52px;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  background: linear-gradient(135deg, #16a34a 0%, #22c55e 100%);
  border: none;
  box-shadow: 0 4px 14px rgba(22, 163, 74, 0.25);
  transition: all 0.3s ease;
}

.register-button:hover {
  background: linear-gradient(135deg, #15803d 0%, #16a34a 100%);
  box-shadow: 0 6px 20px rgba(22, 163, 74, 0.35);
  transform: translateY(-1px);
}

.register-button:active {
  transform: translateY(0);
  box-shadow: 0 2px 8px rgba(22, 163, 74, 0.3);
}

.register-button :deep(.el-loading-text) {
  color: #ffffff;
}

.login-link {
  text-align: center;
  margin-top: 20px;
  font-size: 14px;
  color: #6b7280;
}

.text-link {
  color: #16a34a;
  text-decoration: none;
  margin-left: 4px;
  transition: color 0.2s ease;
}

.text-link:hover {
  color: #15803d;
  text-decoration: underline;
}

.register-footer {
  text-align: center;
  margin-top: 32px;
}

.register-footer p {
  font-size: 13px;
  color: #9ca3af;
  margin: 0;
}

@media (max-width: 480px) {
  .register-content {
    padding: 16px;
  }
  
  .register-title {
    font-size: 26px;
  }
  
  .register-subtitle {
    font-size: 14px;
  }
  
  .register-card {
    padding: 24px 20px;
  }
  
  .register-form {
    padding: 0;
  }
}
</style>
