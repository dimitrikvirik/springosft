package git.dimitrikvirik.springsoft.common.model.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class ErrorDTO {

    private String message;
    private Date timestamp;
}
