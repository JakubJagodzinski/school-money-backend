package com.example.schoolmoney.interceptor;

import com.example.schoolmoney.auth.access.SecurityUtils;
import com.example.schoolmoney.requestlog.AsyncRequestLogger;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

@RequiredArgsConstructor
@Component
public class RequestLoggingInterceptor implements HandlerInterceptor {

    private final AsyncRequestLogger asyncRequestLogger;

    private final SecurityUtils securityUtils;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        request.setAttribute("startTime", System.currentTimeMillis());

        String traceId = UUID.randomUUID().toString();
        request.setAttribute("traceId", traceId);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler,
                                Exception ex) {

        long start = (Long) request.getAttribute("startTime");
        long duration = System.currentTimeMillis() - start;
        String traceId = (String) request.getAttribute("traceId");
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String queryString = request.getQueryString();
        String clientIp = getClientIp(request);
        String userAgent = request.getHeader("User-Agent");
        String status = String.valueOf(response.getStatus());
        UUID userId = null;

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            userId = securityUtils.getCurrentUserId();
        }

        asyncRequestLogger.logRequest(
                method,
                uri,
                queryString,
                clientIp,
                userAgent,
                status,
                userId,
                duration,
                traceId
        );
    }

    private String getClientIp(HttpServletRequest request) {
        String xf = request.getHeader("X-Forwarded-For");
        return xf == null ? request.getRemoteAddr() : xf.split(",")[0].trim();
    }

}
