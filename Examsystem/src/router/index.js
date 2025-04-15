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
      component: () => import('../views/longin.vue')
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('../views/Register.vue')
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

  if (to.meta.requiresAuth && !isAuthenticated) {
    next('/login')
  } else if (to.meta.requiresAdmin && !isAdmin) {
    next('/404') // 或者可以跳转到无权限页面
  } else {
    next()
  }
})

export default router