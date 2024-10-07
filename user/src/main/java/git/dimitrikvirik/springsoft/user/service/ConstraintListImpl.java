package git.dimitrikvirik.springsoft.user.service;

import git.dimitrikvirik.springsoft.common.services.ConstraintList;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ConstraintListImpl implements ConstraintList {
    @Override
    public Map<String, String> getConstraintList() {
        return Map.of("uk_users_email", "An account with this email already exists.", "uk_users_username", "This username is already taken.");
    }
}
