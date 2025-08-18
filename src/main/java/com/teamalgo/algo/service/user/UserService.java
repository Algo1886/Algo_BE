package com.teamalgo.algo.service.user;

import com.teamalgo.algo.domain.user.User;
import com.teamalgo.algo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public Optional<User> findByProviderAndProviderId(String provider, String providerId) {
        return userRepository.findByProviderAndProviderId(provider, providerId);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }
}
