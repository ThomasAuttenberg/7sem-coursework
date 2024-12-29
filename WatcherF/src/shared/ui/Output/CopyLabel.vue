<template>
  <div class="copy-label">
    <span class="label-text">{{ text }}</span>
    <button
      class="copy-button"
      @click="copyText"
      :aria-label="'Копировать ' + text"
    >
      <i :class="copyIcon"></i>
    </button>
    <transition name="fade">
      <span v-if="copied" class="copied-tooltip">Скопировано!</span>
    </transition>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';

// Определение свойств компонента
const props = defineProps({
  text: {
    type: String,
    required: true,
  },
  showButton: {
    type: Boolean,
    default: true,
  },
  // Можно добавить дополнительные пропсы для кастомизации
  tooltipDuration: {
    type: Number,
    default: 2000, // Длительность отображения tooltip в миллисекундах
  },
});

// Определение событий компонента
const emit = defineEmits(['copied']);

// Реактивные переменные
const copied = ref(false);

const copyIcon = ref('pi pi-copy'); // Пример с PrimeIcons

// Функция для копирования текста
const copyText = async () => {
  try {
    await navigator.clipboard.writeText(props.text);
    copied.value = true;
    emit('copied');

    // Сброс состояния после заданной длительности
    setTimeout(() => {
      copied.value = false;
    }, props.tooltipDuration);
  } catch (err) {
    console.error('Ошибка при копировании текста:', err);
  }
};
</script>

<style scoped lang="scss">
.copy-label {
  display: flex;
  align-items: center;
  position: relative;

  .label-text {
    font-size: 16px;
    color: #333;
    margin-right: 8px;
    word-break: break-all;
  }

  .copy-button {
    background: none;
    border: none;
    cursor: pointer;
    color: #555;
    font-size: 18px;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 4px;
    transition: color 0.3s ease;

    &:hover {
      color: #000;
    }

    i {
      pointer-events: none;
    }
  }

  .copied-tooltip {
    position: absolute;
    top: 0;
    right: 0;
    transform: translateX(100%);
    background-color: #545173;
    color: #fff;
    padding: 4px 8px;
    border-radius: 4px;
    font-size: 12px;
    white-space: nowrap;
    opacity: 0.9;
  }

  /* Анимация для tooltip */
  .fade-enter-active, .fade-leave-active {
    transition: opacity 0.3s;
  }
  .fade-enter-from, .fade-leave-to {
    opacity: 0;
  }
}
</style>
