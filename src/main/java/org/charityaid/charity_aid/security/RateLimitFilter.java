package org.charityaid.charity_aid.security;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.charityaid.charity_aid.service.AppConfigService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

    private final AppConfigService appConfigService;
    private final Map<String, Counter> counters = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String path = request.getRequestURI();
        if (!path.startsWith("/api/")) {
            filterChain.doFilter(request, response);
            return;
        }

        String key = request.getRemoteAddr();
        long minute = Instant.now().getEpochSecond() / 60;
        Counter counter = counters.computeIfAbsent(key, k -> new Counter(minute, 0));
        synchronized (counter) {
            if (counter.minute != minute) {
                counter.minute = minute;
                counter.count = 0;
            }
            counter.count++;
            if (counter.count > appConfigService.getRateLimitPerMinute()) {
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.setContentType("application/json");
                response.getWriter().write("{\"success\":false,\"message\":\"Rate limit exceeded\"}");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private static final class Counter {
        long minute;
        int count;

        Counter(long minute, int count) {
            this.minute = minute;
            this.count = count;
        }
    }
}
