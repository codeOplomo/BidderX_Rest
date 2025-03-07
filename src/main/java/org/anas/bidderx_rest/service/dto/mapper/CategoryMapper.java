package org.anas.bidderx_rest.service.dto.mapper;


import org.anas.bidderx_rest.domain.Category;
import org.anas.bidderx_rest.repository.CategoryRepository;
import org.anas.bidderx_rest.service.dto.CategoryDTO;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
        @Mapping(target = "productsCount", expression = "java(countProducts(category, categoryRepository))")
        CategoryDTO toCategoryDTO(Category category, @Context CategoryRepository categoryRepository);

        default Long countProducts(Category category, @Context CategoryRepository categoryRepository) {
                return categoryRepository.countProductsByCategoryId(category.getId());
        }
}
