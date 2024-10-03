package git.dimitrikvirik.springsoft.user.model.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AuthDTO {

    private String token;
}
