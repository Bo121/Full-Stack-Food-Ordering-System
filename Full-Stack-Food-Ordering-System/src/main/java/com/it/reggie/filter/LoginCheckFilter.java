package com.it.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.it.reggie.common.BaseContext;
import com.it.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Check if the user has completed login
 */
@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {
    // Path matcher, supporting wildcards
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        // 1. Get the URI of this request
        String requestURI = request.getRequestURI(); // /backend/index.html

        log.info("Intercepted request: {}", requestURI);

        // Define request paths that do not need to be processed
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg",
                "/user/login"
        };

        // 2. Check if this request needs to be processed
        boolean check = check(urls, requestURI);

        // 3. If it does not need to be processed, let it pass directly
        if (check) {
            log.info("This request {} does not need to be processed", requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        // 4-1. Check login status, if logged in, let it pass directly
        if (request.getSession().getAttribute("employee") != null) {
            log.info("User is logged in, user id: {}", request.getSession().getAttribute("employee"));

            Long empId = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);

            filterChain.doFilter(request, response);
            return;
        }

        // 4-2. Check login status, if logged in, let it pass directly
        if (request.getSession().getAttribute("user") != null) {
            log.info("User is logged in, user id: {}", request.getSession().getAttribute("user"));

            Long userId = (Long) request.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);

            filterChain.doFilter(request, response);
            return;
        }

        log.info("User is not logged in");
        // 5. If not logged in, return the not logged in result, respond data to the client page through output stream
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
    }

    /**
     * Path matching, check if this request needs to be let through
     * @param urls
     * @param requestURI
     * @return
     */
    public boolean check(String[] urls, String requestURI) {
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if (match) {
                return true;
            }
        }
        return false;
    }
}
