package git.dimitrikvirik.springsoft.common.model.dto;

import java.util.List;

public record PrincipalDTO(
        Long id,
        List<String> authorities
){}