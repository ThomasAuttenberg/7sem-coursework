<template>
  <div class="custom-input" :class="{ 'has-error': error }">
    <div class="input-wrapper">
      <!-- Floating Label -->
      <label
        :class="{ 'float': isFocused || internalValue }"
        class="floating-label"
      >
        {{ label }}
      </label>

      <!-- Input Field -->
      <input
        ref="inputRef"
        :type="type"
        v-model="internalValue"
        :placeholder="isFocused ? placeholder : ''"
        @focus="handleFocus"
        @blur="handleBlur"
        :disabled="disabled"
        v-bind="$attrs"
        class="input-field"
      />

      <!-- Clear Button -->
      <button
        v-if="clearable && internalValue && !disabled"
        class="clear-button"
        @click="clearInput"
        type="button"
      >
        &times;
      </button>
    </div>

    <!-- Error Message -->
    <p v-if="error" class="error-message">{{ error }}</p>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue';
import { IMask } from 'vue-imask'


// Определение свойств компонента
const props = defineProps({
  modelValue: {
    type: String,
    default: '',
  },
  mask: {
    type: Object,
    default: null,
  },
  clearable: {
    type: Boolean,
    default: false,
  },
  label: {
    type: String,
    default: '',
  },
  error: {
    type: [String, Boolean],
    default: false,
  },
  type: {
    type: String,
    default: 'text',
  },
  placeholder: {
    type: String,
    default: '',
  },
  disabled: {
    type: Boolean,
    default: false,
  },
});

// Определение событий компонента
const emit = defineEmits(['update:modelValue']);

// Ссылки и реактивные переменные
const inputRef = ref<HTMLInputElement | null>(null);
const isFocused = ref(false);
const internalValue = ref(props.modelValue);

// Обновление внутреннего значения при изменении `modelValue`
watch(
  () => props.modelValue,
  (newVal) => {
    if (newVal !== internalValue.value) {
      internalValue.value = newVal;
    }
  }
);

const iMaskObject = props.mask ? IMask.createMask(props.mask) : null;
const getMaskedValue = (input: string)=>{
  if(iMaskObject) {
    iMaskObject.resolve(input);
    return iMaskObject.value;
  }
  return null;
}

// Обновление родительского компонента при изменении `internalValue`
watch(internalValue, (newVal) => {
  if(newVal && iMaskObject){
    internalValue.value = getMaskedValue(newVal) as unknown as string;
  }
  emit('update:modelValue', newVal);
});



// Функции обработки событий
const handleFocus = () => {
  isFocused.value = true;
};

const handleBlur = () => {
  isFocused.value = false;
};

const clearInput = () => {
  internalValue.value = '';
};


</script>

<style scoped lang="scss">
.custom-input {
  display: flex;
  flex-direction: column;
  position: relative;
  margin: 20px 0;

  .input-wrapper {
    position: relative;
    display: flex;
    align-items: center;

    .floating-label {
      position: absolute;
      left: 12px;
      top: 50%;
      transform: translateY(-50%);
      padding: 0 4px;
      color: #777;
      transition: all 0.2s ease;
      pointer-events: none;
      font-size: 16px;
    }

    .floating-label.float {
      top: -8px;
      left: 0;
      font-size: 12px;
      color: #333;
    }

    .input-field {
      width: 100%;
      padding: 12px;
      padding-left: 12px;
      font-size: 16px;
      border: 1px solid #ccc;
      border-radius: 4px;
      transition: border-color 0.2s ease;

      &:focus {
        outline: none;
        border-color: #545173;
      }

      &:disabled {
        background-color: #f5f5f5;
        cursor: not-allowed;
      }
    }

    .clear-button {
      position: absolute;
      right: 12px;
      background: none;
      border: none;
      font-size: 20px;
      color: #999;
      cursor: pointer;
      padding: 0;
      line-height: 1;

      &:hover {
        color: #333;
      }
    }
  }

  .error-message {
    margin-top: 4px;
    color: #e74c3c;
    font-size: 12px;
  }

  &.has-error .input-field {
    border-color: #e74c3c;

    &:focus {
      border-color: #e74c3c;
    }
  }
}
</style>
