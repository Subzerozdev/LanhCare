package com.lanhcare.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Method;
import java.util.Arrays;

@Slf4j
public class AsyncExceptionHandler implements AsyncUncaughtExceptionHandler {

    @Override
    public void handleUncaughtException(Throwable throwable, Method method, Object... params) {
        // Log exception overall
        log.error("Async method '{}' with parameters {} threw an exception.",
                method.getName(), Arrays.toString(params), throwable);

        // Log exception details
        if (throwable instanceof ImageException) {
            logEmailException(params);
        }
    }

    private void logEmailException(Object... params) {
        if (params.length > 0 && params[0] instanceof MultipartFile file) {
            log.error("File Name: {}, Content Type: {}",
                    file.getName(), file.getContentType());
        }
    }
}
