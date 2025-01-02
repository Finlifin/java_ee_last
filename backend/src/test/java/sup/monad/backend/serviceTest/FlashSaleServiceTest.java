package sup.monad.backend.serviceTest;

import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import sup.monad.backend.exception.CustomException;
import sup.monad.backend.pojo.FlashSaleActivity;
import sup.monad.backend.pojo.Order;
import sup.monad.backend.pojo.Product;
import sup.monad.backend.repository.FlashSaleRepository;
import sup.monad.backend.service.FlashSaleService;
import sup.monad.backend.service.OrderService;
import sup.monad.backend.service.PaymentService;
import sup.monad.backend.service.ProductService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class FlashSaleServiceTest {

    @Mock
    private FlashSaleRepository flashSaleRepository;

    @Mock
    private OrderService orderService;

    @Mock
    private ProductService productService;

    @Mock
    private PaymentService paymentService;

    @Mock
    private Validator validator;

    @InjectMocks
    private FlashSaleService flashSaleService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        // 设置validator的模拟行为
        when(validator.validate(any(), any())).thenReturn(null);
    }

    @Test
    void getAllFlashSales() {
        // 模拟返回的闪购活动列表
        List<FlashSaleActivity> mockList = new ArrayList<>();
        when(flashSaleRepository.findAll()).thenReturn(mockList);

        List<FlashSaleActivity> result = flashSaleService.getAllFlashSales();

        assertEquals(mockList, result);
    }

    @Test
    void getFlashSaleById() {
        Long id = 1L;
        FlashSaleActivity mockActivity = new FlashSaleActivity();
        mockActivity.setId(id);
        when(flashSaleRepository.findById(id)).thenReturn(Optional.of(mockActivity));

        FlashSaleActivity result = flashSaleService.getFlashSaleById(id);

        assertEquals(mockActivity, result);
    }

    @Test
    void createFlashSale() {
        FlashSaleActivity flashSaleActivity = new FlashSaleActivity();
        flashSaleActivity.setProductId(1L);
        flashSaleActivity.setTotalQuantity(10);
        flashSaleActivity.setStartTime(LocalDateTime.now());
        flashSaleActivity.setEndTime(LocalDateTime.now().plusHours(1));

        Product mockProduct = new Product();
        mockProduct.setId(1L);
        mockProduct.setStock(20);
        List<String> tags = new ArrayList<>();
        tags.add("tag1");
        mockProduct.setTags(tags);

        when(productService.findProductById(flashSaleActivity.getProductId())).thenReturn(mockProduct);
        when(flashSaleRepository.save(any(FlashSaleActivity.class))).thenReturn(flashSaleActivity);
        when(validator.validate(any(), any())).thenReturn(null);

        FlashSaleActivity result = flashSaleService.createFlashSale(flashSaleActivity);

        assertEquals(flashSaleActivity, result);
    }

    @Test
    void updateFlashSale() {
        Long id = 1L;
        FlashSaleActivity flashSaleActivity = new FlashSaleActivity();
        flashSaleActivity.setProductId(1L);
        flashSaleActivity.setTotalQuantity(10);
        flashSaleActivity.setStartTime(LocalDateTime.now());
        flashSaleActivity.setEndTime(LocalDateTime.now().plusHours(1));

        Product mockProduct = new Product();
        mockProduct.setId(1L);
        mockProduct.setStock(20);
        List<String> tags = new ArrayList<>();
        tags.add("tag1");
        mockProduct.setTags(tags);

        when(productService.findProductById(flashSaleActivity.getProductId())).thenReturn(mockProduct);
        when(flashSaleRepository.findById(id)).thenReturn(Optional.of(flashSaleActivity));
        when(flashSaleRepository.save(any(FlashSaleActivity.class))).thenReturn(flashSaleActivity);
        when(validator.validate(any(), any())).thenReturn(null);

        FlashSaleActivity result = flashSaleService.updateFlashSale(id, flashSaleActivity);

        assertNull(result);
    }

    @Test
    void deleteFlashSale() {
        Long id = 1L;
        when(flashSaleRepository.existsById(id)).thenReturn(true);

        flashSaleService.deleteFlashSale(id);

        verify(flashSaleRepository, times(1)).deleteById(id);
    }

    @Test
    void checkout() {
        Long flashSaleId = 1L;
        Long userId = 1L;
        int quantity = 5;

        FlashSaleActivity flashSale = new FlashSaleActivity();
        flashSale.setProductId(1L);
        flashSale.setDiscount(0.8);
        flashSale.setTotalQuantity(10);

        Product product = new Product();
        product.setPrice(1000.0);
        List<String> tags = new ArrayList<>();
        tags.add("tag1");
        product.setTags(tags);

        when(flashSaleRepository.findById(flashSaleId)).thenReturn(Optional.of(flashSale));
        when(productService.findProductById(flashSale.getProductId())).thenReturn(product);
        when(orderService.createOrder(any(Order.class))).thenReturn(new Order());
        when(paymentService.payOrder(any(Order.class), eq(userId))).thenReturn(null);

        Order result = flashSaleService.checkout(flashSaleId, userId, quantity);

        assertNotNull(result);
        assertEquals(quantity, result.getQuantity());
        assertEquals(flashSale.getProductId(), result.getProductId());
        assertEquals("pending", result.getState());
        assertEquals(product.getPrice() * quantity * flashSale.getDiscount(), result.getTotalAmount());
    }

    @Test
    void startFlashSale() {
        Long flashSaleId = 1L;
        FlashSaleActivity flashSale = new FlashSaleActivity();
        when(flashSaleRepository.findById(flashSaleId)).thenReturn(Optional.of(flashSale));

        flashSaleService.startFlashSale(flashSaleId);

        assertEquals("started", flashSale.getState());
        verify(flashSaleRepository, times(1)).save(flashSale);
    }

    @Test
    void endFlashSale() {
        Long flashSaleId = 1L;
        FlashSaleActivity flashSale = new FlashSaleActivity();
        when(flashSaleRepository.findById(flashSaleId)).thenReturn(Optional.of(flashSale));

        flashSaleService.endFlashSale(flashSaleId);

        assertEquals("ended", flashSale.getState());
        verify(flashSaleRepository, times(1)).save(flashSale);
    }

    @Test
    void cancelFlashSale() {
        Long flashSaleId = 1L;
        FlashSaleActivity flashSale = new FlashSaleActivity();
        when(flashSaleRepository.findById(flashSaleId)).thenReturn(Optional.of(flashSale));

        flashSaleService.cancelFlashSale(flashSaleId);

        assertEquals("canceled", flashSale.getState());
        verify(flashSaleRepository, times(1)).save(flashSale);
    }
}