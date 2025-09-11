package br.com.gustavobarez.Kairo.Security;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class SecurityUserFilter extends OncePerRequestFilter {

    private JWTUserProvider jwtProvider;

    public SecurityUserFilter(JWTUserProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    private static final List<String> PUBLIC_ENDPOINTS = Arrays.asList(
            "/api/v1/user",
            "/api/v1/auth",
            "/swagger-ui",
            "/v3/api-docs",
            "/actuator");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestURI = request.getRequestURI();

        if (isPublicEndpoint(requestURI)) {
            filterChain.doFilter(request, response);
            return;
        }

       if (requestURI.startsWith("/api/v1/")) {
            String header = request.getHeader("Authorization");

            if (header != null) {
                try {
                    String token = header.startsWith("Bearer ") ? header.substring(7) : header;

                    var decodedToken = this.jwtProvider.validateToken(token);

                    if (decodedToken == null) {
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        return;
                    }

                    request.setAttribute("user_id", decodedToken.getSubject());

                    var rolesClaim = decodedToken.getClaim("roles");
                    List<SimpleGrantedAuthority> grants;

                    if (rolesClaim != null) {
                        var roles = rolesClaim.asList(Object.class);
                        grants = roles.stream()
                                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toString().toUpperCase()))
                                .toList();
                    } else {
                        grants = List.of(new SimpleGrantedAuthority("ROLE_USER"));
                    }

                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                            decodedToken.getSubject(), null, grants);
                    SecurityContextHolder.getContext().setAuthentication(auth);

                } catch (Exception e) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                }
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean isPublicEndpoint(String requestURI) {
        return PUBLIC_ENDPOINTS.stream().anyMatch(requestURI::startsWith);
    }
}