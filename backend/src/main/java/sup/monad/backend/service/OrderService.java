package sup.monad.backend.service;

import sup.monad.backend.exception.CustomException;
import sup.monad.backend.pojo.Order;
import sup.monad.backend.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import jakarta.validation.Validator;

import java.util.List;
import java.util.Set;

@Service
public class OrderService implements IOrderService {

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderRepository orderRepository;

    
    @Autowired
    private Validator validator;

    @Override
    public Order createOrder(@Valid Order order) {
        Set<ConstraintViolation<Order>> violations = validator.validate(order);
        if (!violations.isEmpty()) {
            StringBuilder errorMessages = new StringBuilder();
            for (ConstraintViolation<Order> violation : violations) {
                errorMessages.append(violation.getMessage()).append(", ");
            }
            throw new CustomException(errorMessages.toString(), HttpStatus.BAD_REQUEST);
        }
        
        //check if stock enough
        if (order.getQuantity() > productService.findProductById(order.getProductId()).getStock()) {
            throw new CustomException("Not enough stock", HttpStatus.BAD_REQUEST);
        }
        return orderRepository.save(order);
    }

    @Override
    public Order getOrderById(Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public Order updateOrder(Order order) {
        Set<ConstraintViolation<Order>> violations = validator.validate(order);
        if (!violations.isEmpty()) {
            StringBuilder errorMessages = new StringBuilder();
            for (ConstraintViolation<Order> violation : violations) {
                errorMessages.append(violation.getMessage()).append(", ");
            }
            throw new CustomException(errorMessages.toString(), HttpStatus.BAD_REQUEST);
        }
            //check if stock enough
        if (order.getQuantity() > productService.findProductById(order.getProductId()).getStock()) {
            throw new CustomException("Not enough stock", HttpStatus.BAD_REQUEST);
        }
        return orderRepository.save(order);
    }

    @Override
    public void deleteOrder(Long id) {
        if (!orderRepository.findById(id).isEmpty()) {
            orderRepository.deleteById(id);
        }
    }

    @Override
    public List<Order> allOfCustomer(Long customerId) {
        return orderRepository.allOfCustomer(customerId);
    }

    @Override
    public void payOrder(Long orderId) {
        var order = orderRepository.findById(orderId).orElse(null);
        if (order != null) {
            order.setState("payed");
            orderRepository.save(order);
        }
    }
}
