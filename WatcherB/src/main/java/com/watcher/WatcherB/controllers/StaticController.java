package com.watcher.WatcherB.controllers;
import com.watcher.WatcherB.DTO.ErrorResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/static")
public class StaticController {
    private final String imageDirectory = "uploads/product-images/";
    private final List<String> allowedMimeTypes = Arrays.asList("image/jpeg", "image/png");

    public StaticController() {
        // Создаём папку для загрузки изображений, если её ещё нет
        Path path = Paths.get(imageDirectory);
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                throw new RuntimeException("Не удалось создать директорию для изображений", e);
            }
        }
    }

    @PostMapping("/image")
    public ResponseEntity<?> uploadImage(
            @RequestParam("image") MultipartFile imageFile
    ) {
        try {
            if (imageFile.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("Файл не может быть пустым"));
            }

            // Проверка MIME-типа
            String mimeType = imageFile.getContentType();
            if (mimeType == null || !allowedMimeTypes.contains(mimeType)) {
                return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                        .body("Недопустимый формат файла. Разрешены только JPEG, PNG, GIF");
            }

            // Сохранение файла
            String fileName = "image-" + "-" + System.currentTimeMillis() + "-" + imageFile.getOriginalFilename();
            Path filePath = Paths.get(imageDirectory + fileName);
            Files.write(filePath, imageFile.getBytes());
            System.out.println("Байты: "+imageFile.getBytes().length);

            return ResponseEntity.ok("{\"file\":\""+fileName+"\"}");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Ошибка загрузки файла"));
        }
    }

    @GetMapping("/image/{fileName}")
    public ResponseEntity<?> getImage(
            @PathVariable String fileName
    ) {
        try {
            Path filePath = Paths.get(imageDirectory + fileName);
            if (!Files.exists(filePath)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("Файл не найден"));
            }

            // Определение MIME-типа
            String mimeType = Files.probeContentType(filePath);
            if (mimeType == null) {
                mimeType = "application/octet-stream";
            }

            byte[] imageBytes = Files.readAllBytes(filePath);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, mimeType)
                    .body(imageBytes);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Ошибка при сериализации"));
        }
    }
}
