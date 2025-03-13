package org.anas.bidderx_rest.service.dto.mapper;

import org.anas.bidderx_rest.domain.AppUser;
import org.anas.bidderx_rest.service.WalletService;
import org.anas.bidderx_rest.service.dto.ProfileDTO;
import org.anas.bidderx_rest.service.dto.WalletDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AppUserMapper {

    @Mapping(target = "hasWallet", source = "hasWallet")
    @Mapping(target = "collections", ignore = true)
    ProfileDTO toProfileDTO(AppUser user);

}
