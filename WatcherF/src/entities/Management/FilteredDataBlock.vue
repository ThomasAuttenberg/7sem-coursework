<script setup lang="ts">

import InputBase from '@/shared/ui/Input/InputBase.vue'
import SelectBase from '@/shared/ui/Input/SelectBase.vue'

type Filter = {
  type: 'select' | 'input',
  label: string,
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  initialValue: any,
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  updateKey: any,
}

const emits = defineEmits(['updateFilter']);

const props = defineProps({
  filters: Array<Filter>,
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  options: Array<any>,
  optionKey: {required: false}
})

// eslint-disable-next-line @typescript-eslint/no-explicit-any
const onFilterChange = (updateKey: any, event: any)=>{
  emits('updateFilter', updateKey, event);
}

</script>

<template>
<div class="management-ui-data-block">
  <div class="filters">
    <template v-for="(item, index) in filters" :key="index">
      <SelectBase
        v-if="item.type=='select'"
        :placeholder="item.label"
        @change="onFilterChange(item.updateKey, $event)"
      />
      <InputBase
        v-else
        :label="item.label"
        @update:modelValue="onFilterChange(item.updateKey, $event)"
      />
    </template>
  </div>
</div>
</template>

<style scoped lang="scss">

</style>
