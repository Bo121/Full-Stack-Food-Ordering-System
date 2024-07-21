package com.it.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.it.reggie.common.R;
import com.it.reggie.entity.User;
import com.it.reggie.service.UserService;
import com.it.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * Send mobile SMS verification code
     * @param user
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){
        // Get phone number
        String phone = user.getPhone();

        if(StringUtils.isNotEmpty(phone)){
            // Generate a random 4-digit verification code
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("code={}", code);

            // Call the SMS service API provided by Aliyun to send the SMS
            // SMSUtils.sendMessage("Reggie Takeaway", "", phone, code);

            // Save the generated verification code to the session
            session.setAttribute(phone, code);

            return R.success("Mobile verification code sent successfully");
        }

        return R.error("Failed to send SMS");
    }

    /**
     * Mobile user login
     * @param map
     * @param session
     * @return
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session){
        log.info(map.toString());

        // Get phone number
        String phone = map.get("phone").toString();

        // Get verification code
        String code = map.get("code").toString();

        // Get the saved verification code from the session
        Object codeInSession = session.getAttribute(phone);

        // Compare the verification codes (the code submitted from the page and the code saved in the session)
        if(codeInSession != null && codeInSession.equals(code)){
            // If the comparison is successful, it means login is successful

            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone, phone);

            User user = userService.getOne(queryWrapper);
            if(user == null){
                // Check if the current phone number corresponds to a new user, if it is, automatically complete registration
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            session.setAttribute("user", user.getId());
            return R.success(user);
        }
        return R.error("Login failed");
    }
}
