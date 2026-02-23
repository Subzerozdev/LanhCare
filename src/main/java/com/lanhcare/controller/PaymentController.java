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
     * For mobile: redirects to deep link lanhcare://payment-result
     * For web: shows HTML result page with deep link auto-redirect
     */
    @GetMapping("/vnpay-return")
    @Operation(summary = "VNPay return URL", 
               description = "Return URL for VNPay to redirect user's browser after payment. Redirects to mobile deep link.")
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
        String status = "00".equals(responseCode) ? "success" : "failed";

        // Deep link for React Native app
        String deepLink = String.format("lanhcare://payment-result?status=%s&txnRef=%s&responseCode=%s",
                status, txnRef, responseCode);

        // Return HTML page that auto-redirects to deep link
        // If deep link doesn't work (e.g., opened in external browser), show result page
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().write(
                "<!DOCTYPE html><html><head><meta charset='UTF-8'>" +
                "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "<title>Kết quả thanh toán</title>" +
                "<style>" +
                "body{font-family:Arial,sans-serif;display:flex;justify-content:center;align-items:center;min-height:100vh;margin:0;" +
                "background:" + ("success".equals(status) ? "#f0f9f0" : "#fef2f2") + ";}" +
                ".container{text-align:center;padding:40px;background:white;border-radius:16px;box-shadow:0 4px 20px rgba(0,0,0,0.1);max-width:400px;}" +
                ".icon{font-size:64px;margin-bottom:16px;}" +
                ".title{font-size:24px;color:" + ("success".equals(status) ? "#22c55e" : "#ef4444") + ";margin-bottom:8px;}" +
                ".desc{color:#666;margin-bottom:24px;}" +
                ".info{background:#f8f8f8;padding:12px;border-radius:8px;text-align:left;margin-bottom:16px;}" +
                ".btn{background:" + ("success".equals(status) ? "#22c55e" : "#ef4444") + ";color:white;padding:12px 32px;border-radius:8px;text-decoration:none;display:inline-block;}" +
                "</style></head><body>" +
                "<div class='container'>" +
                "<div class='icon'>" + ("success".equals(status) ? "✅" : "❌") + "</div>" +
                "<div class='title'>" + ("success".equals(status) ? "Thanh toán thành công!" : "Thanh toán thất bại") + "</div>" +
                "<div class='desc'>" + ("success".equals(status) ? "Gói dịch vụ đã được kích hoạt" : "Mã lỗi: " + responseCode) + "</div>" +
                "<div class='info'><strong>Mã giao dịch:</strong> " + txnRef + "</div>" +
                "<a class='btn' href='" + deepLink + "'>Quay lại ứng dụng</a>" +
                "<p class='desc' style='margin-top:16px;font-size:12px;'>Đang chuyển về ứng dụng...</p>" +
                "</div>" +
                "<script>" +
                "setTimeout(function(){window.location.href='" + deepLink + "';}, 1500);" +
                "</script>" +
                "</body></html>"
        );
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
