package com.lanhcare.controller;

import com.lanhcare.exception.exps.ResourceNotFoundException;
import com.lanhcare.service.SubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Payment Controller (Public)
 * Handles VNPay callback/return endpoints
 * These endpoints must be public (no JWT) because VNPay calls them directly
 */
@RestController
@RequestMapping("/api/public/payments")
@RequiredArgsConstructor
@Tag(name = "Payment Callbacks", description = "VNPay payment callback endpoints (public)")
@Slf4j
public class PaymentController {

    private final SubscriptionService subscriptionService;

    /**
     * VNPay IPN URL - Server-to-server callback
     * VNPay calls this endpoint to notify payment result
     * Must return JSON response with RspCode and Message
     */
    @GetMapping("/vnpay-ipn")
    @Operation(summary = "VNPay IPN callback", 
               description = "IPN URL for VNPay to notify payment result (server-to-server)")
    public Map<String, String> vnpayIPN(HttpServletRequest request) {
        Map<String, String> result = new HashMap<>();

        try {
            // Extract all parameters from VNPay
            Map<String, String> params = extractParams(request);
            log.info("VNPay IPN received: {}", params);

            // Process the callback
            subscriptionService.processVNPayCallback(params);

            result.put("RspCode", "00");
            result.put("Message", "Confirm Success");

        } catch (ResourceNotFoundException e) {
            log.error("VNPay IPN - Order not found: {}", e.getMessage());
            result.put("RspCode", "01");
            result.put("Message", "Order not Found");
        } catch (SecurityException e) {
            log.error("VNPay IPN - Invalid checksum: {}", e.getMessage());
            result.put("RspCode", "97");
            result.put("Message", "Invalid Checksum");
        } catch (Exception e) {
            log.error("VNPay IPN - Unknown error: {}", e.getMessage());
            result.put("RspCode", "99");
            result.put("Message", "Unknown error");
        }

        return result;
    }

    /**
     * VNPay Return URL - Browser redirect after payment
     * User's browser is redirected here after completing payment on VNPay
     * This endpoint shows the payment result to the user
     */
    @GetMapping("/vnpay-return")
    @Operation(summary = "VNPay return URL", 
               description = "Return URL for VNPay to redirect user's browser after payment")
    public void vnpayReturn(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, String> params = extractParams(request);
        log.info("VNPay Return received: {}", params);

        try {
            // Process the callback (in case IPN hasn't been called yet)
            subscriptionService.processVNPayCallback(params);
        } catch (Exception e) {
            log.warn("VNPay Return processing (may already be processed via IPN): {}", e.getMessage());
        }

        String responseCode = params.get("vnp_ResponseCode");
        String txnRef = params.get("vnp_TxnRef");

        // Redirect to mobile app via deep link or show HTML result
        if ("00".equals(responseCode)) {
            // Payment successful - redirect to success page
            // For mobile app, you can use custom scheme: lanhcare://payment-success?txnRef=xxx
            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().write(
                    "<!DOCTYPE html><html><head><meta charset='UTF-8'>" +
                    "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                    "<title>Thanh toán thành công</title>" +
                    "<style>" +
                    "body{font-family:Arial,sans-serif;display:flex;justify-content:center;align-items:center;min-height:100vh;margin:0;background:#f0f9f0;}" +
                    ".container{text-align:center;padding:40px;background:white;border-radius:16px;box-shadow:0 4px 20px rgba(0,0,0,0.1);max-width:400px;}" +
                    ".icon{font-size:64px;margin-bottom:16px;}" +
                    ".title{font-size:24px;color:#22c55e;margin-bottom:8px;}" +
                    ".desc{color:#666;margin-bottom:24px;}" +
                    ".info{background:#f8f8f8;padding:12px;border-radius:8px;text-align:left;margin-bottom:16px;}" +
                    ".btn{background:#22c55e;color:white;padding:12px 32px;border-radius:8px;text-decoration:none;display:inline-block;}" +
                    "</style></head><body>" +
                    "<div class='container'>" +
                    "<div class='icon'>✅</div>" +
                    "<div class='title'>Thanh toán thành công!</div>" +
                    "<div class='desc'>Gói dịch vụ đã được kích hoạt</div>" +
                    "<div class='info'><strong>Mã giao dịch:</strong> " + txnRef + "</div>" +
                    "<p class='desc'>Bạn có thể đóng trang này và quay lại ứng dụng.</p>" +
                    "</div></body></html>"
            );
        } else {
            // Payment failed
            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().write(
                    "<!DOCTYPE html><html><head><meta charset='UTF-8'>" +
                    "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                    "<title>Thanh toán thất bại</title>" +
                    "<style>" +
                    "body{font-family:Arial,sans-serif;display:flex;justify-content:center;align-items:center;min-height:100vh;margin:0;background:#fef2f2;}" +
                    ".container{text-align:center;padding:40px;background:white;border-radius:16px;box-shadow:0 4px 20px rgba(0,0,0,0.1);max-width:400px;}" +
                    ".icon{font-size:64px;margin-bottom:16px;}" +
                    ".title{font-size:24px;color:#ef4444;margin-bottom:8px;}" +
                    ".desc{color:#666;margin-bottom:24px;}" +
                    ".btn{background:#ef4444;color:white;padding:12px 32px;border-radius:8px;text-decoration:none;display:inline-block;}" +
                    "</style></head><body>" +
                    "<div class='container'>" +
                    "<div class='icon'>❌</div>" +
                    "<div class='title'>Thanh toán thất bại</div>" +
                    "<div class='desc'>Mã lỗi: " + responseCode + "</div>" +
                    "<p class='desc'>Vui lòng thử lại hoặc chọn phương thức thanh toán khác.</p>" +
                    "</div></body></html>"
            );
        }
    }

    /**
     * Extract all request parameters into a Map
     */
    private Map<String, String> extractParams(HttpServletRequest request) {
        Map<String, String> params = new HashMap<>();
        Enumeration<String> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();
            String paramValue = request.getParameter(paramName);
            if (paramValue != null && !paramValue.isEmpty()) {
                params.put(paramName, paramValue);
            }
        }
        return params;
    }
}
