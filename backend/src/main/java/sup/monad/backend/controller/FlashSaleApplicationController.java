package sup.monad.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sup.monad.backend.pojo.FlashSaleApplication;
import sup.monad.backend.service.FlashSaleApplicationService;
import sup.monad.backend.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/flashSaleApplication")
public class FlashSaleApplicationController {

    @Autowired
    private FlashSaleApplicationService flashSaleApplicationService;

    @Autowired
    private UserService userService;

    @GetMapping
    public List<FlashSaleApplication> getAllApplications() {
        return flashSaleApplicationService.getAllApplications();
    }

    @GetMapping("/{id}")
    public FlashSaleApplication getApplicationById(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        return flashSaleApplicationService.getApplicationById(id);
    }

    @GetMapping("/applicant/{applicantId}")
    public List<FlashSaleApplication> getApplicationsByApplicantId(@PathVariable Long applicantId) {
        return flashSaleApplicationService.getApplicationsByApplicantId(applicantId);
    }

    @PostMapping
    public FlashSaleApplication createApplication(@RequestBody FlashSaleApplication flashSaleApplication, @RequestHeader("Authorization") String token) {
        var session = userService.auth(token);
        return flashSaleApplicationService.createApplication(flashSaleApplication, session.userInfo.getId());
    }

    @PostMapping("/approve/{id}")
    public FlashSaleApplication approveApplication(@PathVariable Long id,
            @RequestHeader("Authorization") String token) {
        var session = userService.auth(token, "admin");
        return flashSaleApplicationService.approveApplication(id, session.userInfo.getId());
    }

    @PostMapping("/reject/{id}")
    public FlashSaleApplication rejectApplication(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        var session = userService.auth(token, "admin");
        return flashSaleApplicationService.rejectApplication(id, session.userInfo.getId());
    }
}
