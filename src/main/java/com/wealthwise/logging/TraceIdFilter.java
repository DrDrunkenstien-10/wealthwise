package com.wealthwise.logging;

import com.wealthwise.user.service.CurrentUserProvider;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.security.oauth2.jwt.Jwt;

import java.io.IOException;
import java.util.UUID;

public class TraceIdFilter implements Filter {

    private static final String TRACE_ID_HEADER = "X-Request-ID";
    private static final String TRACE_ID_KEY = "traceId";
    private static final String USER_ID_KEY = "userId";
    private static final String METHOD_KEY = "httpMethod";
    private static final String URI_KEY = "requestUri";
    private static final String CLIENT_IP_KEY = "clientIp";
    private static final String DURATION_KEY = "duration";

    private final CurrentUserProvider currentUserProvider;

    public TraceIdFilter(CurrentUserProvider currentUserProvider) {
        this.currentUserProvider = currentUserProvider;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        long startTime = System.currentTimeMillis();

        try {
            if (request instanceof HttpServletRequest httpRequest
                    && response instanceof HttpServletResponse httpResponse) {

                // 1. traceId
                String traceId = httpRequest.getHeader(TRACE_ID_HEADER);
                if (traceId == null || traceId.isEmpty()) {
                    traceId = UUID.randomUUID().toString();
                }
                ThreadContext.put(TRACE_ID_KEY, traceId);

                // 2. userId
                Jwt jwt = SecurityContextJwtExtractor.extractJwt();
                if (jwt != null) {
                    UUID userId = currentUserProvider.getCurrentUserId(jwt);
                    ThreadContext.put(USER_ID_KEY, userId.toString());
                }

                // 3. method, URI, client IP
                ThreadContext.put(METHOD_KEY, httpRequest.getMethod());
                ThreadContext.put(URI_KEY, httpRequest.getRequestURI());
                ThreadContext.put(CLIENT_IP_KEY, getClientIp(httpRequest));

                // 4. Wrap response to capture HTTP status
                StatusCaptureResponseWrapper responseWrapper = new StatusCaptureResponseWrapper(httpResponse);

                // 5. Proceed with filter chain
                chain.doFilter(request, responseWrapper);

            } else {
                chain.doFilter(request, response);
            }

        } finally {
            // 7. Duration
            long duration = System.currentTimeMillis() - startTime;
            ThreadContext.put(DURATION_KEY, String.valueOf(duration));
            // Clear MDC
            ThreadContext.clearAll();
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader != null && !xfHeader.isEmpty()) {
            return xfHeader.split(",")[0];
        }
        return request.getRemoteAddr();
    }
}
