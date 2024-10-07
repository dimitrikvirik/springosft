package git.dimitrikvirik.springsoft.common.model.dto;

public record UserKafkaDTO(
        Long userId,
        String username,
        Boolean enabled
) {
}
