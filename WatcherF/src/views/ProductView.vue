<template>
  <div class="product-page">
    <!-- Кнопка возврата -->
    <div style=" width: 100%">
      <button class="back-button" @click="goBack">
        ← Назад
      </button>
    </div>

    <!-- Загрузчик -->
    <div v-if="isLoading" class="loader-container">
      <PrettyLoader />
    </div>

    <!-- Ошибка -->
    <div v-if="error" class="error-message">
      {{ error }}
    </div>

    <!-- Детали продукта -->
    <div v-if="product && !isLoading && !error" class="product-details">
      <div class="product-image">
        <img :src="baseUrl + '/static/image/'+product.pictureUrl" :alt="product.name" />
      </div>
      <div class="product-info">
        <h1 class="product-name">{{ product.name }}</h1>
        <p class="product-description">{{ product.description }}</p>
        <p class="product-price">Цена: ${{ product.price }}</p>
        <p class="product-stock">В наличии: {{ product.stockQuantity }}</p>

        <div class="product-specs">
          <p v-if="product.country" class="spec"><span class="spec__title">Страна:</span> {{ product.country.name }}</p>
          <p v-if="product.brand" class="spec"><span class="spec__title">Бренд:</span> {{ product.brand.name }}</p>
          <p v-if="product.waterproof !== undefined" class="spec"><span class="spec__title">Водонепроницаемость:</span> {{ product.waterproof }} метров</p>
          <p v-if="product.glassType" class="spec"><span class="spec__title">Тип стекла:</span> {{ product.glassType.name }}</p>
          <p v-if="product.caseType" class="spec"><span class="spec__title">Тип корпуса:</span> {{ product.caseType.name }}</p>
          <p v-if="product.styleType" class="spec"><span class="spec__title">Стиль:</span> {{ product.styleType.name }}</p>
        </div>

        <!-- Кнопки для добавления/удаления из корзины -->
        <div class="cart-actions">
          <button v-if="inCart" @click="removeFromCart" class="cart-button remove">
            <i class="pi pi-minus"></i> Убрать из корзины ({{ inCart.quantity }})
          </button>
          <button @click="addToCart" class="cart-button add">
            <i class="pi pi-plus"></i> Добавить в корзину
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ProductRepository } from '@/shared/repositories/ProductRepository.ts';
import PrettyLoader from '@/shared/ui/loaders/PrettyLoader.vue';
import type { ProductFull } from '@/shared/repositories/types/product.ts';
import { useCartStore } from '@/shared/stores/cartStore.ts';

const baseUrl : string = import.meta.env.VITE_API_BASE;

// Получение маршрута и роутера
const route = useRoute();
const router = useRouter();

// Получение `productId` из параметров URL
const productId = parseInt(route.params.id as string, 10);

// Состояния
const product = ref<ProductFull | null>(null);
const isLoading = ref<boolean>(true);
const error = ref<string | null>(null);

// Подключение к магазину корзины
const cartStore = useCartStore();

// Вычисляемое свойство для проверки наличия продукта в корзине
const inCart = computed(() => cartStore.cart.find(item => item.productId === productId));

// Функция для возврата на предыдущую страницу
const goBack = () => {
  router.back();
};

// Функция для загрузки данных продукта
const fetchProduct = async () => {
  try {
    isLoading.value = true;
    // Формирование запроса (предполагается, что getCatalog может принимать фильтр по ID)
    const response = await ProductRepository.getProduct(productId);

    // Предполагается, что response.data содержит массив продуктов
    if (response.data && response.data) {
      product.value = response.data;
    } else {
      error.value = 'Продукт не найден.';
    }
  } catch (err) {
    console.error(err);
    error.value = 'Ошибка при загрузке продукта.';
  } finally {
    isLoading.value = false;
  }
};

const addToCart = () => {
  if(inCart.value){
    inCart.value.quantity++;
  }else{
    cartStore.addCartItem({productId: productId, quantity: 1});
  }
}
const removeFromCart = () => {
  if(inCart.value && inCart.value.quantity > 1){
    inCart.value.quantity--;
  }else{
    cartStore.removeCartItem(productId);
  }
}

// Загрузка данных при монтировании компонента
onMounted(() => {
  fetchProduct();
});
</script>

<style scoped lang="scss">
.product-page {
  padding: 40px;
  background-color: $color-background;
  display: flex;
  flex-direction: column;
  gap:50px;
  align-items: center;

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

  .loader-container {
    margin-top: 50px;
  }

  .error-message {
    margin-top: 50px;
    color: red;
    font-size: 18px;
  }

  .product-details {
    display: flex;
    flex-direction: row;
    background-color: #fff;
    box-shadow: 0 4px 6px $shadow;
    border-radius: 15px;
    padding: 30px;
    width: 100%;
    gap: 40px;

    .product-image {
      flex: 1;
      max-width: 500px;
      img {
        width: 100%;
        height: auto;
        border-radius: 10px;
        object-fit: cover;
      }
    }

    .product-info {
      flex: 2;
      display: flex;
      flex-direction: column;
      gap: 20px;

      .product-name {
        font-size: 32px;
        color: $color-base;
        margin: 0;
      }

      .product-description {
        font-size: 18px;
        color: $color-text;
      }

      .product-price {
        font-size: 24px;
        color: $color-light;
      }

      .product-stock {
        font-size: 18px;
        color: $color-text;
      }

      .product-specs {
        display: flex;
        flex-direction: column;
        gap: 10px;

        .spec {
          font-size: 16px;
          color: $color-text;
          &__title{
            font-weight: bold;
          }
        }
      }

      .cart-actions {
        margin-top: 30px;
        display: flex;
        gap: 15px;

        .cart-button {
          padding: 12px 24px;
          border: none;
          border-radius: 8px;
          cursor: pointer;
          font-size: 16px;
          display: flex;
          align-items: center;
          gap: 8px;
          transition: background-color 0.3s ease;

          &.add {
            background-color: $color-base;
            color: $color-background;

            &:hover {
              background-color: $color-light;
            }
          }

          &.remove {
            background-color: #e74c3c;
            color: #fff;

            &:hover {
              background-color: #c0392b;
            }
          }

          i {
            font-size: 18px;
          }
        }
      }
    }
  }
}

/* Анимация Transition */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.5s;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
