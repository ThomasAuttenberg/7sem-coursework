package com.watcher.WatcherB.controllers;

import com.watcher.WatcherB.DTO.ErrorResponse;
import com.watcher.WatcherB.DTO.PagedResponse;
import com.watcher.WatcherB.DTO.Product.*;
import com.watcher.WatcherB.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
public class ProductsController {

    private final ProductService productService;
    ProductsController(@Autowired ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/catalog")
    public ResponseEntity<?> getCatalog(
            @ModelAttribute ProductListRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size){
        if(page < 0 || size < 0){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("invalid pagination values"));
        }
        Pageable pageable = PageRequest.of(page, Math.min(size,30));
        PagedResponse<ProductResponseShort> pagedResponse = productService.getProducts(request, pageable);
        return ResponseEntity.ok(pagedResponse);
    }

    @GetMapping("/product")
    public ResponseEntity<?> getProduct(@ModelAttribute ProductRequest request){
        try {
            return ResponseEntity.ok(productService.getProduct(request));
        }catch (ProductService.ProductNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    @Secured({"ROLE_OPERATOR", "ROLE_ADMIN"})
    @DeleteMapping("/product")
    public ResponseEntity<?> deleteProduct(@ModelAttribute ProductRequest request){
        try{
            productService.deleteProduct(request);
            return ResponseEntity.ok().body("Продукт успешно удален");
        }catch (ProductService.ProductNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    @Secured({"ROLE_OPERATOR", "ROLE_ADMIN"})
    @PatchMapping("/product")
    public ResponseEntity<?> updateProduct(@RequestBody UpdateProductRequest request){
        try{
            return ResponseEntity.ok(productService.updateProduct(request));
        }catch (ProductService.ProductNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    @Secured({"ROLE_OPERATOR", "ROLE_ADMIN"})
    @PostMapping("/product")
    public ResponseEntity<?> addProduct(@RequestBody CreateProductRequest request){
        try{
            return ResponseEntity.ok(productService.createProduct(request));
        }catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
