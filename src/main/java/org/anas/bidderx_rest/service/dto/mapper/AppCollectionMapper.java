package org.anas.bidderx_rest.service.dto.mapper;

import org.anas.bidderx_rest.domain.AppCollection;
import org.anas.bidderx_rest.domain.ProductCollection;
import org.anas.bidderx_rest.service.dto.CollectionDTO;
import org.anas.bidderx_rest.service.dto.ItemDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AppCollectionMapper {


    @Mapping(target = "items", source = "productCollections")
    @Mapping(target = "imageUrl", source = "imageUrl")
    CollectionDTO toCollectionDTO(AppCollection collection);

//    @Mapping(target = "imageUrl", source = "product.imageUrl") // Assuming you have this field
    @Mapping(target = "id", source = "product.id")
    @Mapping(target = "name", source = "product.title")
    @Mapping(target = "description", source = "product.description")
    ItemDTO toItemDTO(ProductCollection productCollection);
}
