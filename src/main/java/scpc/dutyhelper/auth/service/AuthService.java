package scpc.dutyhelper.auth.service;

import scpc.dutyhelper.auth.model.User;
import scpc.dutyhelper.auth.payload.request.SignInRequest;
import scpc.dutyhelper.auth.payload.request.SignUpRequest;
import scpc.dutyhelper.auth.payload.response.JwtResponse;

public interface AuthService {
    User signUp(SignUpRequest signUpRequest, String baseUrl);

    JwtResponse signIn(SignInRequest signinRequest);

    void confirmEmail(String confirmationCode);

    void initializeResetPassword(String email, String baseUrl);

    void resetPassword(String confirmationCode, String password);
}
