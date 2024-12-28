package sup.monad.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sup.monad.backend.pojo.Cart;
import sup.monad.backend.service.ICartService;
import sup.monad.backend.service.UserService;

@RestController
@RequestMapping("/api/v1/cart")
public class CartController {

    @Autowired
    private ICartService cartService;

    @Autowired
    private UserService userService;

    @GetMapping
    public Cart getCart(@RequestHeader(value = "Authorization") String token) {
        var session = userService.auth(token);
        return cartService.getCartByUserId(session.getUserInfo().getId());
    }

    @PostMapping("/add")
    public void addProduct(@RequestParam Long productId, @RequestHeader(value = "Authorization") String token) {
        var session = userService.auth(token);
        cartService.addProduct(session.getUserInfo().getId(), productId);
    }

    @PutMapping("/increase")
    public void increaseProduct(@RequestParam Long productId, @RequestHeader(value = "Authorization") String token) {
        var session = userService.auth(token);
        cartService.addProduct(session.getUserInfo().getId(), productId);
    }

    @PutMapping("/decrease")
    public void decreaseProduct(@RequestParam Long productId, @RequestHeader(value = "Authorization") String token) {
        var session = userService.auth(token);
        cartService.decreaseCart(session.getUserInfo().getId(), productId);
    }

    @PutMapping("/setQuantity")
    public void setProductQuantity(@RequestParam Long productId, @RequestParam Integer quantity,
            @RequestHeader(value = "Authorization") String token) {
        var session = userService.auth(token);
        cartService.setProductQuantity(session.getUserInfo().getId(), productId, quantity);
    }

    @DeleteMapping("/remove")
    public void removeProduct(@RequestParam Long productId, @RequestHeader(value = "Authorization") String token) {
        var session = userService.auth(token);
        cartService.removeProduct(session.getUserInfo().getId(), productId);
    }

    @DeleteMapping("/clear")
    public void clearCart(@RequestHeader(value = "Authorization") String token) {
        var session = userService.auth(token);
        cartService.clearCart(session.getUserInfo().getId());
    }

    @PostMapping("/checkout")
    public void checkout(@RequestHeader(value = "Authorization") String token) {
        var session = userService.auth(token);
        cartService.checkout(session.getUserInfo().getId());
    }
}
