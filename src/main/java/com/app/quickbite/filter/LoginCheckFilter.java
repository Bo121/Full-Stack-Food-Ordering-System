package com.app.quickbite.filter;

import com.alibaba.fastjson.JSON;
import com.app.quickbite.common.BaseContext;
import com.app.quickbite.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * The filter is registered with the Servlet container using the @WebFilter annotation.
 * The `urlPatterns` attribute specifies the request paths intercepted by the filter. "/*" means intercept all requests.
 * The `filterName` attribute specifies the name of the filter.
 * The @Slf4j annotation automatically injects a logger object.
 *
 * <p>Processing logic of this filter:
 * 1. Retrieve the URL of the current request.
 * 2. Determine whether the request needs to be processed.
 * 3. If the request does not need processing, allow it to proceed.
 * 4. Check the login status. If the user is logged in, allow the request to proceed.
 * 5. If the user is not logged in, return a "not logged in" response.
 * <p/>
 */
@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")  // "/*" indicates interception of all requests
@Slf4j
public class LoginCheckFilter implements Filter {

    // Path matcher
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        // Convert ServletRequest and ServletResponse to HttpServletRequest and HttpServletResponse
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        // 1. Retrieve the URL of the current request
        String requestURI = request.getRequestURI();
        log.info("Request URL: {}", requestURI); // Log the request URL

        // 2. Determine whether the request needs to be processed
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/user/login",
                "/user/sendMsg"
        }; // URLs that do not require processing

        boolean needProcess = check(urls, requestURI); // Check if the request needs to be processed

        // 3. If the request does not need processing, allow it to proceed
        if (needProcess) {
            log.info("Request {} does not need processing. Allowing it to proceed.", requestURI);
            filterChain.doFilter(request, response); // Allow the request to proceed
            return;
        }

        // 4-1. Check employee login status. If logged in, allow the request to proceed
        Object employeeId = request.getSession().getAttribute("employee");
        if (employeeId != null) {
            log.info("Request {} by employee ID={} is logged in. Allowing it to proceed.", requestURI, employeeId);

            // Save the current employee ID in the thread. `BaseContext` is a utility class in the `common` package
            BaseContext.setCurrentId((Long) employeeId);

            filterChain.doFilter(request, response); // Allow the request to proceed
            return;
        }

        // 4-2. Check user login status. If logged in, allow the request to proceed
        if (request.getSession().getAttribute("user") != null) {
            log.info("Request {} by user ID={} is logged in. Allowing it to proceed.", requestURI, request.getSession().getAttribute("user"));

            Long userId = (Long) request.getSession().getAttribute("user");
            // Save the current user ID in the thread
            BaseContext.setCurrentId(userId);

            filterChain.doFilter(request, response); // Allow the request to proceed
            return;
        }

        // 5. If not logged in, return a "not logged in" response
        // The frontend code (with js/request.js) handles redirecting to the login page. 
        // Here, just return a "not logged in" response as JSON.
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN"))); // Return "not logged in" response
        log.info("Request {} is not logged in. Returning 'not logged in' response.", requestURI);
    }

    /**
     * Path matching<br>
     * Determines whether the current request needs to be processed.
     *
     * @param urls       URLs that do not require processing
     * @param requestURI The current request URL
     * @return `true` if the URL matches one of the `urls` (no processing needed); `false` otherwise
     */
    public boolean check(String[] urls, String requestURI) {
        for (String url : urls) {
            if (PATH_MATCHER.match(url, requestURI)) {
                return true;
            }
        }
        return false;
    }
}
