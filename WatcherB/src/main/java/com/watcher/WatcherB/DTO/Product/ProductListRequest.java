package com.watcher.WatcherB.DTO.Product;

import lombok.Data;

@Data
public class ProductListRequest {
    Integer style;
    Integer brand;
    Integer caseType;
    Integer glassType;
    Integer waterproofMin;
    Integer waterproofMax;
    Boolean inStock;
    float priceMin;
    float priceMax = Float.MAX_VALUE;
}
