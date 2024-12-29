package com.watcher.WatcherB.DTO.Product;

import com.watcher.WatcherB.DTO.DictionaryTypeDTO;
import com.watcher.WatcherB.models.Data.Goods.Product;
import lombok.Getter;

@Getter
public class ProductResponseShort {
    private final Integer id;
    private final String name;
    private final Float price;
    private final Integer stockQuantity;
    private final String pictureUrl;
    private final DictionaryTypeDTO brand;
    public ProductResponseShort(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.price = product.getPrice().floatValue();
        this.stockQuantity = product.getStockQuantity();
        this.pictureUrl = product.getPictureUrl();
        if(product.getBrand() != null) {
            this.brand = new DictionaryTypeDTO(product.getBrand().getId(), product.getBrand().getName());
        }else{
            this.brand = null;
        }
    }
}
