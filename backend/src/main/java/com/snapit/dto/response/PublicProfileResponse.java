package com.snapit.dto.response;

import java.time.LocalDateTime;

public class PublicProfileResponse {
    private String username;
    private LocalDateTime memberSince;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LocalDateTime getMemberSince() {
        return memberSince;
    }

    public void setMemberSince(LocalDateTime memberSince) {
        this.memberSince = memberSince;
    }
}
