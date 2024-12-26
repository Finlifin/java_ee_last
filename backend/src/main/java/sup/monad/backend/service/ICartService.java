package sup.monad.backend.service;

import sup.monad.backend.pojo.Cart;

public interface ICartService {
    Cart getCartByUserId(Long userId);
    void updateCart(Long userId, Cart cart);
    void clearCart(Long userId);
    void decreaseCart(Long userId, Long productId);
    void increaseCart(Long userId, Long productId);
    void setProductQuantity(Long userId, Long productId, Integer quantity);
    void removeProduct(Long userId, Long productId);
    void addProduct(Long userId, Long productId);
    void checkout(Long userId);
}
