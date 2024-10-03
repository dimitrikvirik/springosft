package git.dimitrikvirik.springsoft.user.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PublicKeyDTO {

    public String key;
}
