package com.watcher.WatcherB.services;

import com.watcher.WatcherB.DTO.PagedResponse;
import com.watcher.WatcherB.DTO.Product.*;
import com.watcher.WatcherB.models.Data.Goods.Details.*;
import com.watcher.WatcherB.models.Data.Goods.Product;
import com.watcher.WatcherB.repositories.*;
import com.watcher.WatcherB.utils.ValidationUtils;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final GlassTypeRepository glassTypeRepository;
    private final BrandRepository brandRepository;
    private final CountryRepository countryRepository;
    private final CaseTypeRepository caseTypeRepository;
    private final StyleTypeRepository styleTypeRepository;
    ProductService(@Autowired ProductRepository productRepository,
                   @Autowired GlassTypeRepository glassTypeRepository,
                   @Autowired BrandRepository brandRepository,
                   @Autowired CountryRepository countryRepository,
                   @Autowired CaseTypeRepository caseTypeRepository,
                   @Autowired StyleTypeRepository styleTypeRepository
    ) {
        this.glassTypeRepository = glassTypeRepository;
        this.productRepository = productRepository;
        this.brandRepository = brandRepository;
        this.countryRepository = countryRepository;
        this.caseTypeRepository = caseTypeRepository;
        this.styleTypeRepository = styleTypeRepository;
    }

    public PagedResponse<ProductResponseShort> getProducts(ProductListRequest request, Pageable pageable) {

        Page<Product> products = productRepository.findWithFilters(
                request.getStyle(),
                request.getBrand(),
                request.getCaseType(),
                request.getGlassType(),
                request.getWaterproofMin(),
                request.getWaterproofMax(),
                request.getInStock(),
                request.getPriceMin(),
                request.getPriceMax(),
                pageable
        );

        return new PagedResponse<>(products.map(ProductResponseShort::new));
    }

    public ProductResponse getProduct(ProductRequest request) {
        return productRepository.findById(request.getId())
                .map(ProductResponse::new)
                .orElseThrow(() -> new ProductNotFoundException("Товар с заданным ID не найден"));
    }

    public static class ProductNotFoundException extends RuntimeException {
        public ProductNotFoundException(String s) {
        }
    }
    public ProductResponse createProduct(CreateProductRequest request) {
        ValidationUtils.requireNonNull(request.getName(), "Название не указано");
        ValidationUtils.requireNonNull(request.getCountryId(), "Страна не указана");
        ValidationUtils.requirePositive(request.getPrice(), "Некорректная цена");
        ValidationUtils.requireNonNegative(request.getStockQuantity(), "Количество не может быть отрицательным");


        StyleType styleType = styleTypeRepository.findById(request.getStyleTypeId())
                .orElseThrow(()->new IllegalArgumentException("Неверно указан тип стиля"));
        CaseType caseType = caseTypeRepository.findById(request.getCaseTypeId())
                .orElseThrow(()->new IllegalArgumentException("Неверно указан тип кейса"));
        GlassType glassType = glassTypeRepository.findById(request.getGlassTypeId())
                .orElseThrow(()->new IllegalArgumentException("Неверно указан тип стекла"));
        Country country = countryRepository.findById(request.getCountryId())
                .orElseThrow(()->new IllegalArgumentException("Неверно указана страна"));
        Brand brand = brandRepository.findById(request.getBrandId())
                .orElseThrow(()->new IllegalArgumentException("Неверно указан бренд"));

        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setCountry(country);
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setPictureUrl(request.getPictureUrl());
        product.setBrand(brand);
        product.setWaterproof(request.getWaterproof());
        product.setGlassType(glassType);
        product.setCaseType(caseType);
        product.setStyleType(styleType);

        product = productRepository.save(product);
        return new ProductResponse(product);
    }

    @Transactional
    public ProductResponse updateProduct(UpdateProductRequest request) throws IllegalArgumentException, ProductNotFoundException {
        // Проверяем наличие товара

        if(request.getId() == null){
            throw new IllegalArgumentException("ID товара не установлен");
        }

        Product product = productRepository.findById(request.getId())
                .orElseThrow(() -> new ProductNotFoundException("Товар с ID " + request.getId() + " не найден"));

        // Обновляем поля, если они указаны
        if (request.getName() != null) product.setName(request.getName());
        if (request.getDescription() != null) product.setDescription(request.getDescription());
        if (request.getCountryId() != null) {
            Country country = countryRepository.findById(request.getCountryId())
                    .orElseThrow(() -> new IllegalArgumentException("Страна с ID " + request.getCountryId() + " не найдена"));
            product.setCountry(country);
        }
        if (request.getPrice() != null) product.setPrice(request.getPrice());
        if (request.getStockQuantity() != null) product.setStockQuantity(request.getStockQuantity());
        if (request.getPictureUrl() != null) product.setPictureUrl(request.getPictureUrl());
        if (request.getBrandId() != null) {
            Brand brand = brandRepository.findById(request.getBrandId())
                    .orElseThrow(() -> new IllegalArgumentException("Бренд с ID " + request.getBrandId() + " не найден"));
            product.setBrand(brand);
        }
        if (request.getWaterproof() != null) product.setWaterproof(request.getWaterproof());
        if (request.getGlassTypeId() != null) {
            GlassType glassType = glassTypeRepository.findById(request.getGlassTypeId())
                    .orElseThrow(() -> new IllegalArgumentException("Стекло с ID " + request.getGlassTypeId() + " не найдено"));
            product.setGlassType(glassType);
        }
        if (request.getCaseTypeId() != null) {
            CaseType caseType = caseTypeRepository.findById(request.getCaseTypeId())
                    .orElseThrow(() -> new IllegalArgumentException("Тип корпуса с ID " + request.getCaseTypeId() + " не найден"));
            product.setCaseType(caseType);
        }
        if (request.getStyleTypeId() != null) {
            StyleType styleType = styleTypeRepository.findById(request.getStyleTypeId())
                    .orElseThrow(() -> new IllegalArgumentException("Тип стиля с ID " + request.getStyleTypeId() + " не найден"));
            product.setStyleType(styleType);
        }

        // Сохраняем изменения
        product = productRepository.save(product);

        return new ProductResponse(product);
    }

    @Transactional
    public void deleteProduct(ProductRequest request) {
        if (!productRepository.existsById(request.getId())) {
            throw new ProductNotFoundException("Товар с ID " + request.getId() + " не найден");
        }
        productRepository.deleteById(request.getId());
    }

}
