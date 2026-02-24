package com.lanhcare.aspect;

import com.lanhcare.annotation.RequiresFeature;
import com.lanhcare.exception.exps.FeatureNotAvailableException;
import com.lanhcare.security.JwtTokenProvider;
import com.lanhcare.service.FeatureGateService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;

/**
 * AOP Aspect that intercepts controller methods annotated with @RequiresFeature.
 * Checks user's subscription before allowing the method to execute.
 * For quota-limited features, also records usage after successful execution.
 */
@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class FeatureGateAspect {

    private final FeatureGateService featureGateService;
    private final JwtTokenProvider jwtTokenProvider;

    @Around("@annotation(com.lanhcare.annotation.RequiresFeature)")
    public Object checkFeature(ProceedingJoinPoint joinPoint) throws Throwable {
        // 1. Get the annotation
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RequiresFeature annotation = method.getAnnotation(RequiresFeature.class);
        String featureCode = annotation.value();

        // 2. Extract accountId from Authorization header
        Integer accountId = extractAccountId();
        if (accountId == null) {
            throw new FeatureNotAvailableException(featureCode, "Vui lòng đăng nhập để sử dụng tính năng này.");
        }

        // 3. Check access
        boolean canAccess = featureGateService.canAccess(accountId, featureCode);
        if (!canAccess) {
            log.info("Feature gate blocked: account={}, feature={}", accountId, featureCode);
            throw new FeatureNotAvailableException(featureCode);
        }

        // 4. Execute the method
        Object result = joinPoint.proceed();

        // 5. Record usage AFTER successful execution (for quota features)
        featureGateService.recordUsage(accountId, featureCode);

        return result;
    }

    private Integer extractAccountId() {
        try {
            ServletRequestAttributes attrs =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs == null) return null;

            HttpServletRequest request = attrs.getRequest();
            String token = request.getHeader("Authorization");
            if (token == null || token.isEmpty()) return null;

            return Integer.parseInt(jwtTokenProvider.getIdentifierFromToken(token));
        } catch (Exception e) {
            log.warn("Failed to extract accountId from token: {}", e.getMessage());
            return null;
        }
    }
}
