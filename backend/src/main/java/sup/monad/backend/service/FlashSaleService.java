package sup.monad.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import sup.monad.backend.exception.CustomException;
import sup.monad.backend.pojo.FlashSaleActivity;
import sup.monad.backend.pojo.Order;
import sup.monad.backend.pojo.Product;
import sup.monad.backend.repository.FlashSaleRepository;
import java.util.List;
import java.util.Set;


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

    @Autowired
    private Validator validator;

    @Override
    public List<FlashSaleActivity> getAllFlashSales() {
        return flashSaleRepository.findAll();
    }

    @Override
    public FlashSaleActivity getFlashSaleById(Long id) {
        return flashSaleRepository.findById(id).orElse(null);
    }

    @Override
    public FlashSaleActivity createFlashSale(@Valid FlashSaleActivity flashSaleActivity) {
        Set<ConstraintViolation<FlashSaleActivity>> violations = validator.validate(flashSaleActivity);
        if (!violations.isEmpty()) {
            StringBuilder errorMessages = new StringBuilder();
            for (ConstraintViolation<FlashSaleActivity> violation : violations) {
                errorMessages.append(violation.getMessage()).append(", ");
            }
            throw new CustomException(errorMessages.toString(), HttpStatus.BAD_REQUEST);
        }
        Product product = productService.findProductById(flashSaleActivity.getProductId());
        if (product == null) {
            throw new CustomException("Product not found", HttpStatus.NOT_FOUND);
        }
        if (flashSaleActivity.getTotalQuantity() > product.getStock()) {
             throw new CustomException("Not enough stock", HttpStatus.BAD_REQUEST);
        }
        if (flashSaleActivity.getStartTime().isAfter(flashSaleActivity.getEndTime())) {
            throw new CustomException("Start time must be before end time", HttpStatus.BAD_REQUEST);
        }

        return flashSaleRepository.save(flashSaleActivity);
        
    }

    @Override
    public FlashSaleActivity updateFlashSale(Long id,@Valid FlashSaleActivity flashSaleActivity) {
        Set<ConstraintViolation<FlashSaleActivity>> violations = validator.validate(flashSaleActivity);
        if (!violations.isEmpty()) {
            StringBuilder errorMessages = new StringBuilder();
            for (ConstraintViolation<FlashSaleActivity> violation : violations) {
                errorMessages.append(violation.getMessage()).append(", ");
            }
            throw new CustomException(errorMessages.toString(), HttpStatus.BAD_REQUEST);
        }
        Product product = productService.findProductById(flashSaleActivity.getProductId());
        if (product == null) {
            throw new CustomException("Product not found", HttpStatus.NOT_FOUND);
        }
        if (flashSaleActivity.getTotalQuantity() > product.getStock()) {
             throw new CustomException("Not enough stock", HttpStatus.BAD_REQUEST);
        }
        if (flashSaleActivity.getStartTime().isAfter(flashSaleActivity.getEndTime())) {
            throw new CustomException("Start time must be before end time", HttpStatus.BAD_REQUEST);
        }
        return null;
    }

    @Override
    public void deleteFlashSale(Long id) {
        if (flashSaleRepository.existsById(id)) {
            flashSaleRepository.deleteById(id);
        }
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
