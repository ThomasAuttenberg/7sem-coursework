import axios, {type AxiosResponse } from 'axios'
import { useAuthStore } from '@/shared/stores/authStore.ts'

type RequestHeaders = Record<string, string>;

// Интерфейс обертки для ответа от API
export interface Response<T> {
  data: T;               // Данные, возвращаемые от API
  status: number;        // HTTP статус
  statusText: string;    // Статус текста
}

class ApiClient {
  private axiosInstance;
  private baseURL : string = import.meta.env.VITE_API_BASE;
  public constructor() {
    this.axiosInstance = axios.create();
  }

  private defaultHeaders = {
    "Content-Type": "application/json",
    "Access-Control-Allow-Origin": "*",
    "Cross-Origin": "*",
  }

  private catchUnauthorized(error: unknown){
    if(axios.isAxiosError(error) && error.status == 401){
      useAuthStore().removeToken();
    }
  }

  private getProcessedHeaders(headers: RequestHeaders){
    console.log("preprocessed headers", headers);
    const authHeader : {Authorization?:string} = {};
    if(useAuthStore().isAuthorized) {
      authHeader.Authorization = 'Basic ' + localStorage.getItem('authToken');
    }
    // Вот в newHeaders добавляем только хэдеры, которые отсутствуют изначально
    return {...authHeader, ...headers, ...Object.fromEntries(
        Object.entries(this.defaultHeaders).filter(([key]) => !headers[key])
      ),};
  }


  public async get<ResponseData>(
    endpoint: string,
    headers: RequestHeaders = {}
  ): Promise<Response<ResponseData>> {
    const headers_modified = this.getProcessedHeaders(headers);
    const endpoint_modified = this.baseURL + endpoint;
    console.log(endpoint_modified);
    try {
      const axiosResponse = await this.axiosInstance.get(endpoint_modified, {
        headers: headers_modified,
      });
      return this.wrapResponse<ResponseData>(axiosResponse);
    } catch (error) {
      this.catchUnauthorized(error);
      throw error;
    }
  }

  public async put<ResponseData, RequestData>(
    endpoint: string,
    body: RequestData,
    headers: RequestHeaders = {}
  ): Promise<Response<ResponseData>> {
    const headers_modified = this.getProcessedHeaders(headers);
    const endpoint_modified = this.baseURL + endpoint;
    try {
      const axiosResponse = await this.axiosInstance.put(endpoint_modified, body, {
        headers: headers_modified,
      });
      return this.wrapResponse<ResponseData>(axiosResponse);
    } catch (error) {
      this.catchUnauthorized(error);
      throw error;
    }
  }

  public async post<ResponseData, RequestData>(
    endpoint: string,
    body: RequestData,
    headers: RequestHeaders = {}
  ): Promise<Response<ResponseData>> {
    const headers_modified = this.getProcessedHeaders(headers);
    const endpoint_modified = this.baseURL + endpoint;
    try {
      const axiosResponse = await this.axiosInstance.post(endpoint_modified, body, {
        headers: headers_modified,
      });
      return this.wrapResponse<ResponseData>(axiosResponse);
    } catch (error) {
      this.catchUnauthorized(error);
      throw error;
    }
  }
  public async delete<ResponseData>(
    endpoint: string,
    headers: RequestHeaders = {}
  ): Promise<Response<ResponseData>> {
    const headers_modified = this.getProcessedHeaders(headers);
    const endpoint_modified = this.baseURL + endpoint;
    try {
      const axiosResponse = await this.axiosInstance.delete(endpoint_modified, {
        headers: headers_modified
      });
      return this.wrapResponse<ResponseData>(axiosResponse);
    } catch (error) {
      this.catchUnauthorized(error);
      throw error;
    }
  }

  public async patch<ResponseData, RequestData>(
    endpoint: string,
    body: RequestData,
    headers: RequestHeaders = {}
  ): Promise<Response<ResponseData>> {
    const headers_modified = this.getProcessedHeaders(headers);
    const endpoint_modified = this.baseURL + endpoint;
    try {
      const axiosResponse = await this.axiosInstance.patch(endpoint_modified, body, {
        headers: headers_modified,
      });
      return this.wrapResponse<ResponseData>(axiosResponse);
    } catch (error) {
      this.catchUnauthorized(error);
      throw error;
    }
  }

  // Вспомогательный метод для обёртки ответа
  private wrapResponse<T>(axiosResponse: AxiosResponse): Response<T> {
    return {
      data: axiosResponse.data,
      status: axiosResponse.status,
      statusText: axiosResponse.statusText,
    };
  }
}

// Экземпляр API клиента
const apiClient = new ApiClient();

// Экспорт методов API для удобства
export const post = apiClient.post.bind(apiClient);
export const put = apiClient.put.bind(apiClient);
export const get = apiClient.get.bind(apiClient);
export const del = apiClient.delete.bind(apiClient);
export const patch = apiClient.patch.bind(apiClient);
