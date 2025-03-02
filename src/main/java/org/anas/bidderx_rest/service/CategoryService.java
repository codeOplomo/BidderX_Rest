package org.anas.bidderx_rest.service;

import org.anas.bidderx_rest.service.dto.CategoryDTO;

import java.util.List;
import java.util.UUID;

public interface CategoryService {
    List<CategoryDTO> listCategories();

    CategoryDTO getCategoryById(UUID id);
}
