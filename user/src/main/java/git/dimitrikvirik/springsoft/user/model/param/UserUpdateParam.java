package git.dimitrikvirik.springsoft.user.model.param;

import com.fasterxml.jackson.annotation.JsonProperty;
import git.dimitrikvirik.springsoft.user.model.UserRoleName;
import git.dimitrikvirik.springsoft.user.validator.ValidEmail;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateParam {

    @JsonProperty("firstname")
    @NotBlank
    @Size(min = 2, max = 50)
    private String firstname;

    @JsonProperty("lastname")
    @NotBlank
    @Size(min = 2, max = 50)
    private String lastname;

    @JsonProperty("username")
    @NotBlank
    @Size(min = 2, max = 50)
    private String username;

    @JsonProperty("email")
    @ValidEmail
    private String email;


    @JsonProperty("role")
    @NotNull
    private UserRoleName role;
}
