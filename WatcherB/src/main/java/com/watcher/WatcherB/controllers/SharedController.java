package com.watcher.WatcherB.controllers;

import com.watcher.WatcherB.services.SharedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/shared")
public class SharedController {

    private final SharedService sharedService;

    public SharedController(@Autowired SharedService sharedService) {
        this.sharedService = sharedService;
    }

    // Создание сущности
    @Secured({"ROLE_OPERATOR", "ROLE_ADMIN"})
    @PostMapping("/{entity}")
    public ResponseEntity<?> createEntity(@PathVariable String entity, @RequestBody Map<String, Object> payload) {
        try {
            return ResponseEntity.ok(sharedService.createEntity(entity, payload));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Получение всех сущностей
    @GetMapping("/{entity}")
    public ResponseEntity<?> getAllEntities(@PathVariable String entity) {
        try {
            return ResponseEntity.ok(sharedService.getAllEntities(entity));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Удаление сущности по ID
    @Secured({"ROLE_OPERATOR", "ROLE_ADMIN"})
    @DeleteMapping("/{entity}/{id}")
    public ResponseEntity<?> deleteEntity(@PathVariable String entity, @PathVariable Integer id) {
        try {
            sharedService.deleteEntity(entity, id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}