<script setup lang="ts">
import {RouterView } from 'vue-router'
import GlobalHeader from '@/shared/ui/global/Header/Implementations/GlobalHeader.vue'
</script>

<template>
  <header>
    <GlobalHeader/>
  </header>
  <RouterView id="app-main-router-view" />
</template>

<style lang="scss">
@use '@/shared/styles/main' as *;

body{
  background-color: $color-background;
  color: $color-text;
}
#app-main-router-view{
  max-width: 1800px;
  width: 100%;
  height: 100%;
  overflow: auto;
}
#app {
  position: relative;
  //max-width: 1800px;
  display: flex;
  align-items: center;
  flex-direction: column;
}
#app-header-filler{
  height: 100px;
  width: 100%;
}
</style>
