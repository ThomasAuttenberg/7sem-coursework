import { createRouter, createWebHistory } from 'vue-router'
import ShopView from '@/views/ShopView.vue'
import ProductView from '@/views/ProductView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: ShopView,
    },
    {
      path: '/product/:id',
      name: 'product',
      component: ProductView
    },
    {
      path: '/order',
      name: 'order',
      // route level code-splitting
      // this generates a separate chunk (About.[hash].js) for this route
      // which is lazy-loaded when the route is visited.
      component: () => import('../views/OrderView.vue'),
    },
    {
      path: '/cart',
      name: 'cart',
      component: () => import('../views/CartView.vue'),
    },
    {
      path: '/createOrder/:hash?',
      name: 'createOrder',
      component: () => import('../views/CreateOrderView.vue')
    },
    {
      path: '/management',
      name: 'management',
      children:[
        {
          path: '/management/products',
          name: 'management_products',
          component: () => import('../views/Management/ProductsMView.vue')
        },
        {
          path: '/management/orders',
          name: 'management_orders',
          component: () => import('../views/Management/OrdersMView.vue')
        }
      ],
      component: () => import('../views/ManagerView.vue'),
    },
    {
      path: '/auth',
      name: 'auth',
      component: () => import('../views/AuthView.vue')
    }
  ],
})

export default router
