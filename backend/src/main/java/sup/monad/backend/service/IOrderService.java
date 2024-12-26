package sup.monad.backend.service;


import sup.monad.backend.pojo.Order;
import java.util.List;

public interface IOrderService {
    Order createOrder(Order order);
    Order getOrderById(Long id);
    List<Order> getAllOrders();
    Order updateOrder(Order order);
    void deleteOrder(Long id);
    List<Order> allOfCustomer(Long customerId);
    void payOrder(Long orderId);
}
