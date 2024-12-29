<!-- src/components/OrderDetails.vue -->
<template>
  <div class="order-details">
    <h2 class="section-title">Ваш заказ</h2>

    <!-- Основная информация о заказе -->
    <div class="order-info">
      <div class="info-row">
        <span class="info-label">ID заказа:</span>
        <span class="info-value">{{ order.id }}</span>
      </div>
      <div class="info-row">
        <span class="info-label">Статус:</span>
        <span :class="['info-value', statusClass]">{{ order.status }}</span>
      </div>
      <div class="info-row">
        <span class="info-label">Дата создания:</span>
        <span class="info-value">{{ formattedCreatedAt }}</span>
      </div>
      <div class="info-row" v-if="order.confirmedAt">
        <span class="info-label">Подтверждено:</span>
        <span class="info-value">{{ formattedConfirmedAt }}</span>
      </div>
      <div class="info-row" v-if="order.completedAt">
        <span class="info-label">Завершено:</span>
        <span class="info-value">{{ formattedCompletedAt }}</span>
      </div>
      <div class="info-row" v-if="order.expectedDeliveryAt">
        <span class="info-label">Ожидаемая доставка:</span>
        <span class="info-value">{{ formattedExpectedDeliveryAt }}</span>
      </div>
      <div class="info-row">
        <span class="info-label">Адрес доставки:</span>
        <span class="info-value">{{ order.deliveryAddress || 'Не указан' }}</span>
      </div>
      <div class="info-row">
        <span class="info-label">Контактный телефон:</span>
        <span class="info-value">{{ order.contactPhone }}</span>
      </div>
    </div>

    <!-- Список товаров в заказе -->
    <div class="order-items">
      <h3>Товары в заказе</h3>
      <table class="items-table">
        <thead>
        <tr>
          <th>Название продукта</th>
          <th>Количество</th>
        </tr>
        </thead>
        <tbody>
        <tr @click="router.push({name:'product',params:{id:item.productId}})" v-for="item in order.items" :key="item.productId">
          <td>{{ getProductName(item.productId) }}</td>
          <td>{{ item.quantity }}</td>
        </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { Order } from '@/shared/repositories/types/order.ts';
import { computed, reactive } from 'vue'
import { ProductRepository } from '@/shared/repositories/ProductRepository.ts';
import router from '@/router'

// Пропс с типом Order
const props = defineProps<{
  order: Order;
}>();

const { order } = props;

// Форматирование дат
const formattedCreatedAt = computed(() => new Date(order.createdAt).toLocaleString('ru-RU'));
const formattedConfirmedAt = computed(() => order.confirmedAt ? new Date(order.confirmedAt).toLocaleString('ru-RU') : '');
const formattedCompletedAt = computed(() => order.completedAt ? new Date(order.completedAt).toLocaleString('ru-RU') : '');
const formattedExpectedDeliveryAt = computed(() => order.expectedDeliveryAt ? new Date(order.expectedDeliveryAt).toLocaleString('ru-RU') : '');

// Реактивное хранилище названий продуктов
const productsMap = reactive<Record<number, string>>({});

// Функция для получения названия продукта по ID

const getProductName = (productId: number) => {
  if (productsMap[productId]) {
    return productsMap[productId];
  }
  try {
    productsMap[productId] = 'Загрузка...'
    ProductRepository.getProduct(productId).then((res)=>{
      productsMap[productId] = res.data.name;
    });

  } catch (error) {
    console.log(error);
    productsMap[productId] = 'Неизвестный продукт';
  }
  return productsMap[productId];
};

// Вычисляемый класс для статуса заказа
const statusClass = computed(() => {
  switch (order.status.toLowerCase()) {
    case 'создан':
      return 'status-created';
    case 'подтвержден':
      return 'status-confirmed';
    case 'завершен':
      return 'status-completed';
    default:
      return 'status-other';
  }
});
</script>

<style scoped lang="scss">
.order-details {
  padding: 30px;
  background-color: $color-background;
  border-radius: 15px;
  box-shadow: 0 0 15px rgba(84, 81, 115, 0.4);
  max-width: 900px;
  margin: 20px auto;

  .section-title {
    text-align: center;
    font-size: 28px;
    color: #545173;
    margin-bottom: 25px;
  }

  .order-info {
    display: flex;
    flex-wrap: wrap;
    gap: 15px;

    .info-row {
      flex: 1 1 45%;
      display: flex;
      align-items: center;

      .info-label {
        font-weight: bold;
        color: #545173;
        width: 150px;
      }

      .info-value {
        color: #333;
      }

      .status-created {
        color: #5d6e78; /* Синий для статуса "Создан" */
      }

      .status-confirmed {
        color: #3498db; /* Желтый для статуса "Подтверждён" */
      }

      .status-completed {
        color: #2ecc71; /* Зеленый для статуса "Завершён" */
      }

      .status-other {
        color: #e74c3c; /* Красный для остальных статусов */
      }
    }
  }

  .order-items {
    margin-top: 30px;

    h3 {
      font-size: 22px;
      color: #545173;
      margin-bottom: 15px;
    }

    .items-table {
      width: 100%;
      border-collapse: collapse;

      th, td {
        padding: 12px 15px;
        text-align: left;
        border-bottom: 1px solid #eee;
      }

      th {
        background-color: #f5f5f5;
        color: #545173;
      }

      tr:hover {
        background-color: #fafafa;
        cursor: pointer;
      }

      @media (max-width: 600px) {
        th, td {
          padding: 8px 10px;
        }
      }
    }
  }
}
</style>
