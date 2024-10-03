package git.dimitrikvirik.springsoft.user.model.param;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UserLoginParam {

    @JsonProperty("username")
    private String username;

    @JsonProperty("password")
    private String password;
}
