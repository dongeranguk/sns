package com.fastcampus.sns.controller;

import com.fastcampus.sns.controller.request.UserJoinRequest;
import com.fastcampus.sns.controller.request.UserLoginRequest;
import com.fastcampus.sns.exception.ErrorCode;
import com.fastcampus.sns.exception.SnsApplicationException;
import com.fastcampus.sns.model.User;
import com.fastcampus.sns.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;


    @DisplayName("회원가입 - 정상 호출")
    @Test
    public void givenUserInfo_whenJoined_thenJoins() throws Exception {

        // Given
        String userName = "userName";
        String password = "password";

        // When
        when(userService.join(userName, password)).thenReturn(mock(User.class));

        mvc.perform(post("/api/v1/users/join")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(new UserJoinRequest(userName, password)))
        ).andDo(print())
                .andExpect(status().isOk());

        // Then
    }

    @DisplayName("회원가입 - 이미 가입된 아이디로 가입하는 경우 에러 반환")
    @Test
    public void givenExistsUserInfo_whenJoined_thenThrowsException() throws Exception {

        // Given
        String userName = "userName";
        String password = "password";

        when(userService.join(userName, password)).thenThrow(new SnsApplicationException(ErrorCode.DUPLICATED_USER_NAME, ""));

        // When & Then
        mvc.perform(post("/api/v1/users/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new UserJoinRequest(userName, password)))
                ).andDo(print())
                .andExpect(status().isConflict());
    }

    @DisplayName("로그인 - 정상 호출")
    @Test
    public void givenUserInfo_whenLogin_thenLogins() throws Exception {

        // Given
        String userName = "userName";
        String password = "password";

        when(userService.login(userName, password)).thenReturn("test-token");

        // When & Then
        mvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new UserLoginRequest(userName, password)))
                ).andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("로그인 - 회원가입이 안된 아이디로 로그인시 에러 반환")
    @Test
    public void givenNotExistsUserInfo_whenLogin_thenThrowsException() throws Exception {

        // Given
        String userName = "userName";
        String password = "password";

        when(userService.login(userName, password)).thenThrow(new SnsApplicationException(ErrorCode.DUPLICATED_USER_NAME, ""));

        // When & Then
        mvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new UserLoginRequest(userName, password)))
                ).andDo(print())
                .andExpect(status().isNotFound());
    }

    @DisplayName("로그인 - 틀린 유저 정보를 입력한 경우 에러 반환")
    @Test
    public void givenIncorrectUserInfo_whenLogin_thenThrowsException() throws Exception {

        // Given
        String userName = "userName";
        String password = "password";

        when(userService.login(userName, password)).thenThrow(new SnsApplicationException(ErrorCode.DUPLICATED_USER_NAME, ""));

        // When & Then
        mvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new UserLoginRequest(userName, password)))
                ).andDo(print())
                .andExpect(status().isUnauthorized());
    }
}