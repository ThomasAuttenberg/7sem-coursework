import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useLastViewedOrderStore = defineStore('lastViewedOrder', () => {
  const hash = ref<string | null>(null);
  const setHash = (hash_: string | null) => {
    if(hash_)
    hash.value = hash_;
  }
  return {hash, setHash}
});
