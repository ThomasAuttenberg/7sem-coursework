<template>
  <div class="editable-label">
    <div v-if="!isEditing" class="editable-label__view">
      <span>{{ modelValue }}</span>
      <button class="editable-label__edit-button" @click="startEditing">
        <i class="pi pi-pencil"></i>
      </button>
    </div>
    <div v-else class="editable-label__edit">
      <input
        v-model="editedLabel"
        @keydown.enter="saveChanges"
        @keydown.esc="cancelEditing"
        class="editable-label__input"
      />
      <button class="editable-label__save-button" @click="saveChanges">
        <i class="pi pi-check"></i>
      </button>
      <button class="editable-label__cancel-button" @click="cancelEditing">
        <i class="pi pi-times"></i>
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, defineEmits } from 'vue';

const props = defineProps({
  modelValue: {
    type: String,
    required: true,
  },
});

const emits = defineEmits(['update:modelValue']);

const isEditing = ref(false);
const editedLabel = ref(props.modelValue);

const startEditing = () => {
  editedLabel.value = props.modelValue;
  isEditing.value = true;
};

const saveChanges = () => {
  emits('update:modelValue', editedLabel.value);
  isEditing.value = false;
};

const cancelEditing = () => {
  editedLabel.value = props.modelValue;
  isEditing.value = false;
};
</script>

<style scoped>
.editable-label {
  display: flex;
  align-items: center;
  gap: 8px;
}

.editable-label__view {
  display: flex;
  align-items: center;
  gap: 8px;
}

.editable-label__edit {
  display: flex;
  align-items: center;
  gap: 8px;
}

.editable-label__input {
  padding: 4px 8px;
  border: 1px solid #ccc;
  border-radius: 4px;
}

.editable-label__edit-button,
.editable-label__save-button,
.editable-label__cancel-button {
  background: none;
  border: none;
  cursor: pointer;
  font-size: 16px;
  color: #545173;
  transition: color 0.2s;
}

.editable-label__edit-button:hover,
.editable-label__save-button:hover,
.editable-label__cancel-button:hover {
  color: #9b97c8;
}
</style>
