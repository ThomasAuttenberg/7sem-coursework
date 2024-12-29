<script setup lang="ts">

import InputBase from '@/shared/ui/Input/InputBase.vue'
import { computed, onMounted, reactive, ref } from 'vue'
import { ProductRepository } from '@/shared/repositories/ProductRepository.ts'
import { getIntegerMask } from '@/shared/utils/masks.ts'
import EditableLabel from '@/entities/Management/EditableLabel.vue'
import type { ProductFull } from '@/shared/repositories/types/product.ts'
import { DetailsRepository } from '@/shared/repositories/DetailsRepository.ts'
import FileInput from '@/shared/ui/Input/FileInput.vue'
import { StaticRepository } from '@/shared/repositories/StaticRepository.ts'


const findingError = ref(false);
const productId = ref<string>('');
const isLoading = ref(false);
const product = ref<ProductFull|null>(null)

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

onMounted(()=> {
  DetailsRepository.getBrands().then((res) => {
    filterValues.brands = res.data
  });
  DetailsRepository.getCaseTypes().then((res) => {
    filterValues.caseTypes = res.data
  });
  DetailsRepository.getCountries().then((res) => {
    filterValues.countries = res.data
  });
  DetailsRepository.getGlassTypes().then((res) => {
    filterValues.glassTypes = res.data
  });
  DetailsRepository.getStyleTypes().then((res) => {
    filterValues.styleTypes = res.data
  });
});

onMounted(()=>{
  productActionRequest.value = {
    name: null,
    description: null,
    countryId: null,
    price: null,
    stockQuantity: null,
    pictureUrl: null,
    brandId: null,
    waterproof: null,
    glassTypeId: null,
    caseTypeId: null,
    styleTypeId: null,
  };
})

const findProduct = ()=>{
  isLoading.value = true;
  ProductRepository.getProduct(Number.parseInt(productId.value)).then((data) => {
    product.value = data.data;
  }).catch(()=>{
    findingError.value = true;
  }).finally(()=>{
    isLoading.value = false;
  })
}

type possibleProductActionValues = {
  name: string | null;
  description: string | null;
  countryId: number | null;
  price: number | null;
  stockQuantity: number | null;
  pictureUrl: string | null;
  brandId: number | null;
  waterproof: number | null;
  glassTypeId: number | null;
  caseTypeId: number | null;
  styleTypeId: number | null;
}

const productActionRequest = ref<possibleProductActionValues>();

const updateProductByKey = (key: keyof possibleProductActionValues, value: string)=>{
  switch(key){
    case 'brandId':
    case 'caseTypeId':
    case 'countryId':
    case 'glassTypeId':
    case 'stockQuantity':
    case 'waterproof':
    case 'styleTypeId':
      (productActionRequest.value as possibleProductActionValues)[key] = Number.parseInt(value);
      break;
    case 'price':
      (productActionRequest.value as possibleProductActionValues)[key] = Number.parseFloat(value).toFixed(2);
      break;
    default:
      (productActionRequest.value as possibleProductActionValues)[key] = value;
  }
}
const labelValues = computed(()=> {
  return {
    brandId: productActionRequest.value?.brandId ? productActionRequest.value.brandId : product.value?.brand?.id,
    caseTypeId: productActionRequest.value?.caseTypeId ? productActionRequest.value.caseTypeId : product.value?.caseType?.id,
    countryId: productActionRequest.value?.countryId ? productActionRequest.value.countryId : product.value?.country?.id,
    glassTypeId: productActionRequest.value?.glassTypeId ? productActionRequest.value.glassTypeId : product.value?.glassType?.id,
    stockQuantity: productActionRequest.value?.stockQuantity ? productActionRequest.value.stockQuantity : product.value?.stockQuantity,
    price: productActionRequest.value?.price ? productActionRequest.value.price : product.value?.price,
    waterproof: productActionRequest.value?.waterproof ? productActionRequest.value.waterproof : product.value?.waterproof,
    styleTypeId: productActionRequest.value?.styleTypeId ? productActionRequest.value.styleTypeId : product.value?.styleType?.id,
    pictureUrl: productActionRequest.value?.pictureUrl ? productActionRequest.value.pictureUrl : product.value?.pictureUrl,
    name: productActionRequest.value?.name ? productActionRequest.value.name : product.value?.name,
    description: productActionRequest.value?.description ? productActionRequest.value.description : product.value?.description,
  }
})
const uploadedFileName = ref<string | undefined>();
const onFileUploaded = (file: File) => {
  if(file.type == 'image/jpeg' || file.type == 'image/png'){
    uploadedFileName.value = file.name;
    StaticRepository.uploadImage(file).then((result) => {
      productActionRequest.value.pictureUrl = result.data.file;
    })
  } else{
    uploadedFileName.value = 'Неверный формат!';
  }
  console.log(file);
}

const isUpdatePossible = computed(()=> productActionRequest.value && Object.entries(productActionRequest.value).filter(([, value]) => value != null).length > 0);

const isCreatingPossible = computed(()=> productActionRequest.value && Object.entries(productActionRequest.value).filter(([, value]) => value == null).length == 0);

const mode = ref<'update' | 'create'>('update');
const setMode = (mode_: 'update' | 'create')=>{
  product.value = null;
  productActionRequest.value = {
    name: null,
    description: null,
    countryId: null,
    price: null,
    stockQuantity: null,
    pictureUrl: null,
    brandId: null,
    waterproof: null,
    glassTypeId: null,
    caseTypeId: null,
    styleTypeId: null,
  };
  mode.value = mode_;
}

const updateProduct = ()=>{
  console.log("update with",productActionRequest.value);

  ProductRepository.updateProduct({id: productId.value, ...productActionRequest.value}).then((res)=>{
    console.log(res);
  })
}
const createProduct = ()=>{
  ProductRepository.addProduct(productActionRequest.value).then((res)=>{
    console.log(res);
  })
}

</script>

<template>
<div class="products-viewer">
  <div class="products-viewer__mode">
    <div @click="setMode('update')"
         :class="['products-viewer__mode__title',{'active':mode=='update'}]"
    >
      Обновление
    </div>
    <div @click="setMode('create')"
         :class="['products-viewer__mode__title',{'active':mode=='create'}]"
    >
      Создание
    </div>
  </div>
  <div class="products-viewer__finder" v-show="mode == 'update'">
    <form @submit.prevent="findProduct">
      <InputBase
        v-model="productId"
        :mask="getIntegerMask()"
        clearable
        label="id"
        required
      />
      <button type="submit"><i class="pi pi-caret-right"/></button>
    </form>
  </div>
  <div class="products-viewer__product" v-if="(productActionRequest && product && mode=='update')">
    <span>Изображение</span>
    <FileInput text="Загрузить" :text-uploaded="uploadedFileName" @fileUploaded="onFileUploaded"/>
    <span>Название</span>
    <EditableLabel
      :model-value="labelValues.name as string"
      @update:modelValue="updateProductByKey('name',$event)"
    />
    <span>Описание</span>
    <EditableLabel
      :model-value="labelValues.description"
      @update:modelValue="updateProductByKey('description',$event)"
    />
    <span>Бренд</span>
    <select name="brand" v-if="filterValues.brands.length" @change="updateProductByKey('brandId',($event.target as HTMLSelectElement).value)">
      <option :selected="filter.id == product.brand?.id" :value="filter.id" v-for="filter in filterValues.brands" :key="filter.id">
        {{filter.name}}
      </option>
    </select>
    <span>Корпус</span>
    <select name="caseType" v-if="filterValues.caseTypes.length" @change="updateProductByKey('caseTypeId',($event.target as HTMLSelectElement).value)">
      <option :selected="filter.id == product.caseType?.id" :value="filter.id" v-for="filter in filterValues.caseTypes" :key="filter.id">
        {{filter.type}}
      </option>
    </select>
    <span>Стекло</span>
    <select name="glassType" v-if="filterValues.glassTypes.length" @change="updateProductByKey('glassTypeId',($event.target as HTMLSelectElement).value)">
      <option :selected="filter.id == product.glassType?.id" :value="filter.id" v-for="filter in filterValues.glassTypes" :key="filter.id">
        {{filter.type}}
      </option>
    </select>
    <span>Стиль</span>
    <select name="styleType" v-if="filterValues.styleTypes.length" @change="updateProductByKey('styleTypeId',($event.target as HTMLSelectElement).value)">
      <option :selected="filter.id == product.styleType?.id" :value="filter.id" v-for="filter in filterValues.styleTypes" :key="filter.id">
        {{filter.type}}
      </option>
    </select>
    <span>Страна</span>
    <select name="country" v-if="filterValues.glassTypes.length" @change="updateProductByKey('countryId',($event.target as HTMLSelectElement).value)">
      <option :selected="filter.id == product.country?.id" :value="filter.id" v-for="filter in filterValues.countries" :key="filter.id">
        {{filter.name}}
      </option>
    </select>
    <span>Цена</span>
    <EditableLabel
      :model-value="String(labelValues.price)"
      @update:modelValue="updateProductByKey('price',$event)"
    />
    <span>Водонепроницаемость</span>
    <EditableLabel
      :model-value="String(labelValues.waterproof)"
      @update:modelValue="updateProductByKey('waterproof',$event)"
    />
    <span>На складе</span>
    <EditableLabel
      :model-value="String(labelValues.stockQuantity)"
      @update:modelValue="updateProductByKey('stockQuantity',$event)"
    />
  </div>
  <div class="products-viewer__product" v-if="mode=='create' && productActionRequest">
    <span>Изображение</span>
    <FileInput text="Загрузить" :text-uploaded="uploadedFileName" @fileUploaded="onFileUploaded"/>
    <span>Название</span>
    <EditableLabel
      :model-value="labelValues.name ? String(labelValues.name) : ''"
      @update:modelValue="updateProductByKey('name',$event)"
    />
    <span>Описание</span>
    <EditableLabel
      :model-value="labelValues.description ? labelValues.description : ''"
      @update:modelValue="updateProductByKey('description',$event)"
    />
    <span>Бренд</span>
    <select name="brand" v-if="filterValues.brands.length" @change="updateProductByKey('brandId',($event.target as HTMLSelectElement).value)">
      <option value="0" hidden>Выбрать</option>
      <option :value="filter.id" v-for="filter in filterValues.brands" :key="filter.id">
        {{filter.name}}
      </option>
    </select>
    <span>Корпус</span>
    <select name="caseType" v-if="filterValues.caseTypes.length" @change="updateProductByKey('caseTypeId',($event.target as HTMLSelectElement).value)">
      <option value="0" hidden>Выбрать</option>
      <option :value="filter.id" v-for="filter in filterValues.caseTypes" :key="filter.id">
        {{filter.type}}
      </option>
    </select>
    <span>Стекло</span>
    <select name="glassType" v-if="filterValues.glassTypes.length" @change="updateProductByKey('glassTypeId',($event.target as HTMLSelectElement).value)">
      <option value="0" hidden>Выбрать</option>
      <option :value="filter.id" v-for="filter in filterValues.glassTypes" :key="filter.id">
        {{filter.type}}
      </option>
    </select>
    <span>Стиль</span>
    <select name="styleType" v-if="filterValues.styleTypes.length" @change="updateProductByKey('styleTypeId',($event.target as HTMLSelectElement).value)">
      <option value="0" hidden>Выбрать</option>
      <option :value="filter.id" v-for="filter in filterValues.styleTypes" :key="filter.id">
        {{filter.type}}
      </option>
    </select>
    <span>Страна</span>
    <select name="country" v-if="filterValues.glassTypes.length" @change="updateProductByKey('countryId',($event.target as HTMLSelectElement).value)">
      <option value="0" hidden>Выбрать</option>
      <option :value="filter.id" v-for="filter in filterValues.countries" :key="filter.id">
        {{filter.name}}
      </option>
    </select>
    <span>Цена</span>
    <EditableLabel
      :model-value="labelValues.price ? String(labelValues.price) : ''"
      @update:modelValue="updateProductByKey('price',$event)"
    />
    <span>Водонепроницаемость</span>
    <EditableLabel
      :model-value="labelValues.waterproof ? String(labelValues.waterproof) : ''"
      @update:modelValue="updateProductByKey('waterproof',$event)"
    />
    <span>На складе</span>
    <EditableLabel
      :model-value="labelValues.stockQuantity ? String(labelValues.stockQuantity) : ''"
      @update:modelValue="updateProductByKey('stockQuantity',$event)"
    />
  </div>
  <div class="product-viewer__action">
    <button
      v-if="(mode == 'update' && isUpdatePossible) || (mode == 'create' && isCreatingPossible)"
      @click="mode == 'update'? updateProduct() : createProduct()"
    >
      {{mode == 'update' ? 'Обновить' : 'Создать'}}
    </button>
  </div>
</div>
</template>

<style scoped lang="scss">
.products-viewer{
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 40px;
  padding: 40px;
}
.products-viewer__finder{
  form{
    display: flex;
    justify-content: center;
    align-items: center;
    button{
      background: $color-base;
      color: #fff;
      height: 40px;
      width: 40px;
      outline: none;
      border: none;
      &:hover{
        transform: scale(1.03);
        cursor: pointer;
      }
    }
  }
}
.products-viewer__product{
  display: grid;
  grid-template-columns: 200px 1fr;
  grid-auto-rows: auto;
}
.products-viewer__mode{
  display: flex;
  gap: 30px;
  &__title {
    width: 150px;
    cursor: pointer;
    padding: 5px;
    background: $color-base;
    border-radius: 20px;
    color: $color-background;
    display: flex;
    align-items: center;
    justify-content: center;

    &.active {
      background: $color-light;
    }
  }
}

</style>
