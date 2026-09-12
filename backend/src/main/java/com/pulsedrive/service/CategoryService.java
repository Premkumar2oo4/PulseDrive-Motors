package com.pulsedrive.service;

import com.pulsedrive.dto.CategoryRequestDTO;
import com.pulsedrive.dto.CategoryResponseDTO;
import com.pulsedrive.entity.Category;
import com.pulsedrive.exception.ResourceNotFoundException;
import com.pulsedrive.mapper.CategoryMapper;
import com.pulsedrive.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import com.pulsedrive.exception.DuplicateResourceException;
import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryService(
            CategoryRepository categoryRepository,
            CategoryMapper categoryMapper) {

        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    public List<CategoryResponseDTO> getAllCategories() {

        return categoryRepository.findAll()
                .stream()
                .map(categoryMapper::toResponseDTO)
                .toList();
    }

    public CategoryResponseDTO getCategoryById(Long id) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category not found with id: " + id));

        return categoryMapper.toResponseDTO(category);
    }

    public CategoryResponseDTO createCategory(
            CategoryRequestDTO dto) {

        if (categoryRepository.existsByName(dto.getName())) {
            throw new DuplicateResourceException(
                    "Category already exists with name: "
                            + dto.getName());
        }

        Category category = categoryMapper.toEntity(dto);

        Category savedCategory = categoryRepository.save(category);

        return categoryMapper.toResponseDTO(savedCategory);
    }

    public CategoryResponseDTO updateCategory(
            Long id,
            CategoryRequestDTO dto) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category not found with id: " + id));

        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        category.setImageUrl(dto.getImageUrl());

        Category updatedCategory = categoryRepository.save(category);

        return categoryMapper.toResponseDTO(updatedCategory);
    }

    public void deleteCategory(Long id) {

        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Category not found with id: " + id);
        }

        categoryRepository.deleteById(id);
    }
}