package sup.monad.backend.serviceTest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import sup.monad.backend.exception.CustomException;
import sup.monad.backend.pojo.Product;
import sup.monad.backend.repository.ProductRepository;
import sup.monad.backend.service.ProductService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void findAllProducts() {
        // 模拟分页数据
        List<Product> productList = new ArrayList<>();
        productList.add(new Product());
        Page<Product> page = new PageImpl<>(productList);
        when(productRepository.all(any(Pageable.class))).thenReturn(page);

        Page<Product> result = productService.findAllProducts(Pageable.unpaged());

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
    }

    @Test
    void findAllProductsOf() {
        // 模拟根据卖家查询到的产品列表
        List<Product> productList = new ArrayList<>();
        productList.add(new Product());
        when(productRepository.allProductsOf(anyLong())).thenReturn(productList);

        List<Product> result = productService.findAllProductsOf(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void sellOneProduct() {
        doNothing().when(productRepository).selledOne(anyLong());

        productService.sellOneProduct(1L);

        verify(productRepository, times(1)).selledOne(1L);
    }

    @Test
    void sellProducts() {
        doNothing().when(productRepository).selled(anyInt(), anyLong());

        productService.sellProducts(2, 1L);

        verify(productRepository, times(1)).selled(2, 1L);
    }

    @Test
    void addStock() {
        doNothing().when(productRepository).storage(anyInt(), anyLong());

        productService.addStock(3, 1L);

        verify(productRepository, times(1)).storage(3, 1L);
    }

    @Test
    void addProduct() throws CustomException {
        Product productToAdd = new Product();
        when(productRepository.save(any(Product.class))).thenReturn(productToAdd);

        Product result = productService.addProduct(productToAdd);

        assertNotNull(result);
        assertEquals(productToAdd, result);
    }

    @Test
    void findProductById() throws CustomException {
        Product product = new Product();
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));

        Product result = productService.findProductById(1L);

        assertNotNull(result);
        assertEquals(product, result);
    }

    @Test
    void findProductById_ProductNotFound() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(CustomException.class, () -> productService.findProductById(1L));
    }

    @Test
    void updateProduct() throws CustomException {
        Product productToUpdate = new Product();
        when(productRepository.save(any(Product.class))).thenReturn(productToUpdate);

        Product result = productService.updateProduct(productToUpdate);

        assertNotNull(result);
        assertEquals(productToUpdate, result);
    }

    @Test
    void deleteProduct() throws CustomException {
        Product product = new Product();
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        doNothing().when(productRepository).delete(any(Product.class));

        Product result = productService.deleteProduct(1L);

        assertNotNull(result);
        assertEquals(product, result);
    }

    @Test
    void deleteProduct_ProductNotFound() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(CustomException.class, () -> productService.deleteProduct(1L));
    }

    @Test
    void allTags() {
        List<List<String>> tagsList = new ArrayList<>();
        List<String> tags = new ArrayList<>();
        tags.add("tag1");
        tags.add("tag2");
        tagsList.add(tags);
        when(productRepository.allTags()).thenReturn(tagsList);

        List<String> result = productService.allTags();

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void allOfTag() {
        List<Product> productList = new ArrayList<>();
        Product product = new Product();
        List<String> tags = new ArrayList<>();
        tags.add("tag");
        product.setTags(tags);
        productList.add(product);
        when(productRepository.all()).thenReturn(productList);

        List<Product> result = productService.allOfTag("tag");

        assertNotNull(result);
        assertEquals(1, result.size());
    }
}