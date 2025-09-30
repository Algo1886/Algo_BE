package com.teamalgo.algo.service.user;

import com.teamalgo.algo.domain.user.User;
import com.teamalgo.algo.dto.response.UserResponse;
import com.teamalgo.algo.dto.request.UserUpdateRequest;
import com.teamalgo.algo.global.common.code.ErrorCode;
import com.teamalgo.algo.global.common.util.UsernameGenerator;
import com.teamalgo.algo.global.exception.CustomException;
import com.teamalgo.algo.repository.UserRepository;
import com.teamalgo.algo.service.stats.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final StatsService statsService;
    private final UsernameGenerator usernameGenerator;

    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByProviderAndProviderId(String provider, String providerId) {
        return userRepository.findByProviderAndProviderId(provider, providerId);
    }

    public UserResponse getUserInfo (Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        int streak = statsService.getValidCurrentStreak(user);
        return UserResponse.from(user, streak);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    // 신규 가입 사용자
    public User createUser(String provider, String providerId, String avatarUrl) {
        String username;
        do {
            username = usernameGenerator.generateRandomUsername();
        } while (userRepository.existsByUsername(username));

        User user = User.builder()
                .provider(provider)
                .providerId(providerId)
                .username(username)
                .avatarUrl(avatarUrl)
                .build();

        return userRepository.save(user);
    }

    @Transactional
    public UserResponse updateUser(Long userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (request.getUsername() != null && !request.getUsername().equals(user.getUsername())) {
            if (userRepository.existsByUsername(request.getUsername())) {
                throw new CustomException(ErrorCode.DUPLICATE_USERNAME);
            }
            user.update(request.getUsername(), request.getAvatarUrl());
        } else {
            user.update(null, request.getAvatarUrl()); // 닉네임 변경 없음
        }

        int streak = statsService.getValidCurrentStreak(user);

        return UserResponse.from(user, streak);
    }

    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        userRepository.delete(user);
    }

}
