package com.app.quickbite.controller;

import com.app.quickbite.common.R;
import com.app.quickbite.entity.User;
import com.app.quickbite.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    /**
     * User service
     */
    @Autowired
    private UserService userService;

    /**
     * <h2>Send verification code<h2/>
     *
     * @param user    User entity from the frontend, primarily containing the email address
     * @param session HTTP session
     * @return Generic response object
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session) {
        log.info("Sending verification code, user info: {}, session: {}", user, session);
        // Get the email address provided by the frontend
        String email = user.getEmail();
        // Check if the email address is not empty
        if (!StringUtils.isEmpty(email)) {
            // Send verification code
            userService.sendCode(email, session); // Use utility class to send verification code; sendCode method is in the UserService interface
            return R.success("Verification code sent successfully. Please check your email promptly.");
        }
        return R.error("Failed to send verification code");
    }

    /**
     * <h2>Login using verification code<h2/>
     * <p>If the user is new, automatically register them</p>
     *
     * @param map     User entity from the frontend, primarily containing the email address and verification code
     * @param session HTTP session
     * @return Returns the User entity to save this information in the browser
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session) {
        log.info("User login, user info: {}, session: {}", map.toString(), session);
        // Get the email address and verification code from the map
        String email = (String) map.get("email");
        String code = (String) map.get("code");
        // Check if the email address and verification code are not empty
        if (!StringUtils.isEmpty(email) && !StringUtils.isEmpty(code)) {
            // Login using verification code
            User user = userService.loginByVerificationCode(email, code, session); // Use utility class to handle login; login method is in the UserService interface
            return R.success(user);
        }
        return R.error("Login failed. Please check the email address and verification code.");
    }

    /**
     * <h2>Logout<h2/>
     *
     * @param session HTTP session
     * @return Generic response object
     */
    @PostMapping("/loginout")
    public R<String> logout(HttpSession session) {
        log.info("User logout, session: {}", session);
        // Clear the session
        session.invalidate(); // Use session.invalidate() to clear the current session
        return R.success("Logout successful");
    }
}

