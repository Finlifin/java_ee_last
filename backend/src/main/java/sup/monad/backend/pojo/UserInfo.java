package sup.monad.backend.pojo;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user_info")
@Getter
@Setter
public class UserInfo implements Serializable {
    @Id
    private Long id;
    private String username;
    private String email;
    private String role;
    private String avatar;
}
