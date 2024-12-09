package org.anas.bidderx_rest.config;

import org.anas.bidderx_rest.domain.enums.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;
import java.util.stream.Collectors;

public class RoleAuthorityMapper {

    public static Set<GrantedAuthority> getAuthorities(Role role) {
        // Add role itself as an authority
        Set<GrantedAuthority> authorities = role.getPermissions()
                .stream()
                .map(permission -> new SimpleGrantedAuthority(permission.name()))
                .collect(Collectors.toSet());

        // Add the role as a granted authority
        authorities.add(new SimpleGrantedAuthority("ROLE_" + role.name()));
        return authorities;
    }
}
