package git.dimitrikvirik.springsoft.user.controller;

import git.dimitrikvirik.springsoft.user.facade.UserFacade;
import git.dimitrikvirik.springsoft.user.model.dto.UserDTO;
import git.dimitrikvirik.springsoft.user.model.entity.User;
import git.dimitrikvirik.springsoft.user.model.param.UserCreateParam;
import git.dimitrikvirik.springsoft.user.model.param.UserUpdateParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Management", description = "APIs for managing users")
@RequiredArgsConstructor
public class UserController {

    private final UserFacade userFacade;


    @Operation(summary = "Get all users", description = "Retrieves a list of all users in the system")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of users",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = UserDTO.class)))
    @GetMapping
    @PageableAsQueryParam
    @PreAuthorize("hasAuthority('GET_USERS')")
    public ResponseEntity<Page<UserDTO>> getAllUsers(
            @PageableDefault(size = 50, sort="id", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(userFacade.getAllUsers(pageable));
    }

    @Operation(summary = "Get a user by ID", description = "Retrieves a specific user by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the user",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content)
    })
    @GetMapping("/{id}")
    @PostAuthorize("returnObject.username == authentication.name or hasAuthority('GET_USERS')")
    public ResponseEntity<UserDTO> getUserById(
            @Parameter(description = "ID of the user to be retrieved") @PathVariable Long id) {
        return ResponseEntity.ok(userFacade.getUserById(id));

    }

    @Operation(summary = "Create a new user", description = "Adds a new user to the system")
    @ApiResponse(responseCode = "201", description = "User successfully created",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = UserDTO.class)))
    @PostMapping
    public ResponseEntity<UserDTO> createUser(
            @Parameter(description = "User object to be created") @RequestBody UserCreateParam user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userFacade.createUser(user));
    }

    @Operation(summary = "Update a user", description = "Updates an existing user's information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User successfully updated",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content)
    })
    @PreAuthorize("hasAuthority('UPDATE_USER') or #id == authentication.principal.id")
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(
            @Parameter(description = "ID of the user to be updated") @PathVariable Long id,
            @Parameter(description = "Updated user object") @RequestBody UserUpdateParam user) {
        return ResponseEntity.ok(userFacade.updateUser(id, user));
    }

    @Operation(summary = "Delete a user", description = "Removes a user from the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User successfully deleted"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PreAuthorize("hasAuthority('DELETE_USER') or #id == authentication.principal.id")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "ID of the user to be deleted") @PathVariable Long id) {
        userFacade.deleteUser(id);
        return ResponseEntity.noContent().build();

    }
}