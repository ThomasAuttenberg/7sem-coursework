import { del, get, patch, post } from '@/shared/repositories/ApiClient.ts'
import { getParameterizedEndpoint } from '@/shared/repositories/utils/getParameterizedEndpoint.ts'
import type {
  Order,
  OrderCreateRequest,
  OrderListRequest,
  OrderUpdateRequest
} from '@/shared/repositories/types/order.ts'

const defaultUrl = "/orders"

export const OrderRepository = {
  getOrders: (filters?: OrderListRequest) =>
    filters ?
      get<Array<Order>>(getParameterizedEndpoint(defaultUrl + "/catalog", filters))
      :
      get<Array<Order>>(defaultUrl + "/catalog"),
  getOrder: (hash: string)=> post<Order>(defaultUrl+"/getOrder", {hash}),
  deleteOrder: ()=> del<{message: string}>(defaultUrl+"/order"),
  updateOrder: (newParams: OrderUpdateRequest)=> patch<{message: string}, OrderUpdateRequest>(defaultUrl+"/order", newParams),
  createOrder: (params: OrderCreateRequest) => post<Order,OrderCreateRequest>(defaultUrl+"/order", params),
}
