package scpc.dutyhelper.auth.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import scpc.dutyhelper.auth.payload.request.*;
import scpc.dutyhelper.auth.service.AuthService;

import javax.validation.Valid;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService service;

    @PostMapping("/signin")
    public ResponseEntity<?> signIn(@Valid @RequestBody SignInRequest signinRequest) {
        return ResponseEntity.ok(
                service.signIn(signinRequest)
        );
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@Valid @RequestBody SignUpRequest signUpRequest) {
        return ResponseEntity.ok(service.signUp(signUpRequest, signUpRequest.getRedirectUrl()));
    }

    @PostMapping("/confirm-email")
    public ResponseEntity<?> confirm(@RequestBody ConfirmRequest request) {
        service.confirmEmail(request.getConfirmationCode());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/password")
    public ResponseEntity<?> initializeResetPassword(@RequestBody PasswordResetInitializeRequest request) {
        service.initializeResetPassword(request.getEmail(), request.getRedirectUrl());
        return ResponseEntity.accepted().build();
    }

    @PutMapping("/password")
    public ResponseEntity<?> resetPassword(@RequestBody PasswordResetRequest request) {
        service.resetPassword(request.getConfirmationCode(), request.getPassword());
        return ResponseEntity.noContent().build();
    }
/*    private String getSiteURL(HttpServletRequest request) {
        String siteURL = request.getRequestURL().toString();
        return siteURL.replace(request.getServletPath(), "");
    }*/
}
