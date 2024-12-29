<script setup lang="ts">

import InputBase from '@/shared/ui/Input/InputBase.vue'
import { ref } from 'vue'
import { getLoginMask } from '@/shared/utils/masks.ts'
import { UserRepository } from '@/shared/repositories/UserRepository.ts'
import NotificationSlider from '@/shared/ui/NotificationSlider.vue'
import { useAuthStore } from '@/shared/stores/authStore.ts'
import router from '@/router'

const loginModel = ref('');
const passwordModel = ref('');
const errors = ref<{ loginModel?: string, passwordModel?:string }>({});
const loading = ref<boolean>(false);
const authError = ref<boolean>(false);
const validate = (): boolean => {
  const newErrors: typeof errors.value = {};

  if (!loginModel.value.trim()) {
    newErrors.loginModel = 'Пожалуйста, введите код заказа.';
  }
  if (!passwordModel.value.trim()) {
    newErrors.passwordModel = 'Пожалуйста, введите код заказа.';
  }

  errors.value = newErrors;
  console.log("validation result:",errors);
  return Object.keys(newErrors).length === 0;
};

const handleAuthButtonClick = () => {
  if (!validate()) {
    return;
  }
  loading.value = true;
  UserRepository.auth({username:loginModel.value, password:passwordModel.value}).then((res) => {
    useAuthStore().setToken(res.data.token);
    router.push({ name: 'management' });
  }).catch(()=>{
    authError.value = true;
  }).finally(()=>{
    loading.value = false;
  })
}

</script>

<template>
  <div class="auth-view-wrapper">
    <NotificationSlider v-model:visible="authError" :duration="2000">
      <template v-slot:default>
        Авторизация не пройдена
      </template>
    </NotificationSlider>
    <div class="auth-view">
      <form @submit.prevent="handleAuthButtonClick">
        <div class="form-grid">
          <span class="field-title">
          Логин:
        </span>
          <InputBase
            v-model:model-value="loginModel"
            :mask="getLoginMask()"
            required
            clearable
          />
          <span class="field-title">
          Пароль:
        </span>
          <InputBase
            type="password"
            v-model:model-value="passwordModel"
            required
            clearable
          />
        </div>
        <button :disabled="loading" type="submit" class="button">
          Авторизация
        </button>
      </form>
    </div>
  </div>
</template>

<style scoped lang="scss">
.auth-view-wrapper{
  height: 100%;
}
.auth-view{

  padding: 40px;
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  box-shadow: 0 0 15px $shadow;
  .form-grid{
    display: grid;
    grid-template-columns: 80px 200px;
    align-items: center;
  }
  .in-row{
    display: flex;
    gap: 15px;
    align-items: center;
  }
  .button{
    width: 100%;
    height: 45px;
    display: flex;
    justify-content: center;
    align-items: center;
    color: $color-background;
    background: $color-base;
    border-radius: 50px;
    &:hover{
      cursor: pointer;
      transform: scale(1.02);
    }
  }
}
</style>
