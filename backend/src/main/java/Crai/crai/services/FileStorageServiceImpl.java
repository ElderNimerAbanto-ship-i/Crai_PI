package Crai.crai.services;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements IFileStorageService {

    @Value("${storage.location}")
    private String storageLocation;

    @Override
    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(Paths.get(storageLocation));
        } catch (IOException e) {
            throw new RuntimeException("No se pudo inicializar la carpeta de imágenes");
        }
    }

    @Override
    public String store(MultipartFile file) {
        if (file.isEmpty()) throw new IllegalArgumentException("El archivo está vacío");

        // Solo se aceptan imágenes
        String tipo = file.getContentType();
        if (tipo == null || !tipo.startsWith("image/")) {
            throw new IllegalArgumentException("El archivo debe ser una imagen (jpg, png, webp...)");
        }

        try {
            // Nombre único para evitar duplicados (ej: uuid_nombre.jpg); se descarta cualquier ruta del nombre original
            String original = file.getOriginalFilename() != null
                    ? Paths.get(file.getOriginalFilename()).getFileName().toString()
                    : "imagen";
            String filename = UUID.randomUUID() + "_" + original;
            Path destinationFile = Paths.get(storageLocation).resolve(filename);

            Files.copy(file.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);
            return filename;
        } catch (IOException e) {
            throw new RuntimeException("Error al guardar el archivo", e);
        }
    }

    @Override
    public void delete(String filename) {
        try {
            Path file = Paths.get(storageLocation).resolve(filename);
            Files.deleteIfExists(file);
        } catch (IOException e) {
            System.err.println("No se pudo borrar el archivo: " + filename);
        }
    }
}
