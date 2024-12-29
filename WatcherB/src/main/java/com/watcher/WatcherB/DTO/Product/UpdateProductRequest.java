package com.watcher.WatcherB.DTO.Product;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateProductRequest {
    private Integer id; // ID товара, который нужно обновить
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