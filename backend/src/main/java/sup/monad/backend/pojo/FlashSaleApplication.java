package sup.monad.backend.pojo;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Entity
@Table(name = "flash_sale_applications")
@JsonSerialize
@JsonDeserialize
@Data
public class FlashSaleApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @Column(name = "applicant_id")
    private Long applicantId;
    @Column(name = "product_id")
    private Long productId;
    @Column(name = "discount")
    private double discount;
    @Column(name = "total_quantity")
    private int totalQuantity;
    @Column(name = "start_time")
    private LocalDateTime startTime;
    @Column(name = "end_time")
    private LocalDateTime endTime;
    private String status; // pending, approved, rejected
    @Column(name = "operator_id")
    private Long operatorId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
