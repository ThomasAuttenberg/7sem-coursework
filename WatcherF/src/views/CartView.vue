<template>
  <div class="cart-page">
    <!-- Кнопка возврата -->
    <button class="back-button" @click="goBack">
      ← Назад
    </button>

    <!-- Загрузчик -->
    <div v-if="isLoading" class="loader-container">
      <PrettyLoader />
    </div>

    <!-- Сообщение об ошибке -->
    <div v-if="error" class="error-message">
      {{ error }}
    </div>

    <!-- Содержимое корзины -->
    <div v-if="!isLoading && !error" class="cart-content">
      <h1 class="cart-title">Ваша корзина</h1>

      <!-- Проверка на пустую корзину -->
      <div v-if="cartItems.length === 0" class="empty-cart">
        <p>Ваша корзина пуста.</p>
      </div>

      <!-- Список товаров в корзине -->
      <div v-else class="cart-items">
        <div v-for="item in cartItems" :key="item.product.id" class="cart-item">
          <div class="item-image">
            <img width="100%" height="100%" :src="baseUrl+'/static/image/'+item.product.pictureUrl" :alt="item.product.name" />
          </div>
          <div class="item-details">
            <h2 class="item-name" @click="router.push({name:'product',params:{id:item.product.id}})">{{ item.product.name }}</h2>
            <p class="item-description">{{ item.product.description }}</p>
            <p class="item-price">Цена: ${{ item.product.price }}</p>
            <p class="item-stock" v-if="item.product.stockQuantity > 0">
              В наличии: {{ item.product.stockQuantity }}
            </p>
            <p class="item-stock" v-else>
              Нет в наличии
            </p>
          </div>
          <div class="item-actions">
            <label for="quantity" class="quantity-label">Количество:</label>
            <input
              type="number"
              step="1"
              id="quantity"
              v-model.number="item.quantity"
              @change="updateQuantity(item.product.id, item.quantity)"
              min="1"
              :max="item.product.stockQuantity"
              class="quantity-input"
            />
            <button @click="removeFromCart(item.product.id)" class="remove-button">
              <i class="pi pi-trash"></i>
            </button>
          </div>
        </div>
      </div>

      <!-- Общая сумма и оформление заказа -->
      <div v-if="cartItems.length > 0" class="cart-summary">
        <p class="total">Итого: ${{ totalPrice }}</p>
        <button @click="checkout" class="checkout-button">
          Оформить заказ
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
const baseUrl : string = import.meta.env.VITE_API_BASE;
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import { useCartStore } from '@/shared/stores/cartStore.ts';
import { ProductRepository } from '@/shared/repositories/ProductRepository.ts';
import PrettyLoader from '@/shared/ui/loaders/PrettyLoader.vue';
import type { ProductFull } from '@/shared/repositories/types/product.ts';

// Тип для комбинированного элемента корзины
interface CartProduct {
  product: ProductFull;
  quantity: number;
}

// Состояния
const isLoading = ref<boolean>(true);
const error = ref<string | null>(null);

// Хранилище продуктов для быстрого доступа по productId
const productsMap = ref<Record<number, ProductFull>>({});

// Вспомогательная функция для конвертации рублей в копейки
const toCents = (value: number): number => {
  return Math.round(value * 100);
};

// Подключение к магазину корзины
const cartStore = useCartStore();
const router = useRouter();

// Функция для возврата на предыдущую страницу
const goBack = () => {
  router.back();
};

// Функция для загрузки данных продуктов из корзины
const loadCartProducts = async () => {
  try {
    isLoading.value = true;

    // Если корзина пуста, очищаем productsMap
    if (cartStore.cart.length === 0) {
      productsMap.value = {};
      return;
    }

    // Асинхронно загружаем данные каждого продукта и сохраняем в productsMap
    await Promise.all(
      cartStore.cart.map(async (cartItem) => {
        const response = await ProductRepository.getProduct(cartItem.productId);
        if (response.data) {
          productsMap.value[cartItem.productId] = response.data;
        } else {
          console.warn(`Продукт с ID ${cartItem.productId} не найден.`);
        }
      })
    );
  } catch (err) {
    console.error(err);
    error.value = 'Ошибка при загрузке корзины.';
  } finally {
    isLoading.value = false;
  }
};

// Вызываем загрузку данных при монтировании компонента
onMounted(() => {
  loadCartProducts();
});

// Вычисляемое свойство для получения актуальных элементов корзины
const cartItems = computed(() => {
  return cartStore.cart.map((cartItem) => {
    const product = productsMap.value[cartItem.productId];
    if (product) {
      return {
        product,
        quantity: cartItem.quantity,
      };
    } else {
      return null;
    }
  }).filter(item => item !== null) as Array<CartProduct>;
});

// Функция для обновления количества товара
const updateQuantity = (productId: number, quantity: number) => {
  if (quantity < 1 || Math.floor(quantity) != quantity) {
    // Минимальное количество - 1, можно также добавить валидацию или установить на 1
    console.log("error. Reseting")
    updateQuantity(productId, 1);
    return;
  }
  const currentQuantity = cartStore.cart.find(ci => ci.productId === productId)?.quantity || 0;
  const difference = quantity - currentQuantity;

  cartStore.updateCartItemQuantity(productId, difference);
};

// Функция для удаления товара из корзины
const removeFromCart = (productId: number) => {
  cartStore.removeCartItem(productId);
};

// Вычисляемая сумма всех товаров в корзине
const totalPrice = computed(() => {
  const totalCents = cartItems.value.reduce((total, item) => {
    return total + toCents(item.product.price) * item.quantity;
  }, 0);

  // Конвертация обратно в рубли и форматирование до двух знаков после запятой
  return (totalCents / 100).toFixed(2);
});

// Функция для оформления заказа
const checkout = () => {
  router.push({name:'createOrder'});
  console.log('Оформление заказа...');
};
</script>


<style scoped lang="scss">

.cart-page {
  padding: 40px;
  background-color: $color-background;
  display: flex;
  gap: 50px;
  flex-direction: column;
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

  .cart-content {
    width: 100%;
    background-color: $color-background;
    box-shadow: 0 4px 15px $shadow;
    border-radius: 15px;
    padding: 30px;
    display: flex;
    flex-direction: column;
    gap: 30px;
    max-height: 70vh;

    .cart-title {
      font-size: 32px;
      color: $color-base;
      margin: 0;
      text-align: center;
    }

    .empty-cart {
      text-align: center;
      font-size: 18px;
      color: $color-text;
    }

    .cart-items {
      display: flex;
      overflow-y: auto;
      flex-direction: column;
      gap: 20px;

      .cart-item {
        display: flex;
        flex-direction: row;
        gap: 20px;
        align-items: center;
        border-bottom: 1px solid #eee;
        padding-bottom: 20px;

        .item-image {
          width: 150px;
          height: 150px;
          flex: 0 0 150px;
          background: #ffff;
          border-radius: 20px;
          img {
            width: 100%;
            height: 150px;
            object-fit: contain;
            border-radius: 10px;
          }
        }

        .item-details {
          flex: 1;
          display: flex;
          flex-direction: column;
          gap: 10px;

          .item-name {
            font-size: 24px;
            color: $color-base;
            margin: 0;
            &:hover{
              margin-bottom: -2px;
              cursor: pointer;
              border-bottom: 2px solid $color-light;
            }
          }

          .item-description {
            font-size: 16px;
            color: $color-text;
          }

          .item-price {
            font-size: 18px;
            color: $color-light;
          }

          .item-stock {
            font-size: 16px;
            color: $color-text;
          }
        }

        .item-actions {
          display: flex;
          gap: 10px;
          align-items: center;

          .quantity-label {
            font-size: 14px;
            color: $color-text;
          }

          .quantity-input {
            width: 60px;
            padding: 5px;
            border: 1px solid #ccc;
            border-radius: 5px;
            text-align: center;
            font-size: 16px;
          }

          .remove-button {
            background-color: $color-base;
            color: #fff;
            border: none;
            padding: 8px 12px;
            border-radius: 5px;
            cursor: pointer;
            display: flex;
            align-items: center;
            gap: 5px;
            transition: background-color 0.3s ease;

            &:hover {
              background-color: $color-light;
            }

            i {
              font-size: 16px;
            }
          }
        }
      }
    }

    .cart-summary {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding-top: 20px;
      border-top: 2px solid #eee;

      .total {
        font-size: 24px;
        font-weight: bold;
        color: $color-text;
      }

      .checkout-button {
        background-color: $color-base;
        color: $color-background;
        border: none;
        padding: 12px 24px;
        border-radius: 8px;
        cursor: pointer;
        font-size: 16px;
        display: flex;
        align-items: center;
        gap: 8px;
        transition: background-color 0.3s ease;

        &:hover {
          background-color: $color-light;
        }
      }
    }
  }
}

/* Адаптивность */
@media (max-width: 768px) {
  .cart-page {
    .cart-content {
      padding: 20px;

      .cart-items {
        .cart-item {
          flex-direction: column;
          align-items: center;

          .item-image {
            flex: none;
            width: 100%;
            max-width: 300px;
            height: auto;

            img {
              height: auto;
            }
          }

          .item-details {
            align-items: center;
            text-align: center;
          }

          .item-actions {
            gap: 20px;
          }
        }
      }

      .cart-summary {
        flex-direction: column;
        gap: 20px;

        .checkout-button {
          width: 100%;
          justify-content: center;
        }
      }
    }
  }
}
</style>
