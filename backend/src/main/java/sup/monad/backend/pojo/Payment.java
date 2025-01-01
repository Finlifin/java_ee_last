package sup.monad.backend.pojo;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    // snake case

    @Column(name = "payer_id")
    private Long payer;
    @Column(name = "payer_name")
    private String payerName;
    @Column(name = "payee_id")
    private Long payee;
    @Column(name = "payee_name")
    private String payeeName;
    @Column(name = "order_id")
    private Long orderId;
    @Column(name = "payment_method")
    private String paymentMethod;
    @Column(name = "payment_status")
    private String paymentStatus;
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime paymentTimestamp;
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime paymentUpdateTimestamp;
    @Column(name = "payment_amount")
    private Double paymentAmount;

    public Payment() {
        this.paymentStatus = "payed";
    }
}