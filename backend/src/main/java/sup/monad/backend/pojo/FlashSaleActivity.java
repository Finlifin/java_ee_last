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
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.*;
import lombok.Data;

@Entity
@Table(name = "flash_sale_activities")
@JsonSerialize
@JsonDeserialize
@Data
public class FlashSaleActivity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotBlank(message = "Name is mandatory")
    private String name;

    @NotBlank(message = "Description is mandatory")
    private String description;

    @DecimalMin(value = "0.0", inclusive = true, message = "Discount must be between 0 and 1")
    @DecimalMax(value = "1.0", inclusive = true, message = "Discount must be between 0 and 1")
    private Double discount;

    @NotNull(message = "Creator ID is mandatory")
    private Long creatorId;

    @NotNull(message = "Start time is mandatory")
    @PastOrPresent(message = "Start time must be in the past or present")
    @Column(name = "start_time")
    private LocalDateTime startTime;

    @NotNull(message = "End time is mandatory")
    @Future(message = "End time must be in the future")
    @Column(name = "end_time")
    private LocalDateTime endTime;

    @NotNull(message = "Product ID is mandatory")
    @Column(name = "product_id")
    private Long productId;

    @NotNull(message = "Limit per user is mandatory")
    @Min(value = 1, message = "Limit per user must be at least 1")
    @Column(name = "limit_per_user")
    private Integer limitPerUser;

    @NotNull(message = "Total quantity is mandatory")
    @Min(value = 1, message = "Total quantity must be at least 1")
    @Column(name = "total_quantity")
    private Integer totalQuantity;

    private String state;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime creationDateTime;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updateDateTime;

    @PrePersist
    public void prePersist() {
        if (state == null) {
            state = "created";
        }
    }
}
