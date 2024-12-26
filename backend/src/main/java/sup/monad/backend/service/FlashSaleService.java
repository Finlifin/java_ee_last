package sup.monad.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sup.monad.backend.pojo.FlashSaleActivity;
import sup.monad.backend.pojo.Order;
import sup.monad.backend.repository.FlashSaleRepository;
import java.util.List;

@Service
public class FlashSaleService implements IFlashSaleService {

    @Autowired
    private FlashSaleRepository flashSaleRepository;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductService productService;

    @Autowired
    private PaymentService paymentService;

    @Override
    public List<FlashSaleActivity> getAllFlashSales() {
        return flashSaleRepository.findAll();
    }

    @Override
    public FlashSaleActivity getFlashSaleById(Long id) {
        return flashSaleRepository.findById(id).orElse(null);
    }

    @Override
    public FlashSaleActivity createFlashSale(FlashSaleActivity flashSaleActivity) {
        return flashSaleRepository.save(flashSaleActivity);
    }

    @Override
    public FlashSaleActivity updateFlashSale(Long id, FlashSaleActivity flashSaleActivity) {
        if (flashSaleRepository.existsById(id)) {
            flashSaleActivity.setId(id);
            return flashSaleRepository.save(flashSaleActivity);
        }
        return null;
    }

    @Override
    public void deleteFlashSale(Long id) {
        flashSaleRepository.deleteById(id);
    }

    @Override
    public Order checkout(Long flashSaleId, Long userId, int quantity) {
        var order = new Order();
        var flashSale = flashSaleRepository.findById(flashSaleId).orElse(null);
        var product = productService.findProductById(flashSale.getProductId());
        order.setProductId(flashSale.getProductId());
        order.setQuantity(quantity);
        order.setState("pending");
        order.setTotalAmount(product.getPrice() * quantity * flashSale.getDiscount());
        
        orderService.createOrder(order);

        // JUST PAY IT
        paymentService.payOrder(order, userId);
        flashSale.setTotalQuantity(flashSale.getTotalQuantity() - quantity);
        return order;
    }

    @Override
    public void startFlashSale(Long flashSaleId) {
        var flashSale = flashSaleRepository.findById(flashSaleId).orElse(null);
        if (flashSale != null) {
            flashSale.setState("started");
            flashSaleRepository.save(flashSale);
        }
    }

    @Override
    public void endFlashSale(Long flashSaleId) {
        var flashSale = flashSaleRepository.findById(flashSaleId).orElse(null);
        if (flashSale != null) {
            flashSale.setState("ended");
            flashSaleRepository.save(flashSale);
        }
    }

    @Override
    public void cancelFlashSale(Long flashSaleId) {
        var flashSale = flashSaleRepository.findById(flashSaleId).orElse(null);
        if (flashSale != null) {
            flashSale.setState("canceled");
            flashSaleRepository.save(flashSale);
        }
    }
}
