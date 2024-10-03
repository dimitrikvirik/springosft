package git.dimitrikvirik.springsoft.user.model.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "user_role")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserRole extends BaseDomain {

    @Column(name = "name")
    private String name;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_role_authority", joinColumns = @JoinColumn(name = "user_role_id"))
    @Column(name = "authority")
    private List<String> authorities;


}
