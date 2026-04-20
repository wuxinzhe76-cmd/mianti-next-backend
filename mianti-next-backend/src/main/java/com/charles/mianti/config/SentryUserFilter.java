package com.charles.mianti.config;

import io.sentry.Sentry;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Sentry 错误监控配置
 * 在请求拦截时绑定用户信息到 Sentry Scope
 *
 * @author Charles
 */
@Component
@ConditionalOnProperty(prefix = "sentry", name = "dsn")
public class SentryUserFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    jakarta.servlet.http.HttpServletResponse response,
                                    jakarta.servlet.FilterChain filterChain)
            throws jakarta.servlet.ServletException, java.io.IOException {
        Sentry.withScope(scope -> {
            scope.setUser(null);
            try {
                filterChain.doFilter(request, response);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
