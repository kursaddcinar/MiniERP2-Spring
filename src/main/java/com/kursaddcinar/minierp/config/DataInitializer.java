package com.kursaddcinar.minierp.config;

import com.kursaddcinar.minierp.service.IAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final IAuthService authService;

    @Override
    public void run(String... args) throws Exception {
        // Uygulama ayağa kalktığında burası çalışır.
        // initializeTestUsers metodu zaten içeride "if exist" kontrolü yaptığı için
        // her seferinde çalışmasında sakınca yok.
        authService.initializeTestUsers();
        
        System.out.println("----------- SYSTEM INITIALIZED -----------");
        System.out.println("Admin User: admin / admin123");
        System.out.println("------------------------------------------");
    }
}