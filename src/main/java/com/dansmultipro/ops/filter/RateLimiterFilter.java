package com.dansmultipro.ops.filter;

import com.dansmultipro.ops.exception.RateLimiterException;
import com.dansmultipro.ops.util.LoginRateLimiter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class RateLimiterFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;
    private final LoginRateLimiter loginRateLimiter;
    private final List<RequestMatcher> requestMatcherList;

    public RateLimiterFilter(ObjectMapper objectMapper, LoginRateLimiter loginRateLimiter,  @Qualifier("getRateLimitMatchers") List<RequestMatcher> requestMatcherList) {
        this.objectMapper = objectMapper;
        this.loginRateLimiter = loginRateLimiter;
        this.requestMatcherList = requestMatcherList;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        boolean matched = requestMatcherList.stream()
                .anyMatch(requestMatcher -> requestMatcher.matches(request));

        if (matched) {
            try {
                String ip = getClientIP(request);
                validateRateLimit(ip);
                filterChain.doFilter(request, response);
            } catch (RateLimiterException e) {
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.setContentType("application/json");
                response.getWriter().write(responseJSON(e.getMessage()));
            } catch (Exception e) {
                response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                response.setContentType("application/json");
                response.getWriter().write(responseJSON("An error occurred"));
            }
        } else {
            filterChain.doFilter(request, response);
        }
    }

    private String getClientIP(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private void validateRateLimit(String ip) {
        if (!loginRateLimiter.allowRequest(ip)) {
            Duration newDuration = loginRateLimiter.getNewDuration(ip);
            loginRateLimiter.extendRefill(ip, newDuration);

            String formatted = String.format("%02d:%02d",
                    newDuration.toMinutesPart(),
                    newDuration.toSecondsPart());

            throw new RateLimiterException(
                    "Rate limit exceeded. Please retry after " + formatted + " minutes");
        }
    }

    private String responseJSON(String message) throws JsonProcessingException {

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "Too many requests");
        errorResponse.put("message", message);
        errorResponse.put("status", HttpStatus.TOO_MANY_REQUESTS.value());

        return objectMapper.writeValueAsString(errorResponse);
    }

}
