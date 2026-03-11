package com.lanhcare.service;

import com.lanhcare.entity.CustomerRequest;

import java.util.List;

public interface DeletionRequestService {
    CustomerRequest createRequest(String email, String reason);
    CustomerRequest updateStatus(Integer id, CustomerRequest.RequestStatus status);
    List<CustomerRequest> getAllRequests();
    void verifyCode(String code);
}
