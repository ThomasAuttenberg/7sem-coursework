import { del, get, patch, post } from '@/shared/repositories/ApiClient.ts';
import { getParameterizedEndpoint } from '@/shared/repositories/utils/getParameterizedEndpoint.ts';
import type {
  ProductCreateRequest,
  ProductFull,
  ProductListRequest, ProductListResponse,
  ProductUpdateRequest
} from '@/shared/repositories/types/product.ts'

// Типы данных



// Основной URL для товаров
const defaultUrl = '/products';

export const ProductRepository = {
  // Получение каталога товаров с фильтрацией и пагинацией
  getCatalog: (filters?: ProductListRequest) =>
    filters
      ? get<ProductListResponse>(getParameterizedEndpoint(`${defaultUrl}/catalog`, filters))
      : get<ProductListResponse>(`${defaultUrl}/catalog`),

  // Получение полной информации о товаре
  getProduct: (id: number) => get<ProductFull>(`${defaultUrl}/product?id=${id}`),

  // Удаление товара
  deleteProduct: (id: number) => del<{ message: string }>(`${defaultUrl}/product?id=${id}`),

  // Обновление товара
  updateProduct: (updateRequest: ProductUpdateRequest) =>
    patch<{ message: string }, ProductUpdateRequest>(`${defaultUrl}/product`, updateRequest),

  // Создание нового товара
  addProduct: (createRequest: ProductCreateRequest) =>
    post<{ message: string }, ProductCreateRequest>(`${defaultUrl}/product`, createRequest),
};
