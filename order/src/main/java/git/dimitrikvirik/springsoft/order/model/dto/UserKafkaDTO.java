package git.dimitrikvirik.springsoft.order.model.dto;

public record UserKafkaDTO(
        Long userId,
        String username,
        Boolean enabled
) {
}
