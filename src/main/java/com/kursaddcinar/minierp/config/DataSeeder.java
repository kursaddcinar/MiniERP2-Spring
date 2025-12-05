/*
package com.kursaddcinar.minierp.config;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import com.kursaddcinar.minierp.jwt.entity.Role;
import com.kursaddcinar.minierp.jwt.entity.User;
// import com.project.repository.RoleRepository;
import com.kursaddcinar.minierp.jwt.UserRepository;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // Şifreyi hashlemek için şart!

    @Override
    public void run(String... args) throws Exception {
        
        // 1. ROLLERİ KONTROL ET VE EKLE
        List<String> roles = Arrays.asList("ADMIN", "MANAGER", "SALES", "PURCHASE", "FINANCE", "WAREHOUSE");

        for (String roleName : roles) {
            // Eğer rol veritabanında yoksa kaydet
            if (!roleRepository.existsByName(roleName)) {
                Role newRole = new Role();
                newRole.setName(roleName); // Senin entity yapında setRoleName vs olabilir
                roleRepository.save(newRole);
            }
        }

        // 2. ADMIN KULLANICISINI KONTROL ET VE EKLE
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            // Şifreyi ASLA düz metin kaydetme, Encoder kullan:
            admin.setPassword(passwordEncoder.encode("admin123")); 
            admin.setEmail("admin@minierp.com");
            admin.setActive(true);

            // Admin rolünü bul ve set et
            // Not: Senin yapında Role entity'si nasıl çekiliyorsa öyle yapmalısın.
            // Örnek: Optional<Role> adminRole = roleRepository.findByName("ADMIN");
            // if(adminRole.isPresent()) { admin.setRoles(Set.of(adminRole.get())); }
            
            // Basit varsayım:
            Role adminRole = roleRepository.findByName("ADMIN").orElseThrow();
            admin.setRoles(Set.of(adminRole));

            userRepository.save(admin);
            System.out.println("--- BAŞLANGIÇ: Admin kullanıcısı ve roller oluşturuldu ---");
        }
    }
}
*/