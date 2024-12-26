package sup.monad.backend.pojo;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class Cart implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private List<Order> orders;
}
