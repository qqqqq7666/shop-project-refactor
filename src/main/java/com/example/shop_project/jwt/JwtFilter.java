package com.example.shop_project.jwt;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;

import java.io.IOException;
import java.util.Optional;
import org.springframework.util.StringUtils;
import com.example.shop_project.jwt.JwtProviderImpl;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import com.example.shop_project.jwt.AuthTokenImpl;
import static com.example.shop_project.jwt.MemberConstants.AUTHORIZATION_TOKEN_KEY;


@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtProviderImpl tokenProvider;

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        Optional<String> token = resolveToken(request);

        if (token.isPresent()) {
            AuthTokenImpl jwtToken = tokenProvider.convertAuthToken(token.get().split(" ")[1]);

            if (jwtToken.validate()) {
                Authentication authentication = tokenProvider.getAuthentication(jwtToken);

                SecurityContextHolder
                    .getContext()
                    .setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }

    private Optional<String> resolveToken(HttpServletRequest request) {
        String authToken = request.getHeader(AUTHORIZATION_TOKEN_KEY);

        if (StringUtils.hasText(authToken)) {
            return Optional.of(authToken);
        } else {
            return Optional.empty();
        }
    }
}

