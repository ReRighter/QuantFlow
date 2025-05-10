import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: () => import('../views/Home.vue')
    },
    {
      path: '/education',
      name: 'education',
      component: () => import('@/views/education/EducationHome.vue'),
      children: [
        {
          name: 'default',
          path: '',
          redirect: '/education/courses'
        },
        {
          path: 'courses',
          component: () => import('@/views/education/CourseList.vue')
        },
        {
          path: 'courses/:id',
          component: () => import('@/views/education/CourseDetail.vue')
        },
        {
          path: 'ebooks',
          component: () => import('@/views/education/BookList.vue')
        }
        // ,
        // {
        //   path: 'cases',
        //   component: () => import('../views/education/CaseList.vue')
        // }
      ]
    },
    {
      path: '/strategy',
      name: 'strategy',
      component: () => import('../views/strategy/StrategyHome.vue'),
    },
    {
      name: 'result',
      path: '/resultlist',
      component: () => import('@/views/strategy/BacktestResultList.vue')
    }
    ,
    {
      path: '/trading',
      name: 'trading',
      component: () => import('../views/trading/SimulationHome.vue')
    },
    {
      path: '/admin',
      name: 'admin',  
      meta: { requiresAdmin: true },
      children: [
        {
          path: 'courses',
          component: () => import('@/views/admin/CourseManagement.vue')
        },
        // {
        //   path: 'courses/:id/lessons',
        //   component: () => import('@/views/admin/LessonManagement.vue')
        // }
      ]
    }
  ]
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  
  if (to.meta.requiresAdmin && (!userStore.userInfo?.role || userStore.userInfo.role !== 'admin')) {
    next('/')
  } else {
    next()
  }
})

export default router