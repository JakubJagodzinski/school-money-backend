package com.example.schoolmoney.user;

import com.example.schoolmoney.common.constants.messages.PasswordMessages;
import com.example.schoolmoney.user.dto.request.ChangePasswordRequestDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void changePassword(ChangePasswordRequestDto changePasswordRequestDto, Principal connectedUser) throws IllegalArgumentException {
        log.debug("Changing password for user {}", connectedUser.getName());

        User user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();

        if (!passwordEncoder.matches(changePasswordRequestDto.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException(PasswordMessages.WRONG_PASSWORD);
        }

        if (!changePasswordRequestDto.getNewPassword().equals(changePasswordRequestDto.getConfirmationPassword())) {
            throw new IllegalArgumentException(PasswordMessages.PASSWORDS_DONT_MATCH);
        }

        user.setPassword(passwordEncoder.encode(changePasswordRequestDto.getNewPassword()));

        userRepository.save(user);

        log.debug("Password successfully changed for user {}", connectedUser.getName());
    }

}
