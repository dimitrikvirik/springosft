package git.dimitrikvirik.springsoft.order.client;

import git.dimitrikvirik.springsoft.order.model.dto.PublicKeyDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "auth", url = "${api.user}")
public interface AuthApiClient {

    @GetMapping("/api/public-key")
    ResponseEntity<PublicKeyDTO> getPublicKey();

}
