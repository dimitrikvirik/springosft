package git.dimitrikvirik.springsoft.user.controller;

import git.dimitrikvirik.springsoft.user.model.dto.AuthDTO;
import git.dimitrikvirik.springsoft.user.model.dto.PublicKeyDTO;
import git.dimitrikvirik.springsoft.user.model.param.UserLoginParam;
import git.dimitrikvirik.springsoft.user.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Auth Management", description = "APIs for managing authentication")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "User login", description = "Authenticates a user and returns an authentication token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully authenticated",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = AuthDTO.class))}),
            @ApiResponse(responseCode = "401", description = "Invalid credentials",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content)
    })
    @PostMapping("/api/login")
    public ResponseEntity<AuthDTO> login(
            @RequestBody UserLoginParam loginParam
    ) {
        return ResponseEntity.ok(authService.getToken(loginParam));
    }

    @Operation(summary = "Get public key", description = "Get public key")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved public key")
    })
    @GetMapping("/api/public-key")
    public ResponseEntity<PublicKeyDTO> getPublicKey() {
        return ResponseEntity.ok(authService.getPublicKey());
    }
}
