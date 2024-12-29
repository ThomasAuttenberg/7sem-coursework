<script setup lang="ts">

import { type PropType, ref, type VNodeRef } from 'vue'

const props = defineProps({
  text: {type: String, required: true},
  textUploaded: String,
  additionalText: String,
  fileValidator: Function as PropType<(arg0: File)=>boolean>
});
const emit = defineEmits(['validationError', 'fileUploaded'])
const input = ref<VNodeRef | null>(null);
const shadowInput = ref<VNodeRef | null>(null);
const fileUploaded = ref(false);

function onFileChange(){
  const shadowInputElement = (shadowInput.value as HTMLInputElement);
  const inputElement = (input.value as HTMLLabelElement);
  const uploadedFile : File | undefined = shadowInputElement?.files?.[0];
  if(uploadedFile){
    if(props.fileValidator){
      if(props.fileValidator(uploadedFile)){
        fileUploaded.value = true;
        emit('fileUploaded',uploadedFile);
      }else{
        fileUploaded.value = false;
        (shadowInput.value as HTMLInputElement).value = '';
        emit('validationError',uploadedFile);
      }
    }else{
      fileUploaded.value = true;
      emit('fileUploaded', uploadedFile);
    }
    if(!props.textUploaded){
      inputElement.textContent = uploadedFile.name;
    }
  }
}
</script>

<template>
  <div class = "pretty-file-input-wrapper">
    <input ref="shadowInput" id="pretty-file-input-input-id" type="file" class = "pretty-file-input-upload-input" @change="onFileChange" hidden/>
    <label ref="input" class="pretty-file-input-custom-file-input" for="pretty-file-input-input-id" v-text="fileUploaded ? textUploaded : text"/>
    <p v-if="additionalText">{{additionalText}}</p>
  </div>
</template>

<style scoped lang="scss">
.pretty-file-input-custom-file-input{
  cursor: pointer;
  color: $color-base;
  height: 20px;
  display: flex;
  align-items: center;
}
.pretty-file-input-wrapper{
  font-size: 14px;
  padding: 0 5px;
  display: flex;
  align-items: center;
  justify-content: start;
  gap: 5px;
}
</style>
