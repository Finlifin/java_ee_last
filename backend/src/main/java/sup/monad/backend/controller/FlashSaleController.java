package sup.monad.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sup.monad.backend.pojo.FlashSaleActivity;
import sup.monad.backend.service.IFlashSaleService;
import sup.monad.backend.service.IUserService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/flashsale")
public class FlashSaleController {

    @Autowired
    private IFlashSaleService flashSaleService;

    @Autowired
    private IUserService userService;

    @GetMapping
    public List<FlashSaleActivity> getAllFlashSales() {
        return flashSaleService.getAllFlashSales();
    }

    @GetMapping("/{id}")
    public FlashSaleActivity getFlashSaleById(@PathVariable Long id) {
        return flashSaleService.getFlashSaleById(id);
    }

    @PostMapping
    public FlashSaleActivity createFlashSale(@RequestBody FlashSaleActivity flashSaleActivity) {
        return flashSaleService.createFlashSale(flashSaleActivity);
    }

    @PutMapping("/{id}")
    public FlashSaleActivity updateFlashSale(@PathVariable Long id, @RequestBody FlashSaleActivity flashSaleActivity) {
        return flashSaleService.updateFlashSale(id, flashSaleActivity);
    }

    @DeleteMapping("/{id}")
    public void deleteFlashSale(@PathVariable Long id) {
        flashSaleService.deleteFlashSale(id);
    }

    @PostMapping("/{flashSaleId}/checkout")
    public void checkout(
            @PathVariable Long flashSaleId,
            @RequestParam int quantity,
            @RequestHeader("Authorization") String bearerToken) {
        var session = userService.auth(bearerToken);
        flashSaleService.checkout(flashSaleId, session.userInfo.getId(), quantity);
    }
}
