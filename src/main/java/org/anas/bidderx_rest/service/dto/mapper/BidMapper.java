package org.anas.bidderx_rest.service.dto.mapper;


import org.anas.bidderx_rest.domain.Bid;
import org.anas.bidderx_rest.service.dto.BidDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BidMapper {

    @Mapping(target = "bidId", source = "id")
    @Mapping(target = "auctionId", source = "auction.id")
    @Mapping(target = "bidder", source = "user")
    BidDTO toBidDTO(Bid bid);
}
