package sup.monad.backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import sup.monad.backend.pojo.Cart;
import sup.monad.backend.pojo.Order;

@Service
public class CartService implements ICartService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private OrderService orderService;

    private static final String CART_KEY_PREFIX = "cart:";

    @Override
    public Cart getCartByUserId(Long userId) {
        // if null create one
        var cart = redisTemplate.opsForValue().get(CART_KEY_PREFIX + userId);
        if (cart == null) {
            cart = new Cart();
            System.out.println(cart.toString());
            updateCart(userId, (Cart) cart);
        }
        return (Cart) cart;
    }

    @Override
    public void updateCart(Long userId, Cart cart) {
        redisTemplate.opsForValue().set(CART_KEY_PREFIX + userId, cart);
    }

    @Override
    public void clearCart(Long userId) {
        redisTemplate.delete(CART_KEY_PREFIX + userId);
    }

    @Override
    public void decreaseCart(Long userId, Long productId) {
        Cart cart = getCartByUserId(userId);
        if (cart != null) {
            cart.getOrders().stream().filter(order -> order.getProductId().equals(productId)).forEach(order -> {
                if (order.getQuantity() > 1) {
                    order.setQuantity(order.getQuantity() - 1);
                } else {
                    cart.getOrders().remove(order);
                }
            });
            updateCart(userId, cart);
        }
    }

    @Override
    public void increaseCart(Long userId, Long productId) {
        Cart cart = getCartByUserId(userId);
        if (cart != null) {
            cart.getOrders().stream().filter(order -> order.getProductId().equals(productId)).forEach(order -> {
                order.setQuantity(order.getQuantity() + 1);
            });
            updateCart(userId, cart);
        }
    }

    @Override
    public void removeProduct(Long userId, Long productId) {
        Cart cart = getCartByUserId(userId);
        if (cart != null) {
            cart.getOrders().removeIf(order -> order.getProductId().equals(productId));
            updateCart(userId, cart);
        }
    }

    @Override
    public void addProduct(Long userId, Long productId) {
        Cart cart = getCartByUserId(userId);
        if (cart == null) {
            cart = new Cart();
        }
        Cart finalCart = cart;
        finalCart.getOrders().stream().filter(order -> order.getProductId().equals(productId)).findAny()
                .ifPresentOrElse(order -> {
                    order.setQuantity(order.getQuantity() + 1);
                }, () -> {
                    var neo = new Order(userId, productId, 0.0, "pending", 1);
                    finalCart.getOrders().add(0, neo);
                });
        updateCart(userId, cart);
    }

    @Override
    public void checkout(Long userId) {
        Cart cart = getCartByUserId(userId);
        // requrest to payment service
        // PAYMENT TODO
        cart.getOrders().forEach(order -> {
            orderService.createOrder(order);
            paymentService.payOrder(order, userId);
        });
        cart.setOrders(List.of());
        updateCart(userId, cart);
    }

    @Override
    public void setProductQuantity(Long userId, Long productId, Integer quantity) {
        Cart cart = getCartByUserId(userId);
        if (cart != null) {
            cart.getOrders().stream().filter(order -> order.getProductId().equals(productId)).forEach(order -> {
                order.setQuantity(quantity);
            });
            updateCart(userId, cart);
        }
    }
}
