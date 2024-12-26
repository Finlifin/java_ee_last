package sup.monad.backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import sup.monad.backend.repository.PaymentRepository;
import sup.monad.backend.exception.CustomException;
import sup.monad.backend.pojo.Order;
import sup.monad.backend.pojo.Payment;

@Service
public class PaymentService implements IPaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductService productService;

    @Override
    public Payment createPayment(Payment payment) throws PaymentServiceException {
        try {
            return paymentRepository.save(payment);
        } catch (Exception e) {
            throw new PaymentServiceException("Failed to create payment", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Payment getPayment(Long id) throws PaymentServiceException {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentServiceException("Payment not found", HttpStatus.NOT_FOUND));
    }

    public static class PaymentServiceException extends CustomException {
        public PaymentServiceException(String message, HttpStatus status) {
            super(message, status);
        }
    }

    @Override
    public List<Payment> getRelativePayments(Long userId) throws PaymentServiceException {
        var result = paymentRepository.findByPayerId(userId);
        result.addAll(paymentRepository.findByPayeeId(userId));
        return result;
    }

    @Override
    public Payment payOrder(Order order, Long userId) throws PaymentServiceException {
        var product = productService.findProductById(order.getProductId());
        var payment = new Payment();
        payment.setOrderId(order.getId());
        payment.setPayer(userId);

        payment.setPayee(product.getSeller());
        payment.setPaymentAmount(product.getPrice());

        orderService.payOrder(order.getId());
        return createPayment(payment);
    }
}
