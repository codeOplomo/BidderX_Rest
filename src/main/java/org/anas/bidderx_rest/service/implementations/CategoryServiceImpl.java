package org.anas.bidderx_rest.service.implementations;

import org.anas.bidderx_rest.domain.Category;
import org.anas.bidderx_rest.exceptions.CategoryNotFoundException;
import org.anas.bidderx_rest.repository.CategoryRepository;
import org.anas.bidderx_rest.service.CategoryService;
import org.anas.bidderx_rest.service.dto.CategoryDTO;
import org.anas.bidderx_rest.service.dto.mapper.CategoryMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryServiceImpl(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }


    @Override
    public List<CategoryDTO> listCategories() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream()
                .map(categoryMapper::toCategoryDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDTO getCategoryById(UUID id) {
        return categoryRepository.findById(id)
                .map(categoryMapper::toCategoryDTO)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found"));
    }

}
