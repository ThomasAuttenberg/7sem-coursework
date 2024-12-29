<script setup lang="ts">

import SelectBase from '@/shared/ui/Input/SelectBase.vue'
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { DetailsRepository } from '@/shared/repositories/DetailsRepository.ts'
import { ProductRepository } from '@/shared/repositories/ProductRepository.ts'
import type { ProductListRequest } from '@/shared/repositories/types/product.ts'
import ProductShortView from '@/entities/Shop/ProductShort.vue'
import {type ProductShort} from '@/shared/repositories/types/product'
import PrettyLoader from '@/shared/ui/loaders/PrettyLoader.vue'
import { useCatalogStore } from '@/shared/stores/catalogStateStore.ts'
import { onBeforeRouteLeave, useRoute } from 'vue-router'
import router from '@/router'

const route = useRoute();
const baseUrl : string = import.meta.env.VITE_API_BASE;



const filtersBlockVisible = ref<boolean>(false);
const toggleFiltersBlockVisible = ()=>{
  filtersBlockVisible.value = !filtersBlockVisible.value;
}

type filterValues = {
  brands: Array<{id: number, name:string}>,
  caseTypes: Array<{id:number, type:string}>,
  countries: Array<{id:number, name:string}>,
  glassTypes: Array<{id:number, type:string}>,
  styleTypes: Array<{id:number, type:string}>,
}

const filterValues = reactive<filterValues>({
  brands: [],
  caseTypes: [],
  countries: [],
  glassTypes: [],
  styleTypes: [],
});

onMounted(()=>{
  DetailsRepository.getBrands().then((res) => {filterValues.brands = res.data});
  DetailsRepository.getCaseTypes().then((res) => {filterValues.caseTypes = res.data});
  DetailsRepository.getCountries().then((res) => {filterValues.countries = res.data});
  DetailsRepository.getGlassTypes().then((res) => {filterValues.glassTypes = res.data});
  DetailsRepository.getStyleTypes().then((res) => {filterValues.styleTypes = res.data});

  const fs = useCatalogStore().filters;
  const pageFromQuery = parseInt(route.query.page as string, 10);
  if (!isNaN(pageFromQuery) && pageFromQuery > 0) {
    currentPage.value = pageFromQuery;
  } else {
    currentPage.value = useCatalogStore().currentPage ? useCatalogStore().currentPage : 1; // Установить 1, если параметр отсутствует
  }
  for(const key of Object.keys(fs)) {
    if(fs[key as keyof typeof fs]){
      // eslint-disable-next-line @typescript-eslint/no-explicit-any
      filters[key as keyof typeof filters] = fs[key as keyof typeof fs] ? fs[key as keyof typeof fs] as any : null;
    }
  }
})

onBeforeRouteLeave(()=>{
  useCatalogStore().updateFilter({...filters });
  useCatalogStore().setCurrentPage(currentPage.value);
})

const filters = reactive({
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

const activeFiltersNumber = computed(()=>{return Object.values(filters).filter((val)=>val != null).length});
watch(activeFiltersNumber,(newVal)=>{
  console.log("New active filter num:",newVal);
  console.log(filters);
})
// eslint-disable-next-line @typescript-eslint/no-explicit-any
const updateFilters = (key: keyof typeof filters, option: any) => {
  switch(key){
    case 'style':
    case 'brand':
    case 'caseType':
    case 'glassType':
      if(filters[key] == (option ? option.id : null)) return;
      filters[key] = option? option.id : null;
      break;
    default:
      if(filters[key] == option) return;
      filters[key] = option;
  }
  isCatalogLoading.value = true;
  loadProductList();
}

onMounted(()=>{
  loadProductList();
})

const currentPage = ref(1);
const pageSize = ref(10);
const pagesTotal = ref(0);
const isCatalogLoading = ref(true);
const catalog = ref<Array<ProductShort>>([]);
const loadProductList = () => {
  isCatalogLoading.value = true;
  const requestBody : ProductListRequest = {};
  Object.keys(filters).forEach((key) => {
    const f_key = key as keyof typeof filters;
    if(filters[f_key] != null){
      requestBody[f_key] = filters[f_key];
    }
  });
  requestBody.size = pageSize.value;
  requestBody.page = currentPage.value - 1;
  ProductRepository.getCatalog(requestBody).then((res) => {
    if(res.status == 200){
      catalog.value = res.data.content;
      isCatalogLoading.value = false;
      pagesTotal.value = res.data.totalPages;
    }
  })
}

const prevPage = ()=>{
  if(currentPage.value > 1){
    currentPage.value--;
    router.replace({
      path: route.path.toString(),
      query: { ...route.query, page: currentPage.value.toString() },
    });
    loadProductList();
  }
}
const nextPage = ()=>{
  if(currentPage.value < pagesTotal.value){
    currentPage.value++;
    router.replace({
      path: route.path.toString(),
      query: { ...route.query, page: currentPage.value.toString() },
    });
    loadProductList();
  }
}

</script>

<template>
<div class="shop-view-wrapper">
  <div class="shop-view__main-block">
    <div class="shop-view__filters-block">
      <div class="shop-view__filters-block__title" @click="toggleFiltersBlockVisible">
        Фильтры
        <span v-show="activeFiltersNumber > 0">
          ( {{activeFiltersNumber}} )
        </span>
        <i v-if="filtersBlockVisible" class="pi pi-chevron-up"/>
        <i v-else class="pi pi-chevron-down"/>
      </div>
      <transition name="slide-down">
        <div v-show="filtersBlockVisible" class="shop-view__filters-block__filters">
          <SelectBase
            :options="filterValues.brands.length != 0 ? filterValues.brands : [{name:'Загрузка...'}]"
            option-label-key="name"
            :model-value="filterValues.brands.find((val)=>val.id == filters.brand)"
            @change="updateFilters('brand',$event)"
            show-clear
            allow-input
            placeholder="Бренд"
          />
          <SelectBase
            :options="filterValues.styleTypes.length != 0 ? filterValues.styleTypes : [{type:'Загрузка...'}]"
            option-label-key="type"
            :model-value="filterValues.styleTypes.find((val)=>val.id == filters.style)"
            @change="updateFilters('style',$event)"
            show-clear
            allow-input
            placeholder="Стиль"
          />
          <SelectBase
            :options="filterValues.caseTypes.length != 0 ? filterValues.caseTypes : [{type:'Загрузка...'}]"
            @change="updateFilters('caseType',$event)"
            :model-value="filterValues.caseTypes.find((val)=>val.id == filters.caseType)"
            option-label-key="type"
            show-clear
            allow-input
            placeholder="Кейс"
          />
          <SelectBase
            :options="filterValues.glassTypes.length != 0 ? filterValues.glassTypes : [{type:'Загрузка...'}]"
            @change="updateFilters('glassType',$event)"
            :model-value="filterValues.glassTypes.find((val)=>val.id == filters.glassType)"
            option-label-key="type"
            show-clear
            allow-input
            placeholder="Стекло"
          />
        </div>
      </transition>
    </div>
    <div v-if="isCatalogLoading"><PrettyLoader/></div>
    <div v-else class="shop-view__products-block">
      <div class="shop-view__products-block__pagination">
        <div class="current-page">{{currentPage}}</div>
        <div class="total-pages">из {{pagesTotal}}</div>
        <div class="control-prev" @click="prevPage">
          <i class="pi pi-chevron-left"/>
        </div>
        <div class="control-next" @click="nextPage">
          <i class="pi pi-chevron-right"/>
        </div>
      </div>
      <div class="shop-view__products-block__content">
        <ProductShortView v-for="(product, index) in catalog" :key="index" :product="product"/>
      </div>
      <div class="shop-view__products-block__pagination">
        <div class="current-page">{{currentPage}}</div>
        <div class="total-pages">из {{pagesTotal}}</div>
        <div class="control-prev">
          <i class="pi pi-chevron-left" @click="prevPage"/>
        </div>
        <div class="control-next">
          <i class="pi pi-chevron-right" @click="nextPage"/>
        </div>
      </div>
    </div>
  </div>
</div>
</template>

<style scoped lang="scss">
.shop-view{
  &__main-block {
    padding: 20px;
    display: flex;
    flex-direction: column;
    gap: 20px;
  }
}

.shop-view__products-block{
  padding: 30px;
  box-shadow: 0 0 15px 0 $shadow;
  height: 100%;
  overflow-y: auto;
  justify-content: center;
  display: flex;
  flex-direction: column;
  gap: 30px;
  &__pagination{
    display: flex;
    justify-content: end;
    gap: 5px;
    align-items: center;
    .control-prev, .control-next{
      box-shadow: 0 0 5px 0 $shadow;
      border-radius: 5px;
      padding: 5px;
      color: $color-base;
      &:hover{
        cursor: pointer;
        transform: scale(1.05);
      }
    }
  }
  &__content{
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
    gap: 20px; // Расстояние между карточками
  }
}

.shop-view__filters-block {
  display: flex;
  flex-direction: column;
  gap: 15px;
  &__title{
    display: flex;
    gap: 5px;
    align-items: center;
    font-weight: bold;
    cursor: pointer;
  }
  &__filters{
    box-shadow: 0 0 15px 0 $shadow;
    padding: 20px;
    display: flex;
    flex-direction: row;
    align-items: center;
    gap: 20px;
    flex-wrap: wrap;
  }
}

/* Анимация slide-down */
.slide-down-enter-active {
  transition: all 0.3s ease-out;
  opacity: 0;
  transform: translateY(-50px);
}

.slide-down-enter-from {
  opacity: 0;
  transform: translateY(-50px);
}

.slide-down-enter-to {
  opacity: 1;
  transform: translateY(0);
}

.slide-down-leave-active {
  transition: all 0.3s ease-out;
  opacity: 1;
  transform: translateY(0);
}

.slide-down-leave-from {
  opacity: 1;
  transform: translateY(0);
}

.slide-down-leave-to {
  opacity: 0;
  transform: translateY(-50px);
}

</style>
