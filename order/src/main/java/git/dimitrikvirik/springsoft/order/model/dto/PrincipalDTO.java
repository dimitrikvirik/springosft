package git.dimitrikvirik.springsoft.order.model.dto;

import java.util.List;

public record PrincipalDTO(
        Long id,
        List<String> authorities
){}