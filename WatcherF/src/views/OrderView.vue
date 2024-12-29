<!-- src/pages/CheckoutPage.vue -->
<template>
  <div class="checkout-page">
    <h1 class="page-title">Просмотр вашего заказа</h1>
    <p class="description">
      Введите код заказа, полученный при оформлении, чтобы просмотреть детали вашего заказа.
    </p>

    <form @submit.prevent="handleSubmit" class="checkout-form">
      <InputBase
        v-model="accessHash"
        label="Код заказа"
        placeholder="Введите код заказа"
        :error="errors.accessHash"
        required
        clearable
      />
      <button type="submit" class="submit-button">Просмотреть заказ</button>
    </form>

    <div v-if="loading" class="loader-container">
      <PrettyLoader />
    </div>

    <div v-if="errorMessage" class="error-message">
      {{ errorMessage }}
    </div>

    <OrderDetails v-if="order" :order="order" />
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import InputBase from '@/shared/ui/Input/InputBase.vue';
import OrderDetails from '@/entities/Order/OrderDetails.vue';
import PrettyLoader from '@/shared/ui/loaders/PrettyLoader.vue';
import { OrderRepository } from '@/shared/repositories/OrderRepository.ts';
import type { Order } from '@/shared/repositories/types/order.ts';
import { useLastViewedOrderStore } from '@/shared/stores/orderViewStore.ts'

// Реактивные переменные
const accessHash = ref('');
const loading = ref(false);
const order = ref<Order | null>(null);
const errors = ref<{ accessHash?: string }>({});
const errorMessage = ref('');

// Функция валидации
const validate = (): boolean => {
  const newErrors: typeof errors.value = {};

  if (!accessHash.value.trim()) {
    newErrors.accessHash = 'Пожалуйста, введите код заказа.';
  }

  errors.value = newErrors;

  return Object.keys(newErrors).length === 0;
};

onMounted(()=>{
  const hash = useLastViewedOrderStore().hash;
  if(hash){
    accessHash.value = hash;
    handleSubmit();
  }
})

// Функция обработки отправки формы
const handleSubmit = async () => {
  if (!validate()) {
    return;
  }

  loading.value = true;
  errorMessage.value = '';
  order.value = null;

  try {
    const fetchedOrder = await OrderRepository.getOrder(accessHash.value.trim());
    useLastViewedOrderStore().setHash(accessHash.value);
    order.value = fetchedOrder.data;
  } catch (error) {
    console.error('Ошибка при получении заказа:', error);
    errorMessage.value = 'Не удалось найти заказ с таким кодом. Проверьте код и попробуйте снова.';
  } finally {
    loading.value = false;
  }
};
</script>

<style scoped lang="scss">
.checkout-page {
  max-width: 600px;
  margin: 0 auto;
  padding: 40px 20px;
  background-color: $color-background;
  border-radius: 15px;

  .page-title {
    text-align: center;
    font-size: 32px;
    color: #545173;
    margin-bottom: 20px;
  }

  .description {
    text-align: center;
    font-size: 16px;
    color: #555;
    margin-bottom: 30px;
  }

  .checkout-form {
    display: flex;
    flex-direction: column;
    gap: 20px;
    width: 100%;
    max-width: 800px;
    justify-self: center;

    .submit-button {
      padding: 12px 20px;
      background-color: #545173;
      color: #fff;
      border: none;
      border-radius: 8px;
      font-size: 16px;
      cursor: pointer;
      transition: background-color 0.3s ease;
      align-self: flex-end;
      width: 100%;
      max-width: 200px;

      &:hover {
        background-color: #9b97c8;
      }

      &:disabled {
        background-color: #ccc;
        cursor: not-allowed;
      }
    }
  }

  .loader-container {
    display: flex;
    justify-content: center;
    margin-top: 20px;
  }

  .error-message {
    margin-top: 20px;
    color: #e74c3c;
    text-align: center;
    font-size: 16px;
  }
}
</style>
