package com.watcher.WatcherB.DTO.Product;
import com.watcher.WatcherB.models.Data.Goods.Details.*;
import lombok.Data;

import java.math.BigDecimal;
@Data
public class CreateProductRequest {
    private String name;
    private String description;
    private Integer countryId;
    private BigDecimal price;
    private Integer stockQuantity;
    private String pictureUrl;
    private Integer brandId;
    private Integer waterproof;
    private Integer glassTypeId;
    private Integer caseTypeId;
    private Integer styleTypeId;
}