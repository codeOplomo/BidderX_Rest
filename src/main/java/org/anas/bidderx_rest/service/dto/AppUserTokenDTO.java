package org.anas.bidderx_rest.service.dto;

public class AppUserTokenDTO {
    private String token;

    public AppUserTokenDTO() {
    }

    public AppUserTokenDTO(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
