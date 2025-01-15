package com.app.quickbite.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.app.quickbite.common.CustomException;
import com.app.quickbite.entity.User;
import com.app.quickbite.mapper.UserMapper;
import com.app.quickbite.service.UserService;
import com.app.quickbite.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
  // By extending ServiceImpl, the basic CRUD methods are implemented.

  /**
   * Email sender
   */
  @Autowired
  private JavaMailSender javaMailSender;

  /**
   * Email address used to send verification codes
   */
  @Value("${spring.mail.username}")
  private String FROM;

  /**
   * Expiration time for verification codes (in minutes)
   */
  @Value("${spring.mail.timeout}")
  private int TIME_OUT_MINUTES;

  /**
   * RedisTemplate for Redis operations
   */
  @Autowired
  private RedisTemplate<Object, Object> redisTemplate;

  /**
   * Send a verification code
   *
   * @param email   The email address to which the code will be sent
   * @param session The session object
   */
  public void sendCode(String email, HttpSession session) {
      // Generate a 4-digit verification code
      String code = ValidateCodeUtils.generateValidateCode(4).toString();
      log.info("Session:{}, Code:{}, to {}", session, code, email); // Logging the session and code

      // Create an email object
      SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
      simpleMailMessage.setFrom(FROM); // Set sender email
      simpleMailMessage.setTo(email); // Set recipient email
      simpleMailMessage.setSubject("[QuickBite Takeout] Login Verification Code"); // Set email subject
      simpleMailMessage.setText("Welcome to QuickBite Takeout Platform! \n\nYour verification code is: "
                                 + code + ". Please use it within " 
                                 + TIME_OUT_MINUTES + " minutes.\n\n[This email is auto-generated, please do not reply.]");
      
      // Save the verification code to Redis with an expiration time
      redisTemplate.opsForValue().set(email, code, TIME_OUT_MINUTES, java.util.concurrent.TimeUnit.MINUTES);

      // Send the email
      try {
          javaMailSender.send(simpleMailMessage);
      } catch (MailException e) {
          e.printStackTrace();
          throw new CustomException("Critical error occurred!");
      }
  }

  /**
   * Log in with a verification code. If the user is new, they are automatically registered.
   *
   * @param email   Email address of the user
   * @param code    Verification code
   * @param session Session object
   * @return The User object
   */
  @Override
  public User loginByVerificationCode(String email, String code, HttpSession session) {
      // Retrieve the verification code from Redis
      Object verificationCodeInRedis = redisTemplate.opsForValue().get(email);

      // Check if the verification code is correct
      if (verificationCodeInRedis != null && verificationCodeInRedis.equals(code)) {
          // Verification code is correct
          // Check if the user exists
          LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
          queryWrapper.eq(User::getEmail, email);
          User user = this.getOne(queryWrapper); // Query user

          // If the user doesn't exist, automatically register them
          if (user == null) {
              user = new User();
              user.setEmail(email);
              user.setStatus(1); // Set user status to active
              this.save(user);
          }

          // Store user information in the session
          session.setAttribute("user", user.getId());

          // Delete the verification code from Redis
          redisTemplate.delete(email);

          return user;
      } else {
          // Verification code is incorrect
          throw new CustomException("Verification code is incorrect!");
      }
  }
}
