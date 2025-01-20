package org.anas.bidderx_rest.service.dto.mapper;

import org.anas.bidderx_rest.domain.AppUser;
import org.anas.bidderx_rest.service.dto.ProfileDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AppUserMapper {

    ProfileDTO toProfileDTO(AppUser user);

}
