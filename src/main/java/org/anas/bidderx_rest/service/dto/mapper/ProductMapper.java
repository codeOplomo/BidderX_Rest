package org.anas.bidderx_rest.service.dto.mapper;

import org.anas.bidderx_rest.domain.Product;
import org.anas.bidderx_rest.service.dto.ProductDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(source = "category", target = "category")
    @Mapping(source = "owner.email", target = "ownerEmail")
    ProductDTO toProductDTO(Product product);
}
