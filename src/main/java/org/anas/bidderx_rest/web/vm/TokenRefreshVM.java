package org.anas.bidderx_rest.web.vm;

public class TokenRefreshVM {
    private String refreshToken;

    public TokenRefreshVM() {
    }

    public String getRefreshToken() {
        return this.refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public TokenRefreshVM refreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
        return this;
    }

}
