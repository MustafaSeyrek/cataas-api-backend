package com.seyrek.CataasApi.repositories;

import com.seyrek.CataasApi.entities.Cat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CatRepository extends JpaRepository<Cat, Integer> {
    void deleteByCode(String code);

    Cat findByCode(String code);
}
