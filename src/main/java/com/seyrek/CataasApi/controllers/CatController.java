package com.seyrek.CataasApi.controllers;

import com.seyrek.CataasApi.entities.Cat;
import com.seyrek.CataasApi.services.CatService;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/cats")
@AllArgsConstructor
public class CatController {
    private final CatService catService;

    /*@GetMapping
    public ResponseEntity<Resource> getFileByCode() {
        Resource resource = null;

        return new ResponseEntity<>(catService.getCatFromApi(), OK);

    }*/

    @GetMapping
    public ResponseEntity<List<Cat>> getAllFiles() {
        List<Cat> files = catService.getAllCats().map(db -> {
            return new Cat(
                    db.getId(),
                    db.getName(),
                    db.getCode(),
                    db.getPath(),
                    db.getSize()
            );
        }).collect(Collectors.toList());
        return ResponseEntity.status(OK).body(files);
    }

    @PostMapping
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            catService.createCat(file);
            return new ResponseEntity<>("File uploaded successfully!", OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.EXPECTATION_FAILED);
        }
    }

}
