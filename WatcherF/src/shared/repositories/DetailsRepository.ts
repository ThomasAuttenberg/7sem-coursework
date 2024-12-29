
import { get } from '@/shared/repositories/ApiClient.ts'

const defaultUrl = "/shared"

export const DetailsRepository = {
  getBrands: () => get<Array<{id: number, name:string}>>(defaultUrl + "/brands"),
  getCaseTypes: () => get<Array<{id:number, type:string}>>(defaultUrl + "/case_types"),
  getCountries: () => get<Array<{id:number, name:string}>>(defaultUrl + "/countries"),
  getGlassTypes: () => get<Array<{id:number, type:string}>>(defaultUrl + "/glass_types"),
  getOrderStatuses: () => get<Array<{id:number, status:string}>>(defaultUrl + "/order_statuses"),
  getStyleTypes: () => get<Array<{id:number, type:string}>>(defaultUrl + "/style_types"),
}
