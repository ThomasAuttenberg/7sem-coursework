import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useLastOrderHashStore = defineStore('hash', () => {
  const hash = ref<string | null>(localStorage.getItem('lastOrderHash'));
  const setHash = (hash_: string | null) => {
    if(hash_)
      localStorage.setItem('lastOrderHash', hash_);
    hash.value = hash_;
  }
  return {hash, setHash}
});
