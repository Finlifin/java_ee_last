package sup.monad.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import sup.monad.backend.pojo.Product;
import sup.monad.backend.service.ProductService;
import sup.monad.backend.exception.CustomException;

@RestController
@RequestMapping(value = "/api/v1/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public ResponseEntity<Object> getAllProducts(@RequestParam(defaultValue = "0") int pageOffset,
                                                 @RequestParam(defaultValue = "10") int pageSize) {
        return new ResponseEntity<>(productService.findAllProducts(PageRequest.of(pageOffset, pageSize)), HttpStatus.OK);
    }

    @GetMapping("/seller/{seller}")
    public ResponseEntity<Object> getAllProductsOf(@PathVariable Long seller) {
        return new ResponseEntity<>(productService.findAllProductsOf(seller), HttpStatus.OK);
    }

    @PostMapping("/sell/{productId}")
    public ResponseEntity<Object> sellProducts(@RequestParam(defaultValue = "1") int quantity, @PathVariable Long productId) {
        productService.sellProducts(quantity, productId);
        return new ResponseEntity<>("", HttpStatus.OK);
    }

    @PostMapping("/addStock/{productId}")
    public ResponseEntity<Object> addStock(@RequestParam(defaultValue = "1") int quantity, @PathVariable Long productId) {
        productService.addStock(quantity, productId);
        return new ResponseEntity<>("", HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<Object> addProduct(@RequestBody Product product) throws CustomException {
        return new ResponseEntity<>(productService.addProduct(product), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getProduct(@PathVariable Long id) throws CustomException {
        return new ResponseEntity<>(productService.findProductById(id), HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity<Object> updateProduct(@RequestBody Product product) throws CustomException {
        return new ResponseEntity<>(productService.updateProduct(product), HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Object> deleteProduct(@PathVariable Long id) throws CustomException {
        productService.deleteProduct(id);
        return new ResponseEntity<>("", HttpStatus.OK);
    }

    @GetMapping("/tags")
    public ResponseEntity<Object> allTags() {
        return new ResponseEntity<>(productService.allTags(), HttpStatus.OK);
    }

    @GetMapping("/tag")
    public ResponseEntity<Object> allOfTag(@RequestParam String tag) {
        return new ResponseEntity<>(productService.allOfTag(tag), HttpStatus.OK);
    }
}