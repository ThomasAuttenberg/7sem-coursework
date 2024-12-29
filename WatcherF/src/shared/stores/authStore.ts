import { computed, ref } from 'vue'
import { defineStore } from 'pinia'
import type { UserData } from '@/shared/repositories/types/user.ts'

export const useAuthStore = defineStore('auth', () => {
  const token = ref<null | string>(localStorage.getItem('authToken'));
  const userData = ref<UserData|null>(
    localStorage.getItem('userData')? JSON.parse(localStorage.getItem('userData') as string) as UserData : null
  );
  const setUserData = (user: UserData): void => {
    userData.value = user;
    if(user == null) {
      localStorage.removeItem('userData');
    }else{
      localStorage.setItem('userData', JSON.stringify(user));
    }
  }
  const removeToken = () => {
    localStorage.removeItem('authToken');
    localStorage.removeItem('userData');
    userData.value = null;
    token.value = null;
  }

  const setToken = (newToken:string) => {
    localStorage.setItem('authToken', newToken);
  };
  const isAuthorized = computed(() => token.value !== null);

  return { token, setToken, isAuthorized, setUserData, userData, removeToken };
});
