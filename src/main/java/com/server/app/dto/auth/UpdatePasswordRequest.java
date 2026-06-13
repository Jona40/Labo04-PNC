package com.server.app.dto.auth;
import lombok.Data;

@Data
public class UpdatePasswordRequest {
    private String oldpassword;
    private String newpassword;
    private String confirmpassword;
}