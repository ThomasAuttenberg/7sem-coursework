<template>
  <div ref="wrapper" class="select-base">
    <div class="select-base__control-wrapper">
      <!-- Плавающий заголовок -->
      <label
        :class="['select-base__label', { 'is-floating': isFloating }]"
        :for="inputId"
      >
        {{ placeholder }}
      </label>

      <div
        class="select-base__control"
        @click="toggleDropdown"
        @keydown="handleKeyDown"
        tabindex="0"
        role="combobox"
        :aria-expanded="isOpen"
        aria-haspopup="listbox"
        :aria-controls="dropdownId"
        :aria-activedescendant="activeOptionId"
        @focus="handleFocus"
        @blur="handleBlur"
      >
        <!-- Если разрешён ввод, показываем input -->
        <template v-if="allowInput">
          <input
            ref="inputRef"
            class="select-base__input"
            @keydown="handleKeyDown"
            v-model="searchQuery"
            :placeholder="''"
          @click.stop="isOpen = true"
          @keydown.stop
          role="textbox"
          :aria-autocomplete="allowInput ? 'list' : 'none'"
          @focus="handleFocus"
          @blur="handleBlur"
          />
        </template>
        <!-- Иначе просто выводим текст -->
        <template v-else>
          <span class="select-base__display-text">
            {{ selectedLabel || '' }}
          </span>
        </template>

        <!-- Кнопка очистки -->
        <span
          v-if="showClear && selectedOption || showClear && searchQuery.length > 0"
          class="select-base__clear"
          @click.stop="clearSelection"
          role="button"
          aria-label="Очистить выбор"
          tabindex="0"
        >
          ×
        </span>
      </div>
    </div>

    <!-- Выпадающий список -->
    <transition name="slide-down">
      <ul
        v-if="isOpen"
        class="select-base__dropdown"
        role="listbox"
        :id="dropdownId"
        ref="dropdownRef"
      >
        <li
          v-for="(option, index) in filteredOptions"
          :key="getOptionKey(option, index)"
          class="select-base__option"
          @click="onSelectOption(option)"
          :class="{ 'is-active': index === highlightedIndex }"
          :id="getOptionId(index)"
          role="option"
          :aria-selected="selectedLabel === option"
        >
          {{ getLabel(option) }}
        </li>
        <li v-if="filteredOptions.length === 0" class="select-base__option--empty" role="option" aria-disabled="true">
          Нет вариантов
        </li>
      </ul>
    </transition>
  </div>
</template>


<script lang="ts" setup>
import {
  ref,
  computed,
  onMounted,
  onBeforeUnmount,
  defineEmits,
  type PropType,
  nextTick,
  watch,
} from 'vue';

// Генерация уникального ID для ARIA и связывания с label
const generateId = (() => {
  let count = Math.floor(Math.random() * 10000000);
  return () => `select-base-dropdown-${++count}`;
})();

const wasChanged = ref(false);

const selectedOption = defineModel('model-value');

const props = defineProps({
  options: {
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    type: Array as PropType<any[]>,
    default: () => [],
  },
  allowInput: {
    type: Boolean,
    default: false,
  },
  showClear: {
    type: Boolean,
    default: false,
  },
  optionLabelKey: {
    type: String,
    default: 'label',
  },
  optionLabelFn: {
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    type: Function as PropType<(option: any) => string>,
    default: null,
  },
  placeholder: {
    type: String,
    default: 'Выберите...',
  },
  modelValue: {
    required: false,
  },
  initValueId: {type: Number}
});

const emits = defineEmits<{
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  (e: 'update:modelValue', value: any | null): void;
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  (e: 'change', value: any | null): void;
  (e: 'clear'): void;
}>();

// Состояние выпадающего списка
const isOpen = ref(false);

// Текст для поиска (только если allowInput = true)
const searchQuery = ref('');

// Подсветка текущей опции для навигации с клавиатуры
const highlightedIndex = ref(-1);

// Уникальный ID для ARIA
const dropdownId = generateId();
const inputId = `select-base-input-${dropdownId}`;

// Ссылка на корневой элемент и другие элементы
const wrapper = ref<HTMLElement | null>(null);
const dropdownRef = ref<HTMLElement | null>(null);
const inputRef = ref<HTMLInputElement | null>(null);

// Флаг для управления плавающим заголовком
const isFloating = computed(() => {
  return (
    (props.allowInput && searchQuery.value.length > 0) ||
    (!props.allowInput && selectedOption.value)
  );
});

// Управление фокусом
const isFocused = ref(false);

const handleFocus = () => {
  isFocused.value = true;
};

const handleBlur = () => {
  // Используем setTimeout, чтобы дать время на клик по опции перед сбросом фокуса
  setTimeout(() => {
    isFocused.value = false;
  }, 100);
};

// Функция по умолчанию для получения лейбла
// eslint-disable-next-line @typescript-eslint/no-explicit-any
const defaultLabelFn = (option: any, labelKey: string): string => {
  if (typeof option === 'string' || typeof option === 'number') {
    return String(option);
  }
  return option[labelKey] ?? '';
};

// Функция toggleDropdown
const toggleDropdown = () => {
  isOpen.value = !isOpen.value;
  if (isOpen.value && props.allowInput) {
    //searchQuery.value = '';
    highlightedIndex.value = -1;
    nextTick(() => {
      inputRef.value?.focus();
    });
  } else {
    highlightedIndex.value = -1;
  }
};

// Функция для получения лейбла опции
// eslint-disable-next-line @typescript-eslint/no-explicit-any
const getLabel = (option: any): string => {
  if (props.optionLabelFn) {
    return props.optionLabelFn(option);
  }
  return defaultLabelFn(option, props.optionLabelKey as string);
};

// Вычисляемый лейбл для текущего выбранного значения
const selectedLabel = computed(() => {
  if (!selectedOption.value) {
    return '';
  }
  return getLabel(selectedOption.value);
});

// Фильтрация списка опций по введённому тексту (если allowInput)
const filteredOptions = computed(() => {
  if (!props.allowInput || !searchQuery.value || !wasChanged.value) {
    return props.options;
  }
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  return props.options.filter((option: any) =>
    getLabel(option).toLowerCase().includes(searchQuery.value.toLowerCase())
  );
});

// Выбор опции из списка
// eslint-disable-next-line @typescript-eslint/no-explicit-any
const onSelectOption = (option: any) => {
  highlightedIndex.value = -1;
  if (props.allowInput) {
    searchQuery.value = getLabel(option);
  }
  nextTick(()=>{  isOpen.value = false;     selectedOption.value = option; wasChanged.value = false;})
};

// Очистка выбранного значения
const clearSelection = () => {
  emits('clear');
  selectedOption.value = null;
  if (props.allowInput) {
    searchQuery.value = "";
    nextTick(()=>{wasChanged.value = false})
  }
};

// Закрываем дропдаун при клике вне компонента (простейшая реализация)
const onClickOutside = (event: MouseEvent) => {
  if (!wrapper.value) return;
  const target = event.target as HTMLElement;
  if (!wrapper.value.contains(target)) {
    if(!selectedLabel.value) {searchQuery.value = ""}
    isOpen.value = false;
    highlightedIndex.value = -1;
  }
};

// Добавление и удаление слушателя кликов
onMounted(() => {
  document.addEventListener('click', onClickOutside);
});
onBeforeUnmount(() => {
  document.removeEventListener('click', onClickOutside);
});

// Функция для получения уникального ключа для опции
// eslint-disable-next-line @typescript-eslint/no-explicit-any
const getOptionKey = (option: any, index: number) => {
  // Если опция объект и имеет уникальный идентификатор, используем его
  if (typeof option === 'object' && option !== null && 'id' in option) {
    return option.id;
  }
  // Иначе используем индекс
  return index;
};

// Обработка клавиатурных событий
const handleKeyDown = (event: KeyboardEvent) => {
  if (!isOpen.value && (event.key === 'ArrowDown' || event.key === 'ArrowUp')) {
    // Открываем список при нажатии стрелок вверх/вниз
    isOpen.value = true;
    event.preventDefault();
    return;
  }

  if (isOpen.value) {
    switch (event.key) {
      case 'ArrowDown':
        event.preventDefault();
        if (highlightedIndex.value < filteredOptions.value.length - 1) {
          highlightedIndex.value += 1;
        } else {
          highlightedIndex.value = 0;
        }
        scrollToOption(highlightedIndex.value);
        break;
      case 'ArrowUp':
        event.preventDefault();
        if (highlightedIndex.value > 0) {
          highlightedIndex.value -= 1;
        } else {
          highlightedIndex.value = filteredOptions.value.length - 1;
        }
        scrollToOption(highlightedIndex.value);
        break;
      case 'Enter':
        event.preventDefault();
        if (highlightedIndex.value >= 0 && highlightedIndex.value < filteredOptions.value.length) {
          onSelectOption(filteredOptions.value[highlightedIndex.value]);
        }
        break;
      case 'Escape':
        event.preventDefault();
        isOpen.value = false;
        highlightedIndex.value = -1;
        break;
      default:
        break;
    }
  }
};

// Функция для скролла к выделенной опции
const scrollToOption = (index: number) => {
  nextTick(() => {
    const optionId = getOptionId(index);
    const optionElement = document.getElementById(optionId);
    optionElement?.scrollIntoView({ block: 'nearest' });
  });
};

// Функция для получения ID опции по индексу
const getOptionId = (index: number) => {
  return `${dropdownId}-option-${index}`;
};

// Управление активной опцией для ARIA
const activeOptionId = computed(() => {
  if (highlightedIndex.value >= 0 && highlightedIndex.value < filteredOptions.value.length) {
    return getOptionId(highlightedIndex.value);
  }
  return '';
});

onMounted(() => {
  if(props.allowInput) {
    (inputRef.value as HTMLInputElement).addEventListener('focusout',()=>{
      if(wasChanged.value){searchQuery.value = getLabel(selectedOption.value);}
    })
  }
})


watch(searchQuery, (newValue)=>{
  if(!isOpen.value && newValue.length) toggleDropdown();
  wasChanged.value = true;
})

watch(()=>props.modelValue, ()=>{
  if(selectedOption.value){
    highlightedIndex.value = -1;
    if (props.allowInput) {
      searchQuery.value = getLabel(selectedOption.value);
      nextTick(()=>{wasChanged.value=false;})
    }
  }
})

watch(selectedOption, (newValue)=>{
  emits('change', newValue);
  emits('update:modelValue', newValue);
})

// Фокус на input при открытии
watch(isOpen, (newVal) => {
  if (newVal && props.allowInput) {
    nextTick(() => {
      inputRef.value?.focus();
    });
  }
});
</script>


<style lang="scss" scoped>
.select-base {
  position: relative;
  display: inline-block;
  width: 200px;

  &__control-wrapper {
    position: relative;
  }

  &__label {
    position: absolute;
    top: 50%;
    left: 8px;
    transform: translateY(-50%);
    font-size: 14px;
    color: #999;
    pointer-events: none;
    transition: all 0.2s ease-out;
    padding: 0 4px;
  }

  &__label.is-floating {
    top: -8px;
    left: 0px;
    font-size: 12px;
    color: #666;
  }

  &__control {
    display: flex;
    align-items: center;
    justify-content: space-between;
    min-height: 36px;
    padding: 8px;
    border: 1px solid #ccc;
    border-radius: 4px;
    cursor: pointer;
    background-color: #fff;
    outline: none;
    height: 43px;

    &:focus {
      border-color: #66afe9;
      box-shadow: 0 0 5px rgba(102, 175, 233, 0.6);
    }
  }

  &__input {
    margin: -8px;
    flex: 1;
    border: none;
    outline: none;
    padding: 8px;
    font-size: 14px;
    background-color: transparent;
    cursor: text;
  }

  &__display-text {
    flex: 1;
    font-size: 14px;
    padding: 4px 0;
    color: #333;
  }

  &__clear {
    margin-left: 4px;
    cursor: pointer;
    color: #999;
    font-size: 16px;
    line-height: 1;

    &:hover {
      color: #333;
    }
  }

  &__arrow {
    margin-left: 4px;
    user-select: none;
  }

  &__dropdown {
    position: absolute;
    width: 100%;
    max-height: 200px;
    overflow-y: auto;
    border: 1px solid #ccc;
    border-radius: 4px;
    background-color: #fff;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
    z-index: 999;
    list-style: none;
    padding: 0;
    margin: 4px 0 0 0;
    transform-origin: top;
  }

  &__option {
    padding: 8px;
    cursor: pointer;

    &.is-active,
    &:hover {
      background-color: #eee;
    }
  }

  &__option--empty {
    padding: 8px;
    color: #999;
    cursor: default;
  }
}

/* Анимация появления dropdown */
.slide-down-enter-active,
.slide-down-leave-active {
  transition: all 0.3s ease-out;
}

.slide-down-enter-from {
  opacity: 0;
  transform: translateY(-10px);
}

.slide-down-enter-to {
  opacity: 1;
  transform: translateY(0);
}

.slide-down-leave-from {
  opacity: 1;
  transform: translateY(0);
}

.slide-down-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}
</style>
