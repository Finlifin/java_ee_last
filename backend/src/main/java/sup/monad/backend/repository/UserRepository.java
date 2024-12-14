package  sup.monad.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sup.monad.backend.pojo.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
}
