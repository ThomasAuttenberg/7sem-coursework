<template>
  <div class="short-list">
    <ul class="short-list__options">
      <li
        v-for="(option, index) in paginatedOptions"
        :key="getOptionKey(option, index)"
        class="short-list__option"
        :class="{ 'short-list__option--clickable': clickable }"
        @click="clickable ? handleOptionClick(option) : undefined"
      >
        {{ getOptionLabel(option) }}
      </li>
    </ul>

    <div v-if="paginationEnabled" class="short-list__pagination">
      <button
        class="short-list__pagination-button"
        :disabled="currentPage === 1"
        @click="updatePage(currentPage - 1)"
      >
        Previous
      </button>
      <span>Page {{ currentPage }} of {{ totalPages }}</span>
      <button
        class="short-list__pagination-button"
        :disabled="currentPage === totalPages"
        @click="updatePage(currentPage + 1)"
      >
        Next
      </button>

      <label>
        Items per page:
        <select v-model="itemsPerPage" @change="updateItemsPerPage">
          <option v-for="size in pageSizes" :key="size" :value="size">{{ size }}</option>
        </select>
      </label>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, type PropType, defineEmits } from 'vue';

const props = defineProps({
  options: {
    type: Array as PropType<any[]>,
    required: true,
  },
  clickable: {
    type: Boolean,
    default: false,
  },
  paginationEnabled: {
    type: Boolean,
    default: false,
  },
  defaultItemsPerPage: {
    type: Number,
    default: 10,
  },
  pageSizes: {
    type: Array as PropType<number[]>,
    default: () => [5, 10, 20, 50],
  },
  optionLabelKey: {
    type: String,
    default: 'label',
  },
  optionKeyFn: {
    type: Function as PropType<(option: any, index: number) => string | number>,
    default: null,
  },
});

const emits = defineEmits(["update:page", "update:itemsPerPage", "optionClick"]);

const currentPage = ref(1);
const itemsPerPage = ref(props.defaultItemsPerPage);

const totalPages = computed(() => {
  return Math.ceil(props.options.length / itemsPerPage.value);
});

const paginatedOptions = computed(() => {
  if (!props.paginationEnabled) return props.options;
  const start = (currentPage.value - 1) * itemsPerPage.value;
  return props.options.slice(start, start + itemsPerPage.value);
});

const getOptionLabel = (option: any): string => {
  if (typeof option === 'string' || typeof option === 'number') {
    return String(option);
  }
  return option[props.optionLabelKey] || '';
};

const getOptionKey = (option: any, index: number): string | number => {
  return props.optionKeyFn ? props.optionKeyFn(option, index) : index;
};

const handleOptionClick = (option: any) => {
  emits('optionClick', option);
};

const updatePage = (page: number) => {
  currentPage.value = page;
  emits('update:page', page);
};

const updateItemsPerPage = () => {
  currentPage.value = 1; // Reset to first page when changing items per page
  emits('update:itemsPerPage', itemsPerPage.value);
};
</script>

<style scoped>
.short-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.short-list__options {
  list-style: none;
  padding: 0;
  margin: 0;
}

.short-list__option {
  padding: 8px;
  border: 1px solid #ccc;
  border-radius: 4px;
  cursor: default;
}

.short-list__option--clickable {
  cursor: pointer;
  background-color: #f9f9f9;
  transition: background-color 0.2s;
}

.short-list__option--clickable:hover {
  background-color: #e9e9e9;
}

.short-list__pagination {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.short-list__pagination-button {
  padding: 5px 10px;
  border: none;
  background-color: #545173;
  color: white;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.short-list__pagination-button:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

.short-list__pagination-button:hover:not(:disabled) {
  background-color: #9b97c8;
}
</style>
