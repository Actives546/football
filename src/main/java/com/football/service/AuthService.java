package com.football.service;

public interface AuthService {

    String sendSmsCode(String phone);

    boolean verifyCode(String phone, String code);
}
