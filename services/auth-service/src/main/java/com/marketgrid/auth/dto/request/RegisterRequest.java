package com.marketgrid.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "password") // never log passwords
public class RegisterRequest {

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 64, message = "Password must be between 8–64 characters")
    private String password;

    @NotBlank(message = "Role is required")
    @Pattern(
        regexp = "^(ADMIN|VENDOR|CUSTOMER)$",
        message = "Role must be ADMIN, VENDOR, or CUSTOMER"
    )
    private String role;
}
