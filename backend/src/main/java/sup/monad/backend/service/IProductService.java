package sup.monad.backend.service;

import sup.monad.backend.pojo.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IProductService {
    Product addProduct(Product product);

    Product findProductById(Long productId);

    Product updateProduct(Product product);

    Product deleteProduct(Long productId);

    Page<Product> findAllProducts(Pageable pageable);

    List<Product> findAllProductsOf(String seller);

    void sellOneProduct(Long productId);

    void sellProducts(int quantity, Long productId);

    void addStock(int quantity, Long productId);

    List<String> allTags();

    List<Product> allOfTag(String tag);
}