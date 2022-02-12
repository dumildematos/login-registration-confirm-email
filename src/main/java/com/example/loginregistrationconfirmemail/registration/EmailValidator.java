package com.example.loginregistrationconfirmemail.registration;

import org.springframework.stereotype.Service;

import java.util.function.Predicate;

@Service
public class EmailValidator implements Predicate<String> {
    // TODO : Regex to validate email
    @Override
    public boolean test(String s) {
        return true;
    }
}
