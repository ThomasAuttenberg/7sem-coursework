package com.watcher.WatcherB.DTO.Product;

import com.watcher.WatcherB.DTO.DictionaryTypeDTO;
import com.watcher.WatcherB.models.Data.Goods.Product;

import lombok.Getter;


@Getter
public class ProductResponse {
    private final Integer id;
    private final String name;
    private final String description;
    private final DictionaryTypeDTO country;
    private final Float price;
    private final Integer stockQuantity;
    private final String pictureUrl;
    private final DictionaryTypeDTO brand;
    private final Integer waterproof;
    private final DictionaryTypeDTO glassType;
    private final DictionaryTypeDTO caseType;
    private final DictionaryTypeDTO styleType;
    public ProductResponse(Product product){
        this.id = product.getId();
        this.name = product.getName();
        this.description = product.getDescription();
        this.country = new DictionaryTypeDTO(product.getCountry().getId(), product.getCountry().getName());
        this.price = product.getPrice().floatValue();
        this.stockQuantity = product.getStockQuantity();
        this.pictureUrl = product.getPictureUrl();
        this.brand = new DictionaryTypeDTO(product.getBrand().getId(),product.getBrand().getName());
        this.waterproof = product.getWaterproof();
        this.glassType = new DictionaryTypeDTO(product.getGlassType().getId(), product.getGlassType().getType());
        this.caseType = new DictionaryTypeDTO(product.getCaseType().getId(), product.getCaseType().getType());
        this.styleType = new DictionaryTypeDTO(product.getStyleType().getId(), product.getStyleType().getType());
    }
}
