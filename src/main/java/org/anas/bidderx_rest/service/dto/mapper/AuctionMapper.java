package org.anas.bidderx_rest.service.dto.mapper;


import org.anas.bidderx_rest.domain.Auction;
import org.anas.bidderx_rest.service.dto.AuctionDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AuctionMapper {

    @Mapping(target = "product", source = "product")
    @Mapping(target = "type", source = "auctionType")
    @Mapping(target = "reactionsCount", expression = "java(auction.getAuctionReactions() != null ? auction.getAuctionReactions().size() : 0)")
    @Mapping(target = "likedByCurrentUser", ignore = true)
    @Mapping(target = "duration", expression = "java(auction.getAuctionDurationInHours() != null ? auction.getAuctionDurationInHours() : 0)")
    AuctionDTO toDTO(Auction auction);
}
