package sup.monad.backend.serviceTest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import sup.monad.backend.pojo.Cart;
import sup.monad.backend.pojo.Order;
import sup.monad.backend.pojo.Product;
import sup.monad.backend.service.CartService;
import sup.monad.backend.service.OrderService;
import sup.monad.backend.service.PaymentService;
import sup.monad.backend.service.ProductService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CartServiceTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ProductService productService;

    @Mock
    private PaymentService paymentService;

    @Mock
    private OrderService orderService;

    @InjectMocks
    private CartService cartService;

    @Test
    public void testGetCartByUserId() {
        Long userId = 1L;
        Cart cart = new Cart();
        when(redisTemplate.opsForValue()).thenReturn(mock(ValueOperations.class));
        when(redisTemplate.opsForValue().get("cart:" + userId)).thenReturn(cart);

        Cart result = cartService.getCartByUserId(userId);

        assertNotNull(result);
    }

    @Test
    public void testUpdateCart() {
        Long userId = 1L;
        Cart cart = new Cart();

        // 模拟opsForValue()方法返回一个ValueOperations的模拟对象
        ValueOperations<String, Object> valueOperationsMock = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOperationsMock);

        cartService.updateCart(userId, cart);

        // 验证模拟对象的set方法是否被调用
        verify(valueOperationsMock).set("cart:" + userId, cart);
    }

    @Test
    public void testClearCart() {
        Long userId = 1L;

        cartService.clearCart(userId);

        verify(redisTemplate).delete("cart:" + userId);
    }

    @Test
    public void testAddProduct() {
        Long userId = 1L;
        Long productId = 1L;
        Cart cart = new Cart();

        // 模拟opsForValue()方法返回一个ValueOperations的模拟对象
        ValueOperations<String, Object> valueOperationsMock = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOperationsMock);

        // 模拟第一次调用opsForValue().get()方法返回null，模拟购物车不存在的情况
        when(valueOperationsMock.get("cart:" + userId)).thenReturn(null);
        when(productService.findProductById(productId)).thenReturn(new Product());

        // 调用addProduct方法添加产品
        cartService.addProduct(userId, productId);

        // 验证更新后的购物车是否被保存到Redis中
        verify(valueOperationsMock).set("cart:" + userId, any(Cart.class));

        // 获取更新后的购物车信息
        Cart updatedCart = (Cart) valueOperationsMock.get("cart:" + userId);

        assertNotNull(updatedCart);
        assertEquals(1, updatedCart.getOrders().size());
        assertEquals(productId, updatedCart.getOrders().get(0).getProductId());
        assertEquals(1, updatedCart.getOrders().get(0).getQuantity());

        // 验证第二次调用opsForValue().set()方法是否被调用
        verify(valueOperationsMock, times(2)).set("cart:" + userId, updatedCart);
    }
    @Test
    public void testCheckout() {
        Long userId = 1L;
        Cart cart = new Cart();
        Order order1 = new Order(userId, 1L, 10.0, "pending", 1);
        Order order2 = new Order(userId, 2L, 20.0, "pending", 2);
        cart.setOrders(List.of(order1, order2));

        // 模拟opsForValue()方法返回一个ValueOperations的模拟对象
        ValueOperations<String, Object> valueOperationsMock = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOperationsMock);
        // 设置模拟对象的get方法的返回值
        when(valueOperationsMock.get("cart:" + userId)).thenReturn(cart);

        cartService.checkout(userId);

        verify(orderService, times(2)).createOrder(any(Order.class));
        verify(paymentService, times(2)).payOrder(any(Order.class), eq(userId));

        assertEquals(0, cart.getOrders().size());
        // 验证set方法是否被调用
        verify(valueOperationsMock).set("cart:" + userId, cart);
    }
}