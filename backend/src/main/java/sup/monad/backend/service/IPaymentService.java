package sup.monad.backend.service;

import java.util.List;

import sup.monad.backend.pojo.Order;
import sup.monad.backend.pojo.Payment;

public interface IPaymentService {
    Payment createPayment(Payment payment) throws PaymentService.PaymentServiceException;
    Payment getPayment(Long id) throws PaymentService.PaymentServiceException;
    
    // all relative payments for a user
    List<Payment> getRelativePayments(Long userId) throws PaymentService.PaymentServiceException;
    Payment payOrder(Order orderId, Long userId) throws PaymentService.PaymentServiceException;
}
