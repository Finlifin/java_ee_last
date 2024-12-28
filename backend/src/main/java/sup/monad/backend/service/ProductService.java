package sup.monad.backend.service;

import sup.monad.backend.repository.ProductRepository;
import sup.monad.backend.exception.CustomException;
import sup.monad.backend.pojo.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductService implements IProductService {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public Page<Product> findAllProducts(Pageable pageable) {
        return productRepository.all(pageable);
    }

    @Override
    public List<Product> findAllProductsOf(Long seller) {
        return productRepository.allProductsOf(seller);
    }

    @Override
    @Transactional
    public void sellOneProduct(Long productId) {
        productRepository.selledOne(productId);
    }

    @Override
    @Transactional
    public void sellProducts(int quantity, Long productId) {
        productRepository.selled(quantity, productId);
    }

    @Override
    @Transactional
    public void addStock(int quantity, Long productId) {
        productRepository.storage(quantity, productId);
    }

    @Override
    public Product addProduct(Product product) throws ProductServiceException {
        try {
            return productRepository.save(product);
        } catch (Exception e) {
            throw new ProductServiceException("Failed to add product", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Product findProductById(Long productId) throws ProductServiceException {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ProductServiceException("Product not found", HttpStatus.NOT_FOUND));
    }

    @Override
    public Product updateProduct(Product product) throws ProductServiceException {
        try {
            return productRepository.save(product);
        } catch (Exception e) {
            throw new ProductServiceException("Failed to update product", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Product deleteProduct(Long productId) throws ProductServiceException {
        Product product = findProductById(productId);
        try {
            if (product != null) {
                productRepository.delete(product);
            }
            return product;
        } catch (Exception e) {
            throw new ProductServiceException("Failed to delete product", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public List<String> allTags() {
        List<List<String>> tags = productRepository.allTags();
        return tags.stream().flatMap(List::stream).distinct().toList();
    }

    @Override
    public List<Product> allOfTag(String tag) {
        return productRepository.all().stream().filter(p -> p.getTags().contains(tag)).toList();
    }

    public class ProductServiceException extends CustomException {
        public ProductServiceException(String message, HttpStatus httpStatus) {
            super(message, httpStatus);
        }
    }
}