package git.dimitrikvirik.springsoft.user.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import git.dimitrikvirik.springsoft.user.model.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.format.DateTimeFormatter;

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

    @JsonProperty("createdAt")
    private String createdAt;

    public static UserDTO fromEntity(User user) {

        return UserDTO.builder()
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .username(user.getUsername())
                .email(user.getEmail())
                .createdAt(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(user.getCreatedAt()))
                .build();
    }

}
