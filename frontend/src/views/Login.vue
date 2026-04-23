<template>
  <div class="login-container">
    <el-card class="login-card">
      <template #header>
        <div class="card-header">
          <span>用户登录</span>
        </div>
      </template>
      <el-form ref="loginFormRef" :model="loginForm" :rules="loginRules" label-width="80px">
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="loginForm.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="验证码" prop="code">
          <el-input v-model="loginForm.code" placeholder="请输入验证码">
            <template #append>
              <el-button
                :disabled="isSending"
                @click="handleSendCode"
              >
                {{ isSending ? `${countdown}s` : '发送验证码' }}
              </el-button>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleLogin" style="width: 100%">登录</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { sendSmsCode, verifyCode } from '@/api/auth'

const router = useRouter()
const loginFormRef = ref(null)

const isSending = ref(false)
const countdown = ref(60)
let countdownTimer = null

const loginForm = reactive({
  phone: '',
  code: ''
})

const loginRules = reactive({
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  code: [
    { required: true, message: '请输入验证码', trigger: 'blur' }
  ]
})

const startCountdown = () => {
  isSending.value = true
  countdown.value = 60
  countdownTimer = setInterval(() => {
    countdown.value--
    if (countdown.value <= 0) {
      isSending.value = false
      clearInterval(countdownTimer)
    }
  }, 1000)
}

const handleSendCode = async () => {
  if (isSending.value) return
  
  const valid = await loginFormRef.value.validateField('phone').catch(() => false)
  if (!valid) return
  
  try {
    await sendSmsCode({ phone: loginForm.phone })
    ElMessage.success('验证码发送成功')
    startCountdown()
  } catch (error) {
    console.error('发送验证码失败：', error)
  }
}

const handleLogin = async () => {
  await loginFormRef.value.validate()
  
  try {
    await verifyCode(loginForm.phone, loginForm.code)
    ElMessage.success('登录成功')
    router.push('/')
  } catch (error) {
    console.error('登录失败：', error)
  }
}

onUnmounted(() => {
  if (countdownTimer) {
    clearInterval(countdownTimer)
  }
})
</script>

<style scoped>
.login-container {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-card {
  width: 400px;
}

.card-header {
  text-align: center;
  font-size: 18px;
  font-weight: bold;
}
</style>
