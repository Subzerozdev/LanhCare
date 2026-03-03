package com.lanhcare.service;

import com.lanhcare.dto.subscription.DashboardProResponse;

public interface DashboardProService {
    DashboardProResponse getDashboard(Integer accountId);
}
