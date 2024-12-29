<script setup lang="ts">

</script>

<template>
  <div class="header">
    <div class="header__content--wrapper">
      <div class="header__content">
        <div class="header__content__logo">
          <slot name="logo"></slot>
        </div>
        <div class="header__content__items">
          <slot name="items"></slot>
        </div>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.header {
  position: relative;
  width: 100%;
  height: 120px;
  &__content {
    width: clamp(0px, 1800px, 100vw);
    top: 0;
    display: flex;
    justify-content: space-between;
    padding: 15px 20px;
    height: 100px;
    &--wrapper{
      display: flex;
      justify-content: center;
      position: fixed;
      z-index: 1;
      left:0;
      background: $color-background;
      box-shadow: 0 10px 8px 6px $shadow;
      width: 100%;
    }
    &__items{
      display: flex;
      align-items: center;
      height: 100%;
    }
  }
}
</style>
