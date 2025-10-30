package com.mycompany.bookme.security.controllers;

import com.mycompany.bookme.api.LoginApi;
import com.mycompany.bookme.api.RegisterApi;
import com.mycompany.bookme.model.AuthenticationDTO;
import com.mycompany.bookme.model.AuthenticationRequestDTO;
import com.mycompany.bookme.model.RegisterRequestDTO;
import com.mycompany.bookme.security.services.AuthenticationService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.NativeWebRequest;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController implements RegisterApi, LoginApi {

  private final AuthenticationService authenticationService;

  @Override
  public ResponseEntity<AuthenticationDTO> login(
      AuthenticationRequestDTO authenticationRequestDTO) {
    return ResponseEntity.ok(authenticationService.authenticate(authenticationRequestDTO));
  }

  @Override
  public ResponseEntity<AuthenticationDTO> register(RegisterRequestDTO registerRequestDTO) {
    return ResponseEntity.ok(authenticationService.register(registerRequestDTO));
  }

  @Override
  public Optional<NativeWebRequest> getRequest() {
    return RegisterApi.super.getRequest();
  }
}
