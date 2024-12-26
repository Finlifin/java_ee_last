package sup.monad.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import sup.monad.backend.service.PaymentService;
import sup.monad.backend.pojo.Payment;

@RestController
@RequestMapping("/api/v1/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping
    public Payment createPayment(@RequestBody Payment payment) throws PaymentService.PaymentServiceException {
        return paymentService.createPayment(payment);
    }

    @GetMapping("/of/{userId}")
    public List<Payment> getRelativePayments(@PathVariable Long userId) throws PaymentService.PaymentServiceException {
        return paymentService.getRelativePayments(userId);
    }

    @GetMapping("/{id}")
    public Payment getPayment(@PathVariable Long id) throws PaymentService.PaymentServiceException {
        return paymentService.getPayment(id);
    }
}
