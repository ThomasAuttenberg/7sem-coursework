package com.watcher.WatcherB.DTO.Product;

import lombok.Getter;

import java.util.LinkedList;

@Getter
public class ProductListResponse {
    private final LinkedList<ProductResponseShort> products;
    ProductListResponse(LinkedList<ProductResponseShort> products) {this.products = products;}
}
