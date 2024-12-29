package sup.monad.backend.pojo;

import java.io.Serializable;
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
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "orders")
@JsonSerialize
@JsonDeserialize
public class Order implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

     @NotNull(message = "Customer ID is mandatory.")
    @Column(name = "customer_id")
    private Long customerId;

    @NotNull(message = "Product ID is mandatory.")
    @Column(name = "product_id")
    private Long productId;

    @NotNull(message = "Total amount is mandatory.")
    @Positive(message = "Total amount must be positive.")
    @Column(name = "total_amount")
    private Double totalAmount;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime creationDateTime;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updateDateTime;

    @Pattern(regexp = "pending|payed|shipped|delivered|cancelled|returned|refunded|overdue", message = "State must be one of: pending, payed, shipped, delivered, cancelled, returned, refunded, overdue.")
    private String state;

    @NotNull(message = "Quantity is mandatory.")
    @Min(value = 1, message = "Quantity must be at least 1.")
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
