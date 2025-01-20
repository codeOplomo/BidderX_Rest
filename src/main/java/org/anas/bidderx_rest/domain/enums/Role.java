package org.anas.bidderx_rest.domain.enums;

import java.util.Set;

public enum Role {
    ADMIN(Set.of(Permission.READ_BIDS, Permission.CREATE_BIDS, Permission.DELETE_BIDS, Permission.MANAGE_USERS, Permission.VIEW_DASHBOARD, Permission.UPDATE_SETTINGS)),
    OWNER(Set.of(Permission.READ_BIDS, Permission.CREATE_BIDS, Permission.VIEW_DASHBOARD)),
    BIDDER(Set.of(Permission.READ_BIDS, Permission.CREATE_BIDS));

    private final Set<Permission> permissions;

    Role(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }
}
