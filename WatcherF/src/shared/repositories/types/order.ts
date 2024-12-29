import type { ProductShort } from '@/shared/repositories/types/product.ts'

export type OrderItems = {
  productId: number;
  quantity: number;
}

export type Order = {
  id: number,
  status: string,
  items: OrderItems[]
  createdAt: Date;
  confirmedAt: Date | null;
  completedAt: Date | null;
  expectedDeliveryAt: Date | null;
  deliveryAddress: string | null;
  contactPhone: string;
  accessHash: string;
};

export type OrderListRequest = {
  expectedDeliveryAt?: Date;
  createdDate?: Date;
  completedAt?: Date;
  contactPhone?: string,
  contactName?: string,
  deliveryAddress?: string,
};

export type OrderListResponse = {
  content: Array<Order>,
  page: number,
  size: number,
  totalElements: number,
  totalPages: number,
}

export type OrderUpdateRequest = {
  id: number;
  statusId?: number;
  confirmedAt?: Date;
  completedAt?: Date;
  expectedDeliveryAt?: Date;
  deliveryAddress?: string;
  contactPhone?: string;
  items?: OrderItems[];
}

export type OrderCreateRequest = {
  deliveryAddress: string;
  contactPhone: string;
  contactName: string;
  items: OrderItems[];
}
