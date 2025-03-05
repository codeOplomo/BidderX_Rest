package org.anas.bidderx_rest.service.dto.mapper;


import org.anas.bidderx_rest.domain.Wallet;
import org.anas.bidderx_rest.service.dto.WalletDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WalletMapper {
    @Mapping(source = "currency", target = "currencyCode")
    @Mapping(source = "user.id", target = "userId")
    WalletDTO toDto(Wallet wallet);
}
