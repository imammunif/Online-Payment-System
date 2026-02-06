package com.dansmultipro.ops.filter;

import com.dansmultipro.ops.pojo.AuthorizationPoJo;
import com.dansmultipro.ops.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Component
public class TokenFilter extends OncePerRequestFilter {

    private final List<RequestMatcher> requestMatchers;

    public TokenFilter(List<RequestMatcher> requestMatchers) {
        this.requestMatchers = requestMatchers;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        var matched = requestMatchers.stream()
                .anyMatch(requestMatcher -> requestMatcher.matches(request));

        if (!matched) {
            try {
                String authHeader = request.getHeader("Authorization");
                if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    return;
                }
                String token = authHeader.substring(7);
                Claims claims = JwtUtil.validateToken(token);

                AuthorizationPoJo data = new AuthorizationPoJo(
                        UUID.fromString(claims.get("id").toString()),
                        claims.get("roleCode").toString()
                );
                String role = claims.get("roleCode").toString();
                List<SimpleGrantedAuthority> simpleGrantedAuthorityList = List.of(
                        new SimpleGrantedAuthority(role)
                );

                var auth = new UsernamePasswordAuthenticationToken(data, null, simpleGrantedAuthorityList);
                SecurityContextHolder.getContext().setAuthentication(auth);
                filterChain.doFilter(request, response);
            } catch (Exception e) {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
            }
        } else {
            filterChain.doFilter(request, response);
        }
    }
}
