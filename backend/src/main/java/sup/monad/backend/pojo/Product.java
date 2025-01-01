package sup.monad.backend.pojo;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "products")
@JsonSerialize
@JsonDeserialize
@Getter
@Setter
public class Product implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String name;
    private String description;
    private Long seller;
    private Double price;
    private String img;
    // postgresql array
    @Column(name = "tags", columnDefinition = "varchar(32)[]")
    private List<String> tags;
    private int stock;
    private String state;
    
    @PrePersist
    public void prePersist() {
        if (state == null) {
            state = "active";
        }
    }
}