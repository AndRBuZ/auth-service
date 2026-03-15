package com.foodorder.auth_service.service;

import com.foodorder.auth_service.dto.request.AuthRegisterDto;
import com.foodorder.auth_service.dto.request.RefreshTokenRequestDto;
import com.foodorder.auth_service.dto.request.UserCreateDto;
import com.foodorder.auth_service.dto.request.UserLoginDto;
import com.foodorder.auth_service.dto.response.*;
import com.foodorder.auth_service.exception.UserInvalidCredentialsException;
import com.foodorder.auth_service.feign.client.UserRestClient;
import com.foodorder.auth_service.mapper.UserMapper;
import com.foodorder.auth_service.security.jwt.service.JwtService;
import com.foodorder.auth_service.security.jwt.service.RefreshTokenService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    RefreshTokenService refreshTokenService;
    @Mock
    private JwtService jwtService;
    @Mock
    private UserRestClient userRestClient;
    @Mock
    private UserMapper userMapper;
    @InjectMocks
    private AuthService authService;

    @Test
    void shouldFindService() {
        assertNotNull(authService);
    }

    @Test
    void shouldRegisterUser() {
        AuthRegisterDto dto = new AuthRegisterDto("tester", "testemail@test.com", "testpass");
        UserCreateDto mappedDto = new UserCreateDto("tester", "testemail@test.com", "HASHED");
        UserPublicDto publicDto = new UserPublicDto(1L, "tester", "testemail@test.com");

        when(userMapper.toCreateDto(any(), any())).thenReturn(mappedDto);
        when(userRestClient.createUser(any())).thenReturn(publicDto);

        AuthResponseDto response = authService.register(dto);

        assertNotNull(response);
        assertTrue(response.success());
        assertEquals("tester", response.data().name());

        verify(userRestClient).createUser(any());
        verify(userMapper).toCreateDto(any(), any());
    }

    @Test
    void shouldLoginUser() {
        UserLoginDto dto = new UserLoginDto("testemail@test.com", "testpass");
        String hashedPass = BCrypt.hashpw(dto.password(), BCrypt.gensalt());
        UserCredentialsDto credentialsDto = new UserCredentialsDto(1L, "tester", "testemail@test.com", hashedPass);
        UserPublicDto publicDto = new UserPublicDto(1L, "tester", "testemail@test.com");

        when(userRestClient.getUserCredentialsByEmail(dto.email())).thenReturn(credentialsDto);
        when(userMapper.toPublicDto(any())).thenReturn(publicDto);
        when(jwtService.generateAccessToken(any())).thenReturn("access_token");
        when(jwtService.generateRefreshToken(any())).thenReturn("refresh_token");

        LoginResponseDto loginResponse = authService.login(dto);

        assertNotNull(loginResponse);
        assertTrue(loginResponse.success());
        assertEquals("tester", loginResponse.data().name());
        assertEquals("testemail@test.com", loginResponse.data().email());

        verify(userRestClient).getUserCredentialsByEmail(dto.email());
        verify(userMapper).toPublicDto(any());
        verify(jwtService).generateAccessToken(any());
        verify(jwtService).generateRefreshToken(any());
        verify(refreshTokenService).saveNewSession(anyLong(), anyString());
    }

    @Test
    void shouldNotLoginUser() {
        UserLoginDto dto = new UserLoginDto("testemail@test.com", "testpass");
        String hashedPass = BCrypt.hashpw("wrongpassword", BCrypt.gensalt());
        UserCredentialsDto credentialsDto = new UserCredentialsDto(1L, "tester", "testemail@test.com", hashedPass);
        UserPublicDto publicDto = new UserPublicDto(1L, "tester", "testemail@test.com");

        when(userRestClient.getUserCredentialsByEmail(dto.email())).thenReturn(credentialsDto);
        when(userMapper.toPublicDto(any())).thenReturn(publicDto);

        assertThrows(UserInvalidCredentialsException.class, () -> authService.login(dto));

        verify(jwtService, never()).generateAccessToken(any());
    }

    @Test
    void shouldRefreshToken() {
        RefreshTokenRequestDto refreshTokenRequestDto = new RefreshTokenRequestDto("refresh_token");
        RefreshTokenResponseDto refreshTokenResponseDto =
                new RefreshTokenResponseDto("new_refresh_token", "new_access_token");

        when(refreshTokenService.refresh(any())).thenReturn(refreshTokenResponseDto);

        RefreshTokenResponseDto refreshTokenResponse = authService.refresh(refreshTokenRequestDto);

        assertNotNull(refreshTokenResponse);
        assertEquals("new_refresh_token", refreshTokenResponse.getRefreshToken());
        assertEquals("new_access_token", refreshTokenResponse.getToken());

        verify(refreshTokenService).refresh("refresh_token");
    }


}
