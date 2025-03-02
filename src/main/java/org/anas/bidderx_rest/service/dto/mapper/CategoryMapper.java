package org.anas.bidderx_rest.service.dto.mapper;


import org.anas.bidderx_rest.domain.Category;
import org.anas.bidderx_rest.service.dto.CategoryDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

        CategoryDTO toCategoryDTO(Category category);
}
