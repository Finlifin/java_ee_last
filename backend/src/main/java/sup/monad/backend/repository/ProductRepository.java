package sup.monad.backend.repository;

import sup.monad.backend.pojo.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // 分页查询所有产品，并支持排序
    @Transactional
    @Query("SELECT p FROM Product p")
    Page<Product> all(Pageable pageable);

    // just get all products
    @Transactional
    @Query("SELECT p FROM Product p")
    List<Product> all();

    // 根据卖家查询所有产品
    @Transactional
    @Query("SELECT p FROM Product p WHERE p.seller = :seller")
    List<Product> allProductsOf(String seller);

    // 销售一个产品，减少库存
    @Transactional
    @Modifying
    @Query("UPDATE Product p SET p.stock = p.stock - 1 WHERE p.id = :productId AND p.stock > 0")
    void selledOne(Long productId);

    // 销售指定数量的产品，减少库存
    @Transactional
    @Modifying
    @Query("UPDATE Product p SET p.stock = p.stock - :quantity WHERE p.id = :productId AND p.stock >= :quantity")
    void selled(int quantity, Long productId);

    // 增加指定数量的产品库存
    @Transactional
    @Modifying
    @Query("UPDATE Product p SET p.stock = p.stock + :quantity WHERE p.id = :productId")
    void storage(int quantity, Long productId);

    // 获取所有不同的标签
    @Query("SELECT p.tags FROM Product p")
    List<List<String>> allTags();

    // 根据标签查询所有产品
    // 字段 tags 是一个 postgresql 数组类型
    // @Query("SELECT p FROM Product p WHERE :tag = ANY(p.tags)")
    // List<Product> allOfTag(String tag);
}