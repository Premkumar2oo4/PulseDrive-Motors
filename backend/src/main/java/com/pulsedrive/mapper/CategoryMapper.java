package com.pulsedrive.mapper;

import com.pulsedrive.dto.CategoryRequestDTO;
import com.pulsedrive.dto.CategoryResponseDTO;
import com.pulsedrive.entity.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public Category toEntity(CategoryRequestDTO dto) {

        Category category = new Category();

        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        category.setImageUrl(dto.getImageUrl());

        return category;
    }

    public CategoryResponseDTO toResponseDTO(Category category) {

        CategoryResponseDTO dto = new CategoryResponseDTO();

        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        dto.setImageUrl(category.getImageUrl());

        return dto;
    }
}