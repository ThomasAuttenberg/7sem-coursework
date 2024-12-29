package com.watcher.WatcherB.services;

import com.watcher.WatcherB.models.Data.Goods.Details.*;
import com.watcher.WatcherB.models.Data.Order.OrderStatus;
import com.watcher.WatcherB.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class SharedService {

    private final BrandRepository brandRepository;
    private final CaseTypeRepository caseTypeRepository;
    private final CountryRepository countryRepository;
    private final GlassTypeRepository glassTypeRepository;
    private final OrderStatusRepository orderStatusRepository;
    private final StyleTypeRepository styleTypeRepository;

    public SharedService(
            @Autowired BrandRepository brandRepository,
            @Autowired CaseTypeRepository caseTypeRepository,
            @Autowired CountryRepository countryRepository,
            @Autowired GlassTypeRepository glassTypeRepository,
            @Autowired OrderStatusRepository orderStatusRepository,
            @Autowired StyleTypeRepository styleTypeRepository) {
        this.brandRepository = brandRepository;
        this.caseTypeRepository = caseTypeRepository;
        this.countryRepository = countryRepository;
        this.glassTypeRepository = glassTypeRepository;
        this.orderStatusRepository = orderStatusRepository;
        this.styleTypeRepository = styleTypeRepository;
    }

    public Object createEntity(String entity, Map<String, Object> payload) {
        switch (entity.toLowerCase()) {
            case "brands":
                Brand brand = new Brand();
                brand.setName((String) payload.get("name"));
                return brandRepository.save(brand);
            case "case_types":
                CaseType caseType = new CaseType();
                caseType.setType((String) payload.get("type"));
                return caseTypeRepository.save(caseType);
            case "countries":
                Country country = new Country();
                country.setName((String) payload.get("name"));
                return countryRepository.save(country);
            case "glass_types":
                GlassType glassType = new GlassType();
                glassType.setType((String) payload.get("type"));
                return glassTypeRepository.save(glassType);
            case "order_statuses":
                OrderStatus orderStatus = new OrderStatus();
                orderStatus.setStatus((String) payload.get("status"));
                return orderStatusRepository.save(orderStatus);
            case "style_types":
                StyleType styleType = new StyleType();
                styleType.setType((String) payload.get("type"));
                return styleTypeRepository.save(styleType);
            default:
                throw new IllegalArgumentException("Unknown entity: " + entity);
        }
    }

    public List<?> getAllEntities(String entity) {
        return switch (entity.toLowerCase()) {
            case "brands" -> brandRepository.findAll();
            case "case_types" -> caseTypeRepository.findAll();
            case "countries" -> countryRepository.findAll();
            case "glass_types" -> glassTypeRepository.findAll();
            case "order_statuses" -> orderStatusRepository.findAll();
            case "style_types" -> styleTypeRepository.findAll();
            default -> throw new IllegalArgumentException("Unknown entity: " + entity);
        };
    }

    public void deleteEntity(String entity, Integer id) {
        switch (entity.toLowerCase()) {
            case "brands":
                brandRepository.deleteById(id);
                break;
            case "case_types":
                caseTypeRepository.deleteById(id);
                break;
            case "countries":
                countryRepository.deleteById(id);
                break;
            case "glass_types":
                glassTypeRepository.deleteById(id);
                break;
            case "order_statuses":
                orderStatusRepository.deleteById(id);
                break;
            case "style_types":
                styleTypeRepository.deleteById(id);
                break;
            default:
                throw new IllegalArgumentException("Unknown entity: " + entity);
        }
    }
}