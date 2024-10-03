package git.dimitrikvirik.springsoft.user.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import git.dimitrikvirik.springsoft.user.model.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserDTO {

    @JsonProperty("firstname")
    private String firstname;

    @JsonProperty("lastname")
    private String lastname;

    @JsonProperty("username")
    private String username;

    @JsonProperty("email")
    private String email;

    @JsonProperty("password")
    private String createdAt;

    public static UserDTO fromEntity(User user) {
        return UserDTO.builder()
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .username(user.getUsername())
                .email(user.getEmail())
                .createdAt(user.getCreatedAt().toString())
                .build();
    }

}
