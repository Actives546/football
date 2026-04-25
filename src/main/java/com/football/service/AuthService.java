package com.football.service;

public interface AuthService {

    void sendSmsCode(String phone);

    boolean verifyCode(String phone, String code);
}
