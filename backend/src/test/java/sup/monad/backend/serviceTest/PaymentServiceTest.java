package sup.monad.backend.serviceTest;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import sup.monad.backend.exception.CustomException;
import sup.monad.backend.pojo.Order;
import sup.monad.backend.pojo.Payment;
import sup.monad.backend.pojo.Product;
import sup.monad.backend.repository.PaymentRepository;
import sup.monad.backend.service.OrderService;
import sup.monad.backend.service.PaymentService;
import sup.monad.backend.service.ProductService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@SpringBootTest
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private OrderService orderService;

    @Mock
    private ProductService productService;

    @InjectMocks
    private PaymentService paymentService;

    // 模拟静态的支付数据
    private static final List<Payment> PAYMENT_LIST = new ArrayList<>();

    static {
        Payment payment1 = new Payment();
        payment1.setId(1L);
        payment1.setPayer(1L);
        payment1.setPayee(2L);
        payment1.setOrderId(101L);
        payment1.setPaymentMethod("Credit Card");
        payment1.setPaymentStatus("paid");
        payment1.setPaymentAmount(100);
        PAYMENT_LIST.add(payment1);

        Payment payment2 = new Payment();
        payment2.setId(2L);
        payment2.setPayer(1L);
        payment2.setPayee(3L);
        payment2.setOrderId(102L);
        payment2.setPaymentMethod("PayPal");
        payment2.setPaymentStatus("pending");
        payment2.setPaymentAmount(200);
        PAYMENT_LIST.add(payment2);
    }

    // 模拟静态的产品数据
    private static final Product PRODUCT = new Product();

    static {
        PRODUCT.setId(1L);
        PRODUCT.setSeller(2L);
        PRODUCT.setPrice(100);
    }

    // 模拟静态的订单数据
    private static final Order ORDER = new Order();

    static {
        ORDER.setId(101L);
        ORDER.setProductId(1L);
        ORDER.setCustomerId(1L);
        ORDER.setTotalAmount(100.0);
        ORDER.setState("pending");
        ORDER.setQuantity(1);
    }

    // 测试正常创建支付
    @Test
    void createPayment() {
        Payment paymentToSave = new Payment();
        Payment savedPayment = new Payment();
        savedPayment.setId(3L);
        // 模拟保存支付成功并返回保存后的支付对象
        when(paymentRepository.save(any(Payment.class))).thenReturn(savedPayment);

        try {
            Payment result = paymentService.createPayment(paymentToSave);
            assertEquals(3L, result.getId());
        } catch (PaymentService.PaymentServiceException e) {
            fail("正常情况下不应抛出异常");
        }
    }

    // 测试创建支付时抛出异常
    @Test
    void createPayment_ShouldThrowException() {
        Payment paymentToSave = new Payment();
        // 模拟保存支付时抛出运行时异常
        when(paymentRepository.save(any(Payment.class))).thenThrow(new RuntimeException());

        assertThrows(PaymentService.PaymentServiceException.class, () -> {
            paymentService.createPayment(paymentToSave);
        });
    }

    // 测试获取存在的支付
    @Test
    void getPayment() {
        Long paymentId = 1L;
        Payment existingPayment = PAYMENT_LIST.stream().filter(p -> p.getId().equals(paymentId)).findFirst().orElse(null);
        // 模拟根据ID查找支付成功并返回支付对象
        when(paymentRepository.findById(paymentId)).thenReturn(Optional.ofNullable(existingPayment));

        try {
            Payment result = paymentService.getPayment(paymentId);
            assertEquals(paymentId, result.getId());
        } catch (PaymentService.PaymentServiceException e) {
            fail("正常情况下不应抛出异常");
        }
    }

    // 测试获取不存在的支付
    @Test
    void getPayment_ShouldThrowException() {
        Long paymentId = 3L;
        // 模拟根据ID查找支付失败，返回空Optional
        when(paymentRepository.findById(paymentId)).thenReturn(Optional.empty());

        assertThrows(PaymentService.PaymentServiceException.class, () -> {
            paymentService.getPayment(paymentId);
        });
    }

    // 测试获取相关支付（正常情况）
    @Test
    void getRelativePayments() {
        Long userId = 1L;
        List<Payment> payerPayments = PAYMENT_LIST.stream().filter(p -> p.getPayer().equals(userId)).toList();
        // 模拟根据付款人ID查找支付成功并返回支付列表
        when(paymentRepository.findByPayerId(userId)).thenReturn(payerPayments);

        List<Payment> payeePayments = PAYMENT_LIST.stream().filter(p -> p.getPayee().equals(userId)).toList();
        // 模拟根据收款人ID查找支付成功并返回支付列表
        when(paymentRepository.findByPayeeId(userId)).thenReturn(payeePayments);

        try {
            List<Payment> result = paymentService.getRelativePayments(userId);
            assertEquals(2, result.size());
        } catch (PaymentService.PaymentServiceException e) {
            fail("正常情况下不应抛出异常");
        }
    }

    // 测试支付订单（正常情况）
    @Test
    void payOrder() {
        Long productId = 1L;
        Long userId = 1L;
        Order order = ORDER;
        Payment payment = new Payment();
        Product product = PRODUCT;
        // 模拟根据产品ID查找产品成功并返回产品对象
        when(productService.findProductById(productId)).thenReturn(product);
        doNothing().when(orderService).payOrder(order.getId());
        // 模拟保存支付成功并返回保存后的支付对象
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        try {
            Payment result = paymentService.payOrder(order, userId);
            assertNotNull(result);
            assertEquals(order.getId(), result.getOrderId());
            assertEquals(userId, result.getPayer());
            assertEquals(product.getSeller(), result.getPayee());
            assertEquals(product.getPrice(), result.getPaymentAmount());
        } catch (PaymentService.PaymentServiceException e) {
            fail("正常情况下不应抛出异常");
        }
    }

    // 测试支付订单时产品不存在
    @Test
    void payOrder_ProductNotFound() {
        Long productId = 1L;
        Long userId = 1L;
        Order order = ORDER;
        // 模拟根据产品ID查找产品失败，抛出产品不存在异常
        when(productService.findProductById(productId)).thenThrow(new CustomException("Product not found", null));

        assertThrows(PaymentService.PaymentServiceException.class, () -> {
            paymentService.payOrder(order, userId);
        });
    }

    // 测试支付订单时支付创建失败
    @Test
    void payOrder_PaymentCreationFailed() {
        Long productId = 1L;
        Long userId = 1L;
        Order order = ORDER;
        Product product = PRODUCT;
        // 模拟根据产品ID查找产品成功并返回产品对象
        when(productService.findProductById(productId)).thenReturn(product);
        doNothing().when(orderService).payOrder(order.getId());
        // 模拟保存支付时抛出运行时异常
        when(paymentRepository.save(any(Payment.class))).thenThrow(new RuntimeException());

        assertThrows(PaymentService.PaymentServiceException.class, () -> {
            paymentService.payOrder(order, userId);
        });
    }
}

//package sup.monad.backend.serviceTest;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.springframework.boot.test.context.SpringBootTest;
//import sup.monad.backend.exception.CustomException;
//import sup.monad.backend.pojo.Order;
//import sup.monad.backend.pojo.Payment;
//import sup.monad.backend.pojo.Product;
//import sup.monad.backend.repository.PaymentRepository;
//import sup.monad.backend.service.OrderService;
//import sup.monad.backend.service.PaymentService;
//import sup.monad.backend.service.ProductService;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.doNothing;
//import static org.mockito.Mockito.when;
//
//@SpringBootTest
//class PaymentServiceTest {
//
//    @Mock
//    private PaymentRepository paymentRepository;
//
//    @Mock
//    private OrderService orderService;
//
//    @Mock
//    private ProductService productService;
//
//    @InjectMocks
//    private PaymentService paymentService;
//
//    // 测试正常创建支付
//    @Test
//    void createPayment() {
//        Payment paymentToSave = new Payment();
//        Payment savedPayment = new Payment();
//        savedPayment.setId(1L);
//        when(paymentRepository.save(any(Payment.class))).thenReturn(savedPayment);
//
//        try {
//            Payment result = paymentService.createPayment(paymentToSave);
//            assertEquals(1L, result.getId());
//        } catch (PaymentService.PaymentServiceException e) {
//            fail("正常情况下不应抛出异常");
//        }
//    }
//
//    // 测试创建支付时抛出异常
//    @Test
//    void createPayment_ShouldThrowException() {
//        Payment paymentToSave = new Payment();
//        when(paymentRepository.save(any(Payment.class))).thenThrow(new RuntimeException());
//
//        assertThrows(PaymentService.PaymentServiceException.class, () -> {
//            paymentService.createPayment(paymentToSave);
//        });
//    }
//
//    // 测试获取存在的支付
//    @Test
//    void getPayment() {
//        Long paymentId = 1L;
//        Payment existingPayment = new Payment();
//        existingPayment.setId(paymentId);
//        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(existingPayment));
//
//        try {
//            Payment result = paymentService.getPayment(paymentId);
//            assertEquals(paymentId, result.getId());
//        } catch (PaymentService.PaymentServiceException e) {
//            fail("正常情况下不应抛出异常");
//        }
//    }
//
//    // 测试获取不存在的支付
//    @Test
//    void getPayment_ShouldThrowException() {
//        Long paymentId = 1L;
//        when(paymentRepository.findById(paymentId)).thenReturn(Optional.empty());
//
//        assertThrows(PaymentService.PaymentServiceException.class, () -> {
//            paymentService.getPayment(paymentId);
//        });
//    }
//
//    // 测试获取相关支付（正常情况）
//    @Test
//    void getRelativePayments() {
//        Long userId = 1L;
//        List<Payment> payerPayments = new ArrayList<>();
//        payerPayments.add(new Payment());
//        payerPayments.add(new Payment());
//        when(paymentRepository.findByPayerId(userId)).thenReturn(payerPayments);
//
//        List<Payment> payeePayments = new ArrayList<>();
//        payeePayments.add(new Payment());
//        when(paymentRepository.findByPayeeId(userId)).thenReturn(payeePayments);
//
//        try {
//            List<Payment> result = paymentService.getRelativePayments(userId);
//            assertEquals(3, result.size());
//        } catch (PaymentService.PaymentServiceException e) {
//            fail("正常情况下不应抛出异常");
//        }
//    }
//
//    // 测试支付订单（正常情况）
//    @Test
//    void payOrder() {
//        Long productId = 1L;
//        Long userId = 1L;
//        Order order = new Order();
//        order.setProductId(productId);
//        Payment payment = new Payment();
//        Product product = new Product();
//        product.setSeller(2L);
//        product.setPrice(100);
//        when(productService.findProductById(productId)).thenReturn(product);
//        doNothing().when(orderService).payOrder(order.getId());
//        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);
//
//        try {
//            Payment result = paymentService.payOrder(order, userId);
//            assertNotNull(result);
//            assertEquals(order.getId(), result.getOrderId());
//            assertEquals(userId, result.getPayer());
//            assertEquals(product.getSeller(), result.getPayee());
//            assertEquals(product.getPrice(), result.getPaymentAmount());
//        } catch (PaymentService.PaymentServiceException e) {
//            fail("正常情况下不应抛出异常");
//        }
//    }
//
//    // 测试支付订单时产品不存在
//    @Test
//    void payOrder_ProductNotFound() {
//        Long productId = 1L;
//        Long userId = 1L;
//        Order order = new Order();
//        order.setProductId(productId);
//        when(productService.findProductById(productId)).thenThrow(new CustomException("Product not found", null));
//
//        assertThrows(PaymentService.PaymentServiceException.class, () -> {
//            paymentService.payOrder(order, userId);
//        });
//    }
//
//    // 测试支付订单时支付创建失败
//    @Test
//    void payOrder_PaymentCreationFailed() {
//        Long productId = 1L;
//        Long userId = 1L;
//        Order order = new Order();
//        order.setProductId(productId);
//        Product product = new Product();
//        product.setSeller(2L);
//        product.setPrice(100);
//        when(productService.findProductById(productId)).thenReturn(product);
//        doNothing().when(orderService).payOrder(order.getId());
//        when(paymentRepository.save(any(Payment.class))).thenThrow(new RuntimeException());
//
//        assertThrows(PaymentService.PaymentServiceException.class, () -> {
//            paymentService.payOrder(order, userId);
//        });
//    }
//}