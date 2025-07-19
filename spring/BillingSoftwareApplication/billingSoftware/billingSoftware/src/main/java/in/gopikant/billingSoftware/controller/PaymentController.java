package in.gopikant.billingSoftware.controller;

import com.razorpay.RazorpayException;
import in.gopikant.billingSoftware.io.OrderResponse;
import in.gopikant.billingSoftware.io.PaymentRequest;
import in.gopikant.billingSoftware.io.PaymentVerificationRequest;
import in.gopikant.billingSoftware.io.RazorpayOrderResponse;
import in.gopikant.billingSoftware.service.OrderService;
import in.gopikant.billingSoftware.service.RazorpayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final RazorpayService razorpayService;
    private final OrderService orderService;

    @PostMapping("/create-order")
    @ResponseStatus(HttpStatus.CREATED)
    public RazorpayOrderResponse createRazorpayOrder(@RequestBody PaymentRequest paymentRequest) throws RazorpayException {
        return razorpayService.createOrder(paymentRequest.getAmount(), paymentRequest.getCurrency());
    }

    @PostMapping("/verify")
    public OrderResponse verifyPayment(@RequestBody PaymentVerificationRequest request){
        return orderService.verifyPayment(request);
    }
}
