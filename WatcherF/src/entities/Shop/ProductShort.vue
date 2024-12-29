<script setup lang="ts">

import type { ProductShort } from '@/shared/repositories/types/product.ts'
import { computed, type PropType, ref } from 'vue'
import { useCartStore } from '@/shared/stores/cartStore.ts'
import router from '@/router'
import NotificationSlider from '@/shared/ui/NotificationSlider.vue'

const props = defineProps({
  product: {type: Object as PropType<ProductShort>, required:true},
})
const baseUrl : string = import.meta.env.VITE_API_BASE;
const emit = defineEmits(['goToProduct'])

const emitProductTransition = ()=>{
  router.push({name:'product',params:{id:props.product?.id}})
}
const cartStore = useCartStore();
const inCart = computed(()=>cartStore.cart.find((item)=>item.productId == props.product.id))

const showOutOfStock = ref<boolean>(false);

const addToCart = () => {
  if(inCart.value){
    if(inCart.value.quantity + 1 > props.product?.stockQuantity){
      showOutOfStock.value = true;
    }else {
      inCart.value.quantity++;
    }
  }else{
    if(props.product?.stockQuantity <= 0){
      showOutOfStock.value = true;
    }else {
      cartStore.addCartItem({ productId: props.product.id, quantity: 1 });
    }
  }
}
const removeFromCart = () => {
  if(inCart.value && inCart.value.quantity > 1){
    inCart.value.quantity--;
  }else{
    cartStore.removeCartItem(props.product?.id);
  }
}

</script>

<template>
<div class="shop-ui-product-short">
  <NotificationSlider v-model:visible="showOutOfStock" :duration="3000">
    <template v-slot:header>
      Ошибка добавления в корзину
    </template>
    <template v-slot:default>
      Извините! Товара больше нет :(
    </template>
  </NotificationSlider>
  <div class="shop-ui-product-short__picture" @click="emitProductTransition">
    <img width="100%" height="100%" :src="baseUrl+'/static/image/'+product.pictureUrl"/>
  </div>
  <div class="shop-ui-product-short__main" @click="emitProductTransition">
    <span class="shop-ui-product-short__main__title">{{product.name}}</span>
    <span class="shop-ui-product-short__main__price">{{product.price}}</span>
  </div>
  <div class="shop-ui-product-short__button-container">
    <Transition name="fade">
      <div class="shop-ui-product-short__in-cart" v-if="inCart">
        <i class="pi pi-shopping-cart"/>: {{inCart.quantity}}
      </div>
    </Transition>
    <div/>
    <div class="shop-ui-product-short__buttons">
      <div v-if="inCart" @click="removeFromCart" class="shop-ui-product-short__button"><i class="pi pi-minus"/></div>
      <div @click="addToCart" class="shop-ui-product-short__button"><i class="pi pi-plus"/></div>
    </div>
  </div>
</div>
</template>

<style scoped lang="scss">
.fade-enter-active, .fade-leave-active {
  transition: opacity .5s;
}
.fade-enter-from, .fade-leave-to {
  opacity: 0;
}
.shop-ui-product-short {
  padding: 20px;
  box-shadow: 0 0 15px 0 $shadow;
  border-radius: 25px;
  flex: 1;
  min-width: 300px;
  display: flex;
  flex-direction: column;
  gap: 30px;
  &__button-container{
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  &__buttons{
    display: flex;
    gap: 5px;
  }
  &__button{
    width: 50px;
    height: 50px;
    background: $color-base;
    color: $color-background;
    border-radius: 15px;
    display: flex;
    justify-content: center;
    align-items: center;
    transition: background-color 0.3s ease;
    &:hover{
      cursor: pointer;
      background: $color-light;
    }
  }
  &__picture{
    width: 300px;
    height: 300px;
    border: 2px solid $color-base;
    background: #ffffff;
    border-radius: 25px;
    cursor: pointer;
    align-self: center;
    img{
      object-fit: contain;
    }
    &:hover{
      transform: scale(1.01);
    }
    img{
      border-radius: 25px;
    }
  }
  &__main{
    display: flex;
    justify-content: space-between;
    cursor: pointer;
    border-bottom: 2px solid $color-background;
    transition: border-bottom-color 0.3s ease;
    &:hover{
      border-bottom: 2px solid $color-light
    }
    &__title{
      font-weight: bold;
      color: $color-base;
    }
    &__price{
      font-weight: bold;
    }
  }
}
</style>
