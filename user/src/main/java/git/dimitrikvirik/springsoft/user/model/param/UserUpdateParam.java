package git.dimitrikvirik.springsoft.user.model.param;

import com.fasterxml.jackson.annotation.JsonProperty;
import git.dimitrikvirik.springsoft.user.validator.ValidEmail;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
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

    @JsonProperty("password")
    @NotBlank
    @Size(min = 8, max = 100)
    private String password;

}
