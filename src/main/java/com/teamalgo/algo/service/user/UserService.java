package com.teamalgo.algo.service.user;

import com.teamalgo.algo.domain.user.User;
import com.teamalgo.algo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

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

    // 신규 가입 사용자
    public User createUser(String provider, String providerId, String avatarUrl) {
        User user = User.builder()
                .provider(provider)
                .providerId(providerId)
                .username(generateUniqueUsername())
                .avatarUrl(avatarUrl)
                .build();

        return userRepository.save(user);
    }

    public String generateRandomUsername() {
        // UUID 이용해서 랜덤 handle 생성
        String prefix = "algo_";
        String randomPart = UUID.randomUUID().toString()
                .replace("-", "")
                .substring(0, 8);
        return prefix + randomPart;
    }

    public String generateUniqueUsername() {
        String username;
        do {
            username = generateRandomUsername();
        } while (userRepository.existsByUsername(username));
        return username;
    }

}
