package com.app.quickbite.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.app.quickbite.entity.User;

import javax.servlet.http.HttpSession;

public interface UserService extends IService<User> {

  /**
   * Send verification code
   *
   * @param email   Email address
   * @param session HTTP session
   */
  void sendCode(String email, HttpSession session);

  /**
   * Login using verification code. If the user is new, automatically register them.
   *
   * @param email   Email address
   * @param code    Verification code
   * @param session HTTP session
   * @return User information
   */
  User loginByVerificationCode(String email, String code, HttpSession session);
}

