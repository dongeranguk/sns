package com.fastcampus.sns.service;

import com.fastcampus.sns.exception.SnsApplicationException;
import com.fastcampus.sns.fixture.UserEntityFixture;
import com.fastcampus.sns.model.entity.UserEntity;
import com.fastcampus.sns.repository.UserEntityRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserEntityRepository userEntityRepository;

    @DisplayName("회원 가입 - 정상 동작")
    @Test
    public void givenUserInfo_whenCalledJoin_thenReturnsJoin() {

        // Given
        String userName = "userName";
        String password = "password";

        // When
        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.empty());
        when(userEntityRepository.save(any())).thenReturn(Optional.of(UserEntityFixture.get(userName, password)));

        // Then
        Assertions.assertDoesNotThrow(() -> userService.join(userName, password));

    }

    @DisplayName("회원 가입 - 이미 존재하는 아이디로 회원 가입하는 경우 에러 반환")
    @Test
    public void givenExistsUserInfo_whenCalledJoin_thenThrowsException() {

        // Given
        String userName = "userName";
        String password = "password";

        UserEntity fixture = UserEntityFixture.get(userName, password);

        // When
        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(fixture));
        when(userEntityRepository.save(any())).thenReturn(Optional.of(fixture));

        // Then
        Assertions.assertThrows(SnsApplicationException.class, () -> userService.join(userName, password));
    }

    @DisplayName("로그인 - 정상 동작")
    @Test
    public void givenUserInfo_whenCalledLogin_thenLogins() {

        // Given
        String userName = "userName";
        String password = "password";

        UserEntity fixture = UserEntityFixture.get(userName, password);


        // When
        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(fixture));
        when(userEntityRepository.save(any())).thenReturn(Optional.of(mock(UserEntity.class)));

        // Then
        Assertions.assertDoesNotThrow(() -> userService.login(userName, password));
    }

    @DisplayName("로그인 - 회원 가입된 아이디가 존재하지 않는 경우 에러 반환")
    @Test
    public void givenNotExistsUserInfo_whenCalledLogin_thenThrowsException() {

        // Given
        String userName = "userName";
        String password = "password";

        UserEntity fixture = UserEntityFixture.get(userName, password);


        // When
        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.empty());

        // Then
        Assertions.assertThrows(SnsApplicationException.class, () -> userService.login(userName, password));
    }

    @DisplayName("로그인 - 비밀번호가 틀린 경우 에러 반환")
    @Test
    public void givenIncorrectPassword_whenCalledLogin_thenThrowsException() {

        // Given
        String userName = "userName";
        String password = "password";
        String wrongPassword = "wrongPassword";

        UserEntity fixture = UserEntityFixture.get(userName, password);

        // When
        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.empty());

        // Then
        Assertions.assertThrows(SnsApplicationException.class, () -> userService.login(userName, wrongPassword));
    }

}
