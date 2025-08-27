package com.teamalgo.algo.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamalgo.algo.domain.user.User;
import com.teamalgo.algo.global.common.api.ApiResponse;
import com.teamalgo.algo.global.common.code.ErrorCode;
import com.teamalgo.algo.global.exception.CustomException;
import com.teamalgo.algo.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final List<String> EXCLUDED_PATHS = List.of(
            "/api/auth/kakao-login",
            "/api/auth/google-login",
            "/api/auth/github-login",
            "/api/auth/refresh",
            "/swagger-ui", // Swagger
            "/v3/api-docs" // OpenAPI docs
    );

    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        String path = request.getRequestURI();

        // 특정 경로는 필터 적용 안 함
        if (EXCLUDED_PATHS.stream().anyMatch(path::startsWith)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // 1) Authorization 헤더에서 토큰 추출
            String token = getTokenFromRequest(request);

            if (token == null || !jwtTokenProvider.validateToken(token)) {
                throw new CustomException(ErrorCode.JWT_TOKEN_INVALID);
            }

            // 2) 토큰에서 userId 추출
            Long userId = jwtTokenProvider.getUserIdFromToken(token);

            // 3) DB에서 유저 조회
            User user = userService.findById(userId)
                    .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

            // 4) CustomUserDetails 로 감싸기
            CustomUserDetails principal = new CustomUserDetails(user);

            // 5) 인증 객체 생성
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            principal, null, principal.getAuthorities()
                    );
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // 6) SecurityContext 에 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);

        } catch (CustomException e) {
            log.error("JWT 인증 실패: {}", e.getMessage());
            response.setStatus(e.getErrorCode().getStatus().value());
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            objectMapper.writeValue(response.getWriter(),
                    ApiResponse.fail(e.getErrorCode()).getBody());
        }
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}

