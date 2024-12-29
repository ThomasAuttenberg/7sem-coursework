<script setup lang="ts">


import { RouterLink } from 'vue-router'
import { useAuthStore } from '@/shared/stores/authStore'
import { ref, watch } from 'vue'
import router from '@/router'
import LogoAsset from '@/assets/icons/LogoAsset.vue'
import HeaderBase from '@/shared/ui/global/Header/HeaderBase.vue'

const authStore = useAuthStore();
const isAuthorized = ref<boolean>(authStore.isAuthorized);
watch(
  () => authStore.isAuthorized, // Это будет ComputedRef
  (newValue) => {
    isAuthorized.value = newValue;
  }
);
const logout = () => {
  authStore.removeToken()
  router.push("/");
}

</script>

<template>
  <HeaderBase>
    <template v-slot:logo>
      <div class="header-element__logo" @click="router.push({name: 'home'})">
        <LogoAsset style="width: 70px;"/>
        <span>WATCHer</span>
      </div>
    </template>
    <template v-slot:items>
      <nav>
        <div class="header-element__nav">
          <RouterLink class="header-element__nav__link" to="/"><i class="pi pi-book"></i>Каталог</RouterLink>
          <RouterLink class="header-element__nav__link" to="/order"><i class="pi pi-ticket"/> Заказ</RouterLink>
          <RouterLink class="header-element__nav__link" to="/cart"><i class="pi pi-shopping-cart"/> Корзина</RouterLink>
          <RouterLink v-if="isAuthorized" class="header-element__nav__link" to="/management"><i class="pi pi-file-edit"/> Менеджмент</RouterLink>
          <a v-if="isAuthorized" @click="logout"><i class="pi pi-sign-out"/> Выход</a>
        </div>
      </nav>
    </template>
  </HeaderBase>
</template>

<style scoped lang="scss">
.header-element{
  &__nav{
    display: flex;
    align-items: center;
    height: 100%;
    justify-content: right;
    gap: 20px;
    > a {
      cursor: pointer;
      text-decoration: none;
      color: $color-base;
      font-weight: 600;
    }
    > a.router-link-active{
      border-bottom: 2px solid $color-light;
      transition: border-bottom-color .5s;
    }
    &__link{
      display: flex;
      gap: 5px;
      align-items: center;
    }
  }
  &__logo{
    display: flex;
    font-weight: 900;
    font-size: 30px;
    align-items: center;
    justify-content: center;
    gap: 20px;
    color: $color-light;
    user-select: none;
    cursor: pointer;
  }
}
</style>
