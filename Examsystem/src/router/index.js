import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'welcome',
      component: () => import('../views/Welcome.vue')
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/longin.vue'),
      meta: { requiresGuest: true } // 添加这个meta，已登录用户不应该看到登录页
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('../views/Register.vue'),
      meta: { requiresGuest: true }
    },
    {
      path: '/change-password',
      name: 'change-password',
      component: () => import('../views/ChangePassword.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/admin/import-questions',
      name: 'import-questions',
      component: () => import('../views/admin/ImportQuestions.vue'),
      meta: { requiresAuth: true, requiresAdmin: true }
    },
    {
      path: '/exam',
      name: 'exam',
      component: () => import('../views/Exam.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/exam-result',
      name: 'exam-result',
      component: () => import('../views/ExamResult.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/404',
      name: '404',
      component: () => import('../views/404.vue')
    },
    {
      path: '/:pathMatch(.*)*',
      redirect: '/404'
    }
  ]
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const isAuthenticated = localStorage.getItem('token') !== null
  const isAdmin = localStorage.getItem('isAdmin') === 'true'

  // 如果用户已登录但尝试访问需要游客状态的页面
  if (to.meta.requiresGuest && isAuthenticated) {
    next('/') // 重定向到首页
    return
  }

  // 如果页面需要登录但用户未登录
  if (to.meta.requiresAuth && !isAuthenticated) {
    next('/login')
    return
  }

  // 如果页面需要管理员权限但用户不是管理员
  if (to.meta.requiresAdmin && !isAdmin) {
    next('/404') // 或者可以跳转到无权限页面
    return
  }

  next()
})

export default router