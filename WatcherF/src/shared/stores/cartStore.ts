import { defineStore } from 'pinia';
import { ref, watch } from 'vue';
import type { CartItem } from '@/shared/types/cart.ts';

export const useCartStore = defineStore('cart', () => {
  // Состояние корзины
  const cart = ref<Array<CartItem>>([]);

  /**
   * Загрузка корзины из localStorage
   */
  const loadCart = () => {
    const cartData = localStorage.getItem('cart');
    if (cartData) {
      try {
        cart.value = JSON.parse(cartData);
      } catch (error) {
        console.error('Не удалось разобрать данные корзины из localStorage:', error);
        cart.value = [];
      }
    }
  };

  /**
   * Сохранение корзины в localStorage при изменении
   */
  watch(
    cart,
    (newCart) => {
      localStorage.setItem('cart', JSON.stringify(newCart));
    },
    { deep: true } // Отслеживать изменения внутри объектов
  );

  // Инициализация корзины при монтировании магазина
  loadCart();

  /**
   * Добавление элемента в корзину
   * Если элемент с таким productId уже существует, увеличиваем количество
   * Иначе добавляем новый элемент
   * @param item - CartItem для добавления
   */
  const addCartItem = (item: CartItem) => {
    const existingItem = cart.value.find((ci) => ci.productId === item.productId);
    if (existingItem) {
      existingItem.quantity += item.quantity;
    } else {
      cart.value.push({ ...item });
    }
  };

  /**
   * Удаление элемента из корзины по productId
   * @param productId - ID продукта для удаления
   */
  const removeCartItem = (productId: number) => {
    const index = cart.value.findIndex((item) => item.productId === productId);
    if (index !== -1) {
      cart.value.splice(index, 1);
    }
  };

  const updateCartItemQuantity = (productId: number, quantity: number) => {
    const existingItem = cart.value.find((ci) => ci.productId === productId);
    if (existingItem) {
      existingItem.quantity += quantity;
      if (existingItem.quantity <= 0) {
        removeCartItem(productId);
      }
    }
  };

  /**
   * Очистка всей корзины
   */
  const clearCart = () => {
    cart.value = [];
  };

  return { cart, addCartItem, removeCartItem, clearCart, updateCartItemQuantity };
});
