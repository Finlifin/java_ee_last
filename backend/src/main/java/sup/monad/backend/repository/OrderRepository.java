package sup.monad.backend.repository;

import sup.monad.backend.pojo.Order;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Transactional
    @Query("SELECT o FROM Order o WHERE o.customerId = :customerId")
    List<Order> allOfCustomer(Long customerId);
}
