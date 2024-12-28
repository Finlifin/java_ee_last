package sup.monad.backend.pojo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.redis.core.RedisHash;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@JsonSerialize
@JsonDeserialize
@RedisHash
public class Cart implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private List<Order> orders;
    
    public Cart() {
        this.orders = new ArrayList<Order>();
    }
}
