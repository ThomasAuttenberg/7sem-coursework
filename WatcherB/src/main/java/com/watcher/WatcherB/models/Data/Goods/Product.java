package com.watcher.WatcherB.models.Data.Goods;

import com.watcher.WatcherB.models.Data.Goods.Details.*;
import lombok.*;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
@Table (name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    private String description;

    @ManyToOne
    @JoinColumn(name = "country_id")
    private Country country;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer stockQuantity;

    private String pictureUrl;

    @ManyToOne
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @Column(nullable = false)
    private Integer waterproof = 0;

    @ManyToOne
    @JoinColumn(name = "glass_type_id")
    private GlassType glassType;

    @ManyToOne
    @JoinColumn(name = "case_type_id")
    private CaseType caseType;

    @ManyToOne
    @JoinColumn(name = "style_id")
    private StyleType styleType;
}
