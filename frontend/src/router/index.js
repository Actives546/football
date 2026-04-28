import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/store'

const Layout = () => import('@/components/Layout.vue')
const Dashboard = () => import('@/views/Dashboard.vue')
const MatchList = () => import('@/views/MatchList.vue')
const SeasonList = () => import('@/views/SeasonList.vue')
const ScheduleList = () => import('@/views/ScheduleList.vue')
const OrganizationList = () => import('@/views/OrganizationList.vue')
const PersonnelList = () => import('@/views/PersonnelList.vue')
const PlayerList = () => import('@/views/PlayerList.vue')
const Login = () => import('@/views/Login.vue')
const Register = () => import('@/views/Register.vue')

const routes = [
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: Dashboard,
        meta: { title: '首页', requiresAuth: true }
      },
      {
        path: 'organization/list',
        name: 'OrganizationList',
        component: OrganizationList,
        meta: { title: '机构管理', requiresAuth: true }
      },
      {
        path: 'match/list',
        name: 'MatchList',
        component: MatchList,
        meta: { title: '赛事信息', requiresAuth: true }
      },
      {
        path: 'season/list',
        name: 'SeasonList',
        component: SeasonList,
        meta: { title: '赛季管理', requiresAuth: true }
      },
      {
        path: 'schedule/list',
        name: 'ScheduleList',
        component: ScheduleList,
        meta: { title: '赛程信息管理', requiresAuth: true }
      },
      {
        path: 'personnel/list',
        name: 'PersonnelList',
        component: PersonnelList,
        meta: { title: '人员管理', requiresAuth: true }
      },
      {
        path: 'player/list',
        name: 'PlayerList',
        component: PlayerList,
        meta: { title: '球员管理', requiresAuth: true }
      }
    ]
  },
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: { title: '登录' }
  },
  {
    path: '/register',
    name: 'Register',
    component: Register,
    meta: { title: '注册' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  document.title = to.meta.title || '足球赛事系统'
  
  const userStore = useUserStore()
  
  if (to.meta.requiresAuth && !userStore.isLoggedIn) {
    next('/login')
  } else if ((to.path === '/login' || to.path === '/register') && userStore.isLoggedIn) {
    next('/dashboard')
  } else if (to.path === '/' && userStore.isLoggedIn) {
    next('/dashboard')
  } else {
    next()
  }
})

export default router
