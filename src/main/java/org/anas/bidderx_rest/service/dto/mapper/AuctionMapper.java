package org.anas.bidderx_rest.service.dto.mapper;


import org.anas.bidderx_rest.domain.Auction;
import org.anas.bidderx_rest.service.dto.AuctionDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AuctionMapper {

    @Mapping(target = "product", source = "product")
    AuctionDTO toDTO(Auction auction);
}
