package scpc.dutyhelper.auth.payload.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class JwtResponse {

    private String tokenType = "Bearer";
    private String accessToken, firstName, lastName, email;
    private Long id;
    private List<String> roles;

    @Builder
    public JwtResponse(String accessToken,
                       Long id,
                       String firstName,
                       String lastName,
                       String email,
                       List<String> roles) {

        this.accessToken = accessToken;
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.roles = roles;
    }
}
