package sup.monad.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;
import sup.monad.backend.pojo.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    @Transactional
    @Query("SELECT p FROM Payment p WHERE p.payer = :payerId")
    List<Payment> findByPayerId(Long payerId);

    @Transactional
    @Query("SELECT p FROM Payment p WHERE p.payee = :payeeId")
    List<Payment> findByPayeeId(Long payeeId);
}
