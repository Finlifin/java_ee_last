package sup.monad.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sup.monad.backend.pojo.FlashSaleActivity;

public interface FlashSaleRepository extends JpaRepository<FlashSaleActivity, Long> {
    // Additional query methods can be defined here
}
