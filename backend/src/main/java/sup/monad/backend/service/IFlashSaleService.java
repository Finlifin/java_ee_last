package sup.monad.backend.service;

import sup.monad.backend.pojo.FlashSaleActivity;
import sup.monad.backend.pojo.Order;

import java.util.List;

public interface IFlashSaleService {
    List<FlashSaleActivity> getAllFlashSales();
    FlashSaleActivity getFlashSaleById(Long id);
    FlashSaleActivity createFlashSale(FlashSaleActivity flashSaleActivity);
    FlashSaleActivity updateFlashSale(Long id, FlashSaleActivity flashSaleActivity);
    void deleteFlashSale(Long id);

    Order checkout(Long flashSaleId, Long userId, int quantity);

    void startFlashSale(Long flashSaleId);
    void endFlashSale(Long flashSaleId);
    void cancelFlashSale(Long flashSaleId);
}
