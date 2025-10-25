package com.example.schoolmoney.auth.jwt;

import com.example.schoolmoney.auth.authtoken.AuthTokenService;
import com.example.schoolmoney.common.constants.SecurityConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    private final UserDetailsService userDetailsService;

    private final AuthTokenService authTokenService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        final String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader == null || !authorizationHeader.startsWith(SecurityConstants.HEADER_START)) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authorizationHeader.substring(SecurityConstants.HEADER_START.length());

        String username;
        try {
            username = jwtService.extractUsername(jwt);
        } catch (Exception e) {
            logger.warn("Invalid JWT token: " + e.getMessage());
            filterChain.doFilter(request, response);
            return;
        }

        if (username == null || SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            boolean isJwtValid = jwtService.isJwtValid(jwt, userDetails);
            boolean isAccessTokenValid = authTokenService.isActiveAccessToken(jwt);
            boolean isAccountNonLocked = userDetails.isAccountNonLocked();
            boolean isAccountNonExpired = userDetails.isAccountNonExpired(); // always true for now, reserved for future account expiry feature
            if (isJwtValid && isAccessTokenValid && isAccountNonLocked && isAccountNonExpired) {
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        } catch (Exception e) {
            logger.warn("JWT authentication failed", e);
        }

        filterChain.doFilter(request, response);
    }

}
