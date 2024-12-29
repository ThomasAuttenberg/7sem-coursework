export type ProductShort = {
  id: number;
  name: string;
  price: number;
  stockQuantity: number;
  pictureUrl: string;
  brand: IdWithName | null;
};

export type ProductFull = {
  id: number;
  name: string;
  description: string;
  country: IdWithName | null;
  price: number;
  stockQuantity: number;
  pictureUrl: string;
  brand: IdWithName | null;
  waterproof: number;
  glassType: IdWithType | null;
  caseType: IdWithType | null;
  styleType: IdWithStyle | null;
};

export type ProductListResponse = {
  content: Array<ProductShort>,
  page: number,
  size: number,
  totalElements: number,
  totalPages: number,
}

export type ProductListRequest = {
  style?: number;
  brand?: number;
  caseType?: number;
  glassType?: number;
  waterproofMin?: number;
  waterproofMax?: number;
  inStock?: boolean;
  priceMin?: number;
  priceMax?: number;
  page?: number;
  size?: number;
};

export type ProductUpdateRequest = {
  id: number;
  name?: string;
  description?: string;
  countryId?: number;
  price?: number;
  stockQuantity?: number;
  pictureUrl?: string;
  brandId?: number;
  waterproof?: number;
  glassTypeId?: number;
  caseTypeId?: number;
  styleTypeId?: number;
};

export type ProductCreateRequest = {
  name: string;
  description?: string;
  countryId?: number;
  price: number;
  stockQuantity: number;
  pictureUrl?: string;
  brandId?: number;
  waterproof?: number;
  glassTypeId?: number;
  caseTypeId?: number;
  styleTypeId?: number;
};



export type IdWithType = {
  id: number;
  type: string;
};
export type IdWithName = {
  id: number;
  type: string;
}
export type IdWithStyle = {
  id: number;
  style: string;
}
