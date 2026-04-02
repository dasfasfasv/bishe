import { createRouter, createWebHistory } from 'vue-router'
import { getToken } from '@/utils/auth'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue')
  },
  {
    path: '/',
    component: () => import('@/views/Layout.vue'),
    redirect: '/dashboard',
    children: [
      // 首页
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/Index.vue'),
        meta: { title: '仪表板' }
      },

      // 数据分析
      {
        path: 'analysis',
        name: 'Analysis',
        component: () => import('@/views/analysis/Dashboard.vue'),
        meta: { title: '数据分析' }
      },

      // 访客管理
      {
        path: 'visitor',
        name: 'Visitor',
        component: () => import('@/views/visitor/List.vue'),
        meta: { title: '访客管理' }
      },

      // 预约管理
      {
        path: 'appointment',
        name: 'Appointment',
        component: () => import('@/views/appointment/List.vue'),
        meta: { title: '预约管理' }
      },

      // 审批管理
      {
        path: 'approval',
        name: 'Approval',
        component: () => import('@/views/approval/List.vue'),
        meta: { title: '审批管理' }
      },

      // 门禁管理
      {
        path: 'access',
        name: 'Access',
        component: () => import('@/views/access/List.vue'),
        meta: { title: '门禁授权' }
      },

      // 通行记录
      {
        path: 'record',
        name: 'Record',
        component: () => import('@/views/record/List.vue'),
        meta: { title: '通行记录' }
      },

      // 黑名单管理
      {
        path: 'blacklist',
        name: 'Blacklist',
        component: () => import('@/views/blacklist/List.vue'),
        meta: { title: '黑名单管理' }
      },

      // 系统管理 - 用户
      {
        path: 'system/user',
        name: 'SystemUser',
        component: () => import('@/views/system/user/List.vue'),
        meta: { title: '用户管理' }
      },

      // 系统管理 - 部门
      {
        path: 'system/department',
        name: 'SystemDepartment',
        component: () => import('@/views/system/department/List.vue'),
        meta: { title: '部门管理' }
      },

      // 修改密码
      // {
      //   path: 'system/change-password',
      //   name: 'ChangePassword',
      //   component: () => import('@/views/system/user/ChangePassword.vue'),
      //   meta: { title: '修改密码' }
      // }
    ]
  },

  // 404
  {
    path: '/:pathMatch(.*)*',
    redirect: '/login'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = getToken()

  if (to.path === '/login') {
    next()
  } else if (!token) {
    next('/login')
  } else {
    next()
  }
})

export default router