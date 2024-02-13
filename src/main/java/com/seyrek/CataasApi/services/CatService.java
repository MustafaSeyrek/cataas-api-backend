package com.seyrek.CataasApi.services;

import com.seyrek.CataasApi.entities.Cat;
import com.seyrek.CataasApi.repositories.CatRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.core.env.Environment;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class CatService {
    private final CatRepository catRepository;
    private final Path fileStorageLocation;

    @Autowired
    public CatService(CatRepository catRepository, Environment env) {
        this.catRepository = catRepository;

        this.fileStorageLocation = Paths.get(env.getProperty("app.file.upload-dir", "./uploads/files"))
                .toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }


    public Cat createCat(MultipartFile file) throws IOException {
        String name = StringUtils.cleanPath(file.getOriginalFilename());
        String code = RandomStringUtils.randomAlphanumeric(8);
        Path targetLocation = this.fileStorageLocation.resolve(code + "-" + name);
        Cat c = Cat.builder()
                .name(name)
                .path("/uploads/files/" + code + "-" + name)
                .code(code)
                .size(file.getSize())
                .build();
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        return catRepository.save(c);
    }

    public Resource getCatAsResource(String code) throws IOException {
        Path dirPath = this.fileStorageLocation;
        AtomicReference<Path> foundFile = new AtomicReference<>();
        Files.list(dirPath).forEach(file -> {
            if (file.getFileName().toString().startsWith(code)) {
                foundFile.set(file);
                return;
            }
        });
        if (foundFile.get() != null) {
            return new UrlResource(foundFile.get().toUri());
        }
        return null;
    }

    public Stream<Cat> getAllCats() {
        return catRepository.findAll().stream();
    }

    public Cat findCatByCode(String code) {
        return catRepository.findByCode(code);
    }

    public void deleteByCode(String code) {
        catRepository.deleteByCode(code);
    }

    public Resource getCatFromApi() {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://cataas.com/cat/funny?position=centre";
        ResponseEntity<Resource> response = restTemplate.getForEntity(url, Resource.class);
        Resource res = response.getBody();
        return res;
    }
}
