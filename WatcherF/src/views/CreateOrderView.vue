<template>
  <div class="checkout-page-wrapper">
    <button class="back-button" @click="goBack">
      ← Назад
    </button>
    <NotificationSlider v-model:visible="orderCreatingError" :duration="3000">
      <template v-slot:default>
        Произошла ошибка при оформлении заказа! Проверьте, что в корзине есть товары или попробуйте позже.
      </template>
      <template v-slot:header>
        Ошибка оформления
      </template>
    </NotificationSlider>
    <Transition>
    <div v-if="hashValue && cartItems.length == 0" class="checkout-page">
      <div class="checkout-success">
        <h1 class="page-title">Заказ успешно оформлен!</h1>
        <div class="warning">
          Пожалуйста! Скопируйте и сохраните секретный код ниже.<br/><br/> Благодаря нему можно отследить ваш заказ на вкладке <a target="_blank" :href="`/order`">ЗАКАЗ</a>
        </div>
        <div class="to-copy-area">
          <span>Секретный код:</span>
          <CopyLabel class="to-copy" :text="hashValue"/>
        </div>
      </div>
    </div>
    <div v-else class="checkout-page">
      <h1 class="page-title">Оформление заказа</h1>

      <form @submit.prevent="handleSubmit" class="checkout-form">
        <!-- Контактное Имя -->
        <InputBase
          v-model="contactName"
          label="Ваше имя"
          placeholder="Введите ваше имя"
          :error="errors.contactName"
          :mask="getCISCityMask()"
          clearable
          required
        />

        <!-- Контактный Телефон -->
        <InputBase
          v-model="contactPhone"
          label="Контактный телефон"
          placeholder="Введите ваш телефон"
          :error="errors.contactPhone"
          :mask="getPhoneMask()"
          clearable
          required
        />

        <!-- Адрес Доставки -->
        <InputBase
          v-model="deliveryAddress"
          label="Адрес доставки"
          placeholder="Введите адрес доставки"
          :error="errors.deliveryAddress"
          clearable
          required
        />

        <!-- Кнопка Отправки Заказа -->
        <button type="submit" class="submit-button">Оформить заказ</button>
      </form>
    </div>
    </Transition>
  </div>

</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useCartStore } from '@/shared/stores/cartStore.ts';
import { OrderRepository } from '@/shared/repositories/OrderRepository.ts';
import InputBase from '@/shared/ui/Input/InputBase.vue'
import { getCISCityMask, getPhoneMask } from '@/shared/utils/masks.ts'
import router from '@/router'
import type { Order } from '@/shared/repositories/types/order.ts'
import CopyLabel from '@/shared/ui/Output/CopyLabel.vue'
import NotificationSlider from '@/shared/ui/NotificationSlider.vue'
import { useLastOrderHashStore } from '@/shared/stores/lastOrderHashStore.ts'


const goBack = ()=>{
  router.back();
}


// Получение данных из стора корзины
const cartStore = useCartStore();
const cartItems = computed(() => cartStore.cart.map(cartItem => ({
  product: cartItem.productId,
  quantity: cartItem.quantity
})));


// Реактивные переменные для формы
const contactName = ref('');
const contactPhone = ref('');
const deliveryAddress = ref('');

// Реактивный объект для ошибок
const errors = ref<{
  contactName?: string;
  contactPhone?: string;
  deliveryAddress?: string;
}>({});

// Функция валидации формы
const validateForm = (): boolean => {
  const newErrors: typeof errors.value = {};

  if (!contactName.value.trim()) {
    newErrors.contactName = 'Пожалуйста, введите ваше имя.';
  }

  const phoneRegex = /^\+7\(\d{3}\)\d{3}-\d{2}-\d{2}$/;
  if (!phoneRegex.test(contactPhone.value)) {
    newErrors.contactPhone = 'Пожалуйста, введите корректный телефон.';
  }

  if (!deliveryAddress.value.trim()) {
    newErrors.deliveryAddress = 'Пожалуйста, введите адрес доставки.';
  }

  errors.value = newErrors;

  return Object.keys(newErrors).length === 0;
};
const orderCreatingError = ref(false);
const createdOrder = ref<Order | null>(null);
const hashValue = ref<string | null>(useLastOrderHashStore().hash);

const handleSubmit = async () => {
  if (!validateForm()) {
    return;
  }

  // Подготовка данных для отправки
  const orderRequest = {
    deliveryAddress: deliveryAddress.value,
    contactPhone: contactPhone.value,
    contactName: contactName.value,
    items: cartStore.cart.map(item => ({
      productId: item.productId,
      quantity: item.quantity
    }))
  };

  try {
    // Отправка запроса к API
    createdOrder.value = (await OrderRepository.createOrder(orderRequest)).data;
    console.log('Заказ успешно оформлен:', createdOrder.value);
    // Очистка корзины после успешного заказа
    cartStore.clearCart();
    // Очистка формы
    contactName.value = '';
    contactPhone.value = '';
    deliveryAddress.value = '';
    errors.value = {};
    hashValue.value = createdOrder.value.accessHash;
    useLastOrderHashStore().setHash(hashValue.value);

  } catch (error) {
    console.error('Ошибка при оформлении заказа:', error);
    orderCreatingError.value = true;
  }
};
</script>

<style scoped lang="scss">
.checkout-page-wrapper{
  padding: 40px;
  display: flex;
  flex-direction: column;
  gap: 50px;
}
.back-button {
  align-self: flex-start;
  background: $color-base;
  color: $color-background;
  border: none;
  padding: 10px 20px;
  border-radius: 8px;
  cursor: pointer;
  font-size: 16px;
  display: flex;
  align-items: center;
  transition: background-color 0.3s ease;

  &:hover {
    background: $color-light;
  }

  i {
    margin-right: 8px;
  }
}
.checkout-page {
  width: 100%;
  box-shadow: 0 4px 15px $shadow;
  margin: 0 auto;
  padding: 40px;
  background-color: $color-background;
  border-radius: 15px;

  .checkout-success{
    display: flex;
    flex-direction: column;
    gap: 30px;
  }
  .warning{
    box-shadow: 0 0 8px 0 $shadow inset;
    padding: 20px;
    color: $color-text;
    a{
      color: $color-base;
      text-decoration: none;
    }
  }
  .to-copy-area{
    display: flex;
    align-items: center;
    gap: 15px;
    width: fit-content;
  }
  .page-title {
    text-align: center;
    font-size: 32px;
    color: #545173;
    margin-bottom: 30px;
  }

  .checkout-form {
    display: flex;
    flex-direction: column;
    gap: 20px;

    .cart-summary {
      padding: 20px;
      border-radius: 10px;
      margin-top: 20px;

      h2 {
        font-size: 20px;
        color: #545173;
        margin-bottom: 10px;
      }

      .items-list {
        list-style: none;
        padding: 0;
        margin: 0;

        .item {
          display: flex;
          justify-content: space-between;
          padding: 10px 0;
          border-bottom: 1px solid #eee;

          &:last-child {
            border-bottom: none;
          }

          .item-name {
            font-weight: bold;
            color: #333;
          }

          .item-quantity,
          .item-price {
            color: #555;
          }
        }
      }

      .total {
        text-align: right;
        font-size: 18px;
        font-weight: bold;
        margin-top: 10px;
        color: #333;
      }
    }

    .submit-button {
      padding: 12px 20px;
      background-color: #545173;
      color: #fff;
      border: none;
      border-radius: 8px;
      font-size: 16px;
      cursor: pointer;
      transition: background-color 0.3s ease;
      margin-top: 20px;

      &:hover {
        background-color: #9b97c8;
      }

      &:disabled {
        background-color: #ccc;
        cursor: not-allowed;
      }
    }
  }
}
</style>
