import { defineStore } from 'pinia';
import { ref } from 'vue';

export const useCatalogStore = defineStore('filterStore', () => {
  const filters = ref({
    style: <number | null>null,
    brand: <number | null>null,
    caseType: <number | null>null,
    glassType: <number | null>null,
    waterproofMin: <number | null>null,
    waterproofMax: <number | null>null,
    inStock: <boolean | null>null,
    priceMin: <number | null>null,
    priceMax: <number | null>null,
  });

  const currentPage = ref<number | null>(null);
  const setCurrentPage = (page: number): void => {currentPage.value = page;}
  const updateFilter = (new_filters: typeof filters.value) => {
    filters.value = new_filters;
  };

  const resetFilters = () => {
    filters.value = {
      style: <number | null>null,
      brand: <number | null>null,
      caseType: <number | null>null,
      glassType: <number | null>null,
      waterproofMin: <number | null>null,
      waterproofMax: <number | null>null,
      inStock: <boolean | null>null,
      priceMin: <number | null>null,
      priceMax: <number | null>null,
    };
  };

  return {
    filters,
    updateFilter,
    resetFilters,
    currentPage,
    setCurrentPage,
  };
});
