<template>
  <div class="login-container">
    <div class="login-bg"></div>
    <div class="login-content">
      <div class="login-header">
        <h1 class="login-title">足球赛事管理系统</h1>
        <p class="login-subtitle">欢迎回来，请登录您的账户</p>
      </div>
      
      <div class="login-card">
        <el-tabs v-model="activeTab" class="login-tabs" @tab-change="handleTabChange">
          <el-tab-pane label="手机号登录" name="phone">
            <el-form 
              ref="phoneFormRef" 
              :model="phoneForm" 
              :rules="phoneRules" 
              class="login-form"
            >
              <el-form-item prop="phone">
                <el-input 
                  v-model="phoneForm.phone" 
                  placeholder="请输入手机号" 
                  prefix-icon="Phone"
                  size="large"
                  class="login-input"
                />
              </el-form-item>
              
              <el-form-item prop="code">
                <el-input 
                  v-model="phoneForm.code" 
                  placeholder="请输入验证码" 
                  prefix-icon="Key"
                  size="large"
                  class="login-input"
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
                  @click="handlePhoneLogin"
                  class="login-button"
                  :loading="phoneLoading"
                >
                  登录
                </el-button>
              </el-form-item>
            </el-form>
          </el-tab-pane>
          
          <el-tab-pane label="用户名登录" name="username">
            <el-form 
              ref="usernameFormRef" 
              :model="usernameForm" 
              :rules="usernameRules" 
              class="login-form"
            >
              <el-form-item prop="username">
                <el-input 
                  v-model="usernameForm.username" 
                  placeholder="请输入用户名" 
                  prefix-icon="User"
                  size="large"
                  class="login-input"
                />
              </el-form-item>
              
              <el-form-item prop="password">
                <el-input 
                  v-model="usernameForm.password" 
                  type="password"
                  placeholder="请输入密码" 
                  prefix-icon="Lock"
                  size="large"
                  class="login-input"
                  show-password
                  @keyup.enter="handleUsernameLogin"
                />
              </el-form-item>
              
              <el-form-item>
                <el-button 
                  type="primary" 
                  size="large" 
                  @click="handleUsernameLogin"
                  class="login-button"
                  :loading="usernameLoading"
                >
                  登录
                </el-button>
              </el-form-item>
            </el-form>
          </el-tab-pane>
        </el-tabs>
      </div>
      
      <div class="login-footer">
        <p>© 2024 足球赛事管理系统 - 专业的赛事管理平台</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onUnmounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { sendSmsCode, verifyCode, loginByUsername } from '@/api/auth'
import { useUserStore } from '@/store'

const router = useRouter()
const userStore = useUserStore()

const phoneFormRef = ref(null)
const usernameFormRef = ref(null)

const activeTab = ref('phone')
const isSending = ref(false)
const countdown = ref(60)
const countdownTimer = ref(null)
const phoneLoading = ref(false)
const usernameLoading = ref(false)

const phoneForm = reactive({
  phone: '',
  code: ''
})

const usernameForm = reactive({
  username: '',
  password: ''
})

const validatePhone = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请输入手机号'))
  } else if (!/^1[3-9]\d{9}$/.test(value)) {
    callback(new Error('请输入正确的手机号格式'))
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

const phoneRules = reactive({
  phone: [
    { validator: validatePhone, trigger: 'blur' }
  ],
  code: [
    { validator: validateCode, trigger: 'blur' }
  ]
})

const usernameRules = reactive({
  username: [
    { validator: validateUsername, trigger: 'blur' }
  ],
  password: [
    { validator: validatePassword, trigger: 'blur' }
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
  
  const valid = await phoneFormRef.value.validateField('phone').catch(() => false)
  if (!valid) return
  
  try {
    await sendSmsCode({ phone: phoneForm.phone })
    ElMessage.success('验证码发送成功')
    startCountdown()
  } catch (error) {
    console.error('发送验证码失败：', error)
    ElMessage.error(error.message || '验证码发送失败')
  }
}

const handleTabChange = () => {
  phoneForm.phone = ''
  phoneForm.code = ''
  usernameForm.username = ''
  usernameForm.password = ''
  phoneFormRef.value?.clearValidate()
  usernameFormRef.value?.clearValidate()
}

const handlePhoneLogin = async () => {
  const valid = await phoneFormRef.value.validate().catch(() => false)
  if (!valid) return
  
  phoneLoading.value = true
  try {
    await verifyCode(phoneForm.phone, phoneForm.code)
    ElMessage.success('登录成功')
    userStore.setToken('mock-token-phone-' + Date.now())
    router.push('/')
  } catch (error) {
    console.error('登录失败：', error)
    ElMessage.error(error.message || '登录失败')
  } finally {
    phoneLoading.value = false
  }
}

const handleUsernameLogin = async () => {
  const valid = await usernameFormRef.value.validate().catch(() => false)
  if (!valid) return
  
  usernameLoading.value = true
  try {
    await loginByUsername(usernameForm.username, usernameForm.password)
    ElMessage.success('登录成功')
    userStore.setToken('mock-token-' + Date.now())
    router.push('/')
  } catch (error) {
    console.error('登录失败：', error)
    ElMessage.error(error.message || '登录失败')
  } finally {
    usernameLoading.value = false
  }
}

onUnmounted(() => {
  if (countdownTimer.value) {
    clearInterval(countdownTimer.value)
  }
})
</script>

<style scoped>
.login-container {
  width: 100%;
  height: 100%;
  position: relative;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e8ec 100%);
}

.login-bg {
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

.login-content {
  position: relative;
  z-index: 2;
  width: 100%;
  max-width: 480px;
  padding: 20px;
}

.login-header {
  text-align: center;
  margin-bottom: 40px;
}

.login-title {
  font-size: 32px;
  font-weight: 700;
  color: #1a1a2e;
  margin: 0 0 12px 0;
  letter-spacing: -0.5px;
}

.login-subtitle {
  font-size: 15px;
  color: #6b7280;
  margin: 0;
  font-weight: 400;
}

.login-card {
  background: #ffffff;
  border-radius: 20px;
  box-shadow: 
    0 1px 3px rgba(0, 0, 0, 0.04),
    0 4px 12px rgba(0, 0, 0, 0.06),
    0 20px 60px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  transition: box-shadow 0.3s ease;
}

.login-card:hover {
  box-shadow: 
    0 1px 3px rgba(0, 0, 0, 0.05),
    0 8px 24px rgba(0, 0, 0, 0.08),
    0 30px 80px rgba(0, 0, 0, 0.1);
}

.login-tabs {
  padding: 32px;
}

.login-tabs :deep(.el-tabs__header) {
  margin: 0 0 28px 0;
  padding: 0 8px;
}

.login-tabs :deep(.el-tabs__nav-wrap::after) {
  background-color: #f1f5f9;
}

.login-tabs :deep(.el-tabs__item) {
  font-size: 15px;
  font-weight: 600;
  color: #94a3b8;
  padding: 0 24px 16px;
  transition: all 0.2s ease;
}

.login-tabs :deep(.el-tabs__item:hover) {
  color: #475569;
}

.login-tabs :deep(.el-tabs__item.is-active) {
  color: #16a34a;
  font-weight: 700;
}

.login-tabs :deep(.el-tabs__active-bar) {
  background: linear-gradient(90deg, #16a34a, #22c55e);
  height: 3px;
  border-radius: 2px;
}

.login-form {
  padding: 0 8px;
}

.login-input {
  border-radius: 12px;
}

.login-input :deep(.el-input__wrapper) {
  border-radius: 12px;
  padding: 12px 16px;
  background: #f8fafc;
  border: 2px solid #e2e8f0;
  box-shadow: none;
  transition: all 0.2s ease;
}

.login-input :deep(.el-input__wrapper:hover) {
  border-color: #cbd5e1;
  background: #ffffff;
}

.login-input :deep(.el-input__wrapper.is-focus) {
  border-color: #16a34a;
  background: #ffffff;
  box-shadow: 0 0 0 3px rgba(22, 163, 74, 0.08);
}

.login-input :deep(.el-input__inner) {
  font-size: 15px;
  color: #1e293b;
}

.login-input :deep(.el-input__inner::placeholder) {
  color: #94a3b8;
}

.login-input :deep(.el-input__prefix-inner) {
  color: #94a3b8;
  margin-right: 8px;
}

.login-input :deep(.el-input__wrapper.is-focus .el-input__prefix-inner) {
  color: #16a34a;
}

.login-input :deep(.el-input-group__append) {
  background: transparent;
  border: none;
  padding: 0 0 0 12px;
}

.code-button {
  border-radius: 10px;
  height: 48px;
  padding: 0 16px;
  font-size: 14px;
  font-weight: 600;
  background: #eff6ff;
  border: none;
  color: #2563eb;
  transition: all 0.2s ease;
}

.code-button:hover:not(:disabled) {
  background: #dbeafe;
  color: #1d4ed8;
}

.code-button:disabled {
  background: #f1f5f9;
  color: #94a3b8;
}

.login-button {
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

.login-button:hover {
  background: linear-gradient(135deg, #15803d 0%, #16a34a 100%);
  box-shadow: 0 6px 20px rgba(22, 163, 74, 0.35);
  transform: translateY(-1px);
}

.login-button:active {
  transform: translateY(0);
  box-shadow: 0 2px 8px rgba(22, 163, 74, 0.3);
}

.login-button :deep(.el-loading-text) {
  color: #ffffff;
}

.login-footer {
  text-align: center;
  margin-top: 32px;
}

.login-footer p {
  font-size: 13px;
  color: #9ca3af;
  margin: 0;
}

@media (max-width: 480px) {
  .login-content {
    padding: 16px;
  }
  
  .login-title {
    font-size: 26px;
  }
  
  .login-subtitle {
    font-size: 14px;
  }
  
  .login-tabs {
    padding: 24px 20px;
  }
  
  .login-form {
    padding: 0;
  }
}
</style>