package sup.monad.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import sup.monad.backend.exception.CustomException;
import sup.monad.backend.pojo.FlashSaleApplication;
import sup.monad.backend.pojo.Product;
import sup.monad.backend.repository.FlashSaleApplicationRepository;
import java.util.List;
import java.util.Set;

@Service
public class FlashSaleApplicationService {

    @Autowired
    private FlashSaleApplicationRepository flashSaleApplicationRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private Validator validator;

    public List<FlashSaleApplication> getAllApplications() {
        return flashSaleApplicationRepository.findAll();
    }

    public FlashSaleApplication getApplicationById(Long id) {
        return flashSaleApplicationRepository.findById(id).orElse(null);
    }

    public FlashSaleApplication createApplication(@Valid FlashSaleApplication flashSaleApplication, Long applicantId) {
        Set<ConstraintViolation<FlashSaleApplication>> violations = validator.validate(flashSaleApplication);
        if (!violations.isEmpty()) {
            StringBuilder errorMessages = new StringBuilder();
            for (ConstraintViolation<FlashSaleApplication> violation : violations) {
                errorMessages.append(violation.getMessage()).append(", ");
            }
            throw new CustomException(errorMessages.toString(), HttpStatus.BAD_REQUEST);
        }
        Product product = productService.findProductById(flashSaleApplication.getProductId());
        if (product == null) {
            throw new CustomException("Product not found", HttpStatus.NOT_FOUND);
        }
        if (flashSaleApplication.getTotalQuantity() > product.getStock()) {
            throw new CustomException("Not enough stock", HttpStatus.BAD_REQUEST);
        }
        if (flashSaleApplication.getStartTime().isAfter(flashSaleApplication.getEndTime())) {
            throw new CustomException("Start time must be before end time", HttpStatus.BAD_REQUEST);
        }
        flashSaleApplication.setStatus("pending");
        flashSaleApplication.setApplicantId(applicantId);
        return flashSaleApplicationRepository.save(flashSaleApplication);
    }

    public List<FlashSaleApplication> getApplicationsByApplicantId(Long applicantId) {
        return flashSaleApplicationRepository.findByApplicantId(applicantId);
    }

    public FlashSaleApplication approveApplication(Long id, Long operatorId) {
        FlashSaleApplication application = flashSaleApplicationRepository.findById(id).orElse(null);
        application.setStatus("approved");
        application.setOperatorId(operatorId);
        return flashSaleApplicationRepository.save(application);
    }

    public FlashSaleApplication rejectApplication(Long id, Long operatorId) {
        FlashSaleApplication application = flashSaleApplicationRepository.findById(id).orElse(null);
        application.setStatus("rejected");
        application.setOperatorId(operatorId);
        return flashSaleApplicationRepository.save(application);
    }
}
