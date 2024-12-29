<script setup lang="ts">

import { onMounted } from 'vue'
import { useAuthStore } from '@/shared/stores/authStore.ts'
import router from '@/router'

const authStore = useAuthStore();
onMounted(()=>{
  if(!authStore.isAuthorized){
    router.push({name: 'auth'});
  }
})

</script>

<template>
<div class="manager-view-wrapper">
<div class="manager-view__side-panel">
  <RouterLink class="menu-item" to="/management/products">Продукты</RouterLink>
  <RouterLink class="menu-item" to="/management/orders">Заказы</RouterLink>
</div>
<div class="manager-view__content">
  <RouterView ></RouterView>
</div>
</div>
</template>

<style scoped lang="scss">
.manager-view-wrapper{
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: row;
}
.manager-view__content{
  width: 100%;
}
.manager-view__side-panel{
  width: 200px;
  box-shadow: 0 0 15px $shadow;
  display: flex;
  flex-direction: column;
  gap: 20px;
  .menu-item{
    width: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    text-decoration: none;
    color: $color-base;
    &:hover{
      transform: scale(1.05);
    }
  }
}
</style>
