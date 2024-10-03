package git.dimitrikvirik.springsoft.user.model.dto;

public record UserKafkaDTO(
        Long userId,
        String username,
        Boolean enabled
) {
}
