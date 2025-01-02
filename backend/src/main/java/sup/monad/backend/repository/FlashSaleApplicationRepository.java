package sup.monad.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import sup.monad.backend.pojo.FlashSaleApplication;

public interface FlashSaleApplicationRepository extends JpaRepository<FlashSaleApplication, Long> {
    public List<FlashSaleApplication> findByApplicantId(Long applicantId);
}
