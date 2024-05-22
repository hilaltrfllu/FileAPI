package com.example.FileAPI;

import com.example.FileAPI.model.User;
import com.example.FileAPI.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findAll().isEmpty()) {
            User user1 = new User();
            user1.setUsername("hilal");
            user1.setPassword(passwordEncoder.encode("l1wZ3VrSj24x7mH!8jLkVbnC9*pgQ4YbD3t^E#FsG7u2L1zN@0P%Uk6?9JmXp*Y"));
            userRepository.save(user1);
        }
    }
}
