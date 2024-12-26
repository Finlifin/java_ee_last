package sup.monad.backend.pojo;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "orders")
@JsonSerialize
@JsonDeserialize
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "customer_id")
    private Long customerId;
    @Column(name = "product_id")
    private Long productId;
    @Column(name = "total_amount")
    private Double totalAmount;
    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime creationDateTime;
    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updateDateTime;
    // pending, payed, shipped, delivered, canceled, returned, refunded, overdue
    private String state;
    private int quantity;

    public Order() {
    }

    public Order(Long customerId, Long productId, Double totalAmount, String state, int quantity) {
        this.customerId = customerId;
        this.productId = productId;
        this.totalAmount = totalAmount;
        this.state = state;
        this.quantity = quantity;
    }
}
