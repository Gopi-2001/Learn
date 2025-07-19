package in.gopikant.billingSoftware.service;

import com.razorpay.RazorpayException;
import in.gopikant.billingSoftware.io.RazorpayOrderResponse;

public interface RazorpayService {
    RazorpayOrderResponse createOrder(Double amount, String currency) throws RazorpayException;
}
