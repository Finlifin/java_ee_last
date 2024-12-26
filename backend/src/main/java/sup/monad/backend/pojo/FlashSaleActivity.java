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
    private String name;
    private String description;
    private Double discount;
    private Long creatorId;
    @Column(name = "start_time")
    private LocalDateTime startTime;
    @Column(name = "end_time")
    private LocalDateTime endTime;
    @Column(name = "product_id")
    private Long productId;
    @Column(name = "limit_per_user")
    private Integer limitPerUser;
    @Column(name = "total_quantity")
    private Integer totalQuantity;
    @Column(name = "state", columnDefinition = "varchar(16) default 'created'")
    private String state;
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime creationDateTime;
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updateDateTime;
}
