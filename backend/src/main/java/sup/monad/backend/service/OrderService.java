package sup.monad.backend.service;

import sup.monad.backend.pojo.Order;
import sup.monad.backend.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService implements IOrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Override
    public Order createOrder(Order order) {
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
        return orderRepository.save(order);
    }

    @Override
    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
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
