package com.snapit.dto.response;

public class AuthResponse {
    private String accessToken;
    private String tokenType = "Bearer";
    private Long Id;
    private String username;
    private String email;

    public AuthResponse(){}

    public AuthResponse(String accessToken, String tokenType, Long id, String username, String email) {
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        Id = id;
        this.username = username;
        this.email = email;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
