package org.anas.bidderx_rest.service.dto;

public class RefreshTokenResponseDTO {
    private String newAccessToken;

    public RefreshTokenResponseDTO() {
    }

    public RefreshTokenResponseDTO(String newAccessToken) {
        this.newAccessToken = newAccessToken;
    }

    public String getNewAccessToken() {
        return newAccessToken;
    }

    public void setNewAccessToken(String newAccessToken) {
        this.newAccessToken = newAccessToken;
    }
}
