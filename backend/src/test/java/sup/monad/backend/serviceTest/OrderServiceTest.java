package sup.monad.backend.serviceTest;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.OngoingStubbing;
import org.springframework.http.HttpStatus;
import sup.monad.backend.exception.CustomException;
import sup.monad.backend.pojo.Order;
import sup.monad.backend.pojo.Product;
import sup.monad.backend.repository.OrderRepository;
import sup.monad.backend.service.OrderService;
import sup.monad.backend.service.ProductService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private ProductService productService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private Validator validator;

    @InjectMocks
    private OrderService orderService;

    @Test
    void createOrder_ShouldThrowException_WhenOrderIsInvalid() {
        // 模拟验证器返回违反约束的情况（返回一个包含模拟违反约束的集合）
        ConstraintViolation<Order> violation = Mockito.mock(ConstraintViolation.class);
        when(violation.getMessage()).thenReturn("Quantity must be at least 1");
        Set<ConstraintViolation<Order>> mockSet = Set.of(violation);
        when(validator.validate(any(Order.class))).thenReturn(mockSet);

        Order invalidOrder = new Order();
        // 假设这里的quantity为-1，违反了NotNull和Min(value = 1)约束
        invalidOrder.setQuantity(-1);

        assertThrows(CustomException.class, () -> {
            orderService.createOrder(invalidOrder);
        });
    }

    @Test
    void createOrder_ShouldThrowException_WhenStockIsNotEnough() {
        // 模拟产品库存不足
        Long productId = 1L;
        Product product = new Product();
        product.setStock(0);
        when(productService.findProductById(productId)).thenReturn(product);

        Order order = new Order();
        order.setProductId(productId);
        order.setQuantity(1);

        assertThrows(CustomException.class, () -> {
            orderService.createOrder(order);
        });
    }

    @Test
    void createOrder_ShouldCreateOrderSuccessfully() {
        // 模拟验证器不返回违反约束的情况（返回一个空的约束违反集合）
        Set<ConstraintViolation<Order>> emptyViolations = Set.of();
        when(validator.validate(any(Order.class))).thenReturn(emptyViolations);

        // 模拟产品库存足够且产品不为null
        Long productId = 1L;
        Product product = new Product();
        product.setStock(10);
        when(productService.findProductById(productId)).thenReturn(product);

        Order order = new Order();
        order.setProductId(productId);
        order.setQuantity(5);

        // 模拟保存订单成功
        Order savedOrder = new Order();
        savedOrder.setId(1L);
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        Order result = orderService.createOrder(order);
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getOrderById() {
        Long orderId = 1L;
        Order order = new Order();
        order.setId(orderId);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        Order result = orderService.getOrderById(orderId);
        assertNotNull(result);
        assertEquals(orderId, result.getId());
    }

    @Test
    void getOrderById_ShouldReturnNull_WhenOrderNotFound() {
        Long orderId = 1L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        Order result = orderService.getOrderById(orderId);
        assertNull(result);
    }

    @Test
    void getAllOrders() {
        List<Order> orders = new ArrayList<>();
        Order order1 = new Order();
        order1.setId(1L);
        Order order2 = new Order();
        order2.setId(2L);
        orders.add(order1);
        orders.add(order2);

        when(orderRepository.findAll()).thenReturn(orders);

        List<Order> result = orderService.getAllOrders();
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void updateOrder_ShouldThrowException_WhenOrderIsInvalid() {
        // 模拟验证器返回违反约束的情况（返回一个包含模拟违反约束的集合）
        ConstraintViolation<Order> violation = Mockito.mock(ConstraintViolation.class);
        when(violation.getMessage()).thenReturn("Quantity must be at least 1");
        Set<ConstraintViolation<Order>> mockSet = Set.of(violation);
        when(validator.validate(any(Order.class))).thenReturn(mockSet);

        Order invalidOrder = new Order();
        // 假设这里的quantity为-1，违反了NotNull和Min(value = 1)约束
        invalidOrder.setQuantity(-1);

        assertThrows(CustomException.class, () -> {
            orderService.updateOrder(invalidOrder);
        });
    }

    @Test
    void updateOrder_ShouldThrowException_WhenStockIsNotEnough() {
        // 模拟产品库存不足
        Long productId = 1L;
        Product product = new Product();
        product.setStock(0);
        when(productService.findProductById(productId)).thenReturn(product);

        Order order = new Order();
        order.setProductId(productId);
        order.setQuantity(1);

        assertThrows(CustomException.class, () -> {
            orderService.updateOrder(order);
        });
    }

    @Test
    void updateOrder_ShouldUpdateOrderSuccessfully() {
        // 模拟验证器不返回违反约束的情况（返回一个空的约束违反集合）
        Set<ConstraintViolation<Order>> emptyViolations = Set.of();
        when(validator.validate(any(Order.class))).thenReturn(emptyViolations);

        // 模拟产品库存足够
        Long productId = 1L;
        Product product = new Product();
        product.setStock(10);
        when(productService.findProductById(productId)).thenReturn(product);

        Order order = new Order();
        order.setId(1L);
        order.setProductId(productId);
        order.setQuantity(5);

        // 模拟保存订单成功
        Order updatedOrder = new Order();
        updatedOrder.setId(1L);
        updatedOrder.setQuantity(5);
        when(orderRepository.save(any(Order.class))).thenReturn(updatedOrder);

        Order result = orderService.updateOrder(order);
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(5, result.getQuantity());
    }

    @Test
    void deleteOrder() {
        Long orderId = 1L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(new Order()));

        orderService.deleteOrder(orderId);

        Mockito.verify(orderRepository).deleteById(orderId);
    }

    @Test
    void deleteOrder_ShouldDoNothing_WhenOrderNotFound() {
        Long orderId = 1L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        orderService.deleteOrder(orderId);

        Mockito.verify(orderRepository, Mockito.never()).deleteById(orderId);
    }

    @Test
    void allOfCustomer() {
        Long customerId = 1L;
        List<Order> orders = new ArrayList<>();
        Order order1 = new Order();
        order1.setCustomerId(customerId);
        Order order2 = new Order();
        order2.setCustomerId(customerId);
        orders.add(order1);
        orders.add(order2);

        when(orderRepository.allOfCustomer(customerId)).thenReturn(orders);

        List<Order> result = orderService.allOfCustomer(customerId);
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void payOrder() {
        Long orderId = 1L;
        Order order = new Order();
        order.setId(orderId);
        order.setState("pending");

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        orderService.payOrder(orderId);

        assertEquals("payed", order.getState());
        Mockito.verify(orderRepository).save(order);
    }

    @Test
    void payOrder_ShouldDoNothing_WhenOrderNotFound() {
        Long orderId = 1L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        orderService.payOrder(orderId);

        Mockito.verify(orderRepository, Mockito.never()).save(any(Order.class));
    }
}