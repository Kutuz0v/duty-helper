package scpc.dutyhelper.auth.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import scpc.dutyhelper.auth.model.role.Role;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.WRITE_ONLY;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "users")
@Getter
@Setter
@ToString
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @PositiveOrZero
    protected Long id;

    @NotBlank
    @Size(max = 50)
    @Email(regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@scpc.gov.ua$")
    @Column(unique = true)
    private String email;

    @NotBlank
    @Size(max = 120)
    @JsonProperty(access = WRITE_ONLY)
    private String password;

    @Transient
    @JsonProperty(access = WRITE_ONLY)
    private String newPassword;

    @NotBlank(message = "First name cannot be blank")
    @Length(min = 2,
            message = "First name must be at least 2 characters long")
    @Length(max = 50,
            message = "First name cannot be more than 50 characters")
    private String firstName;

    @NotBlank(message = "Last name cannot be blank")
    @Length(min = 2,
            message = "Last name must be at least 2 characters long")
    @Length(max = 50,
            message = "Last name cannot be more than 50 characters")
    private String lastName;

    private Long telegramChatId = null;

    private Boolean enabled = false;

    @JsonProperty(access = WRITE_ONLY)
    private String confirmationCode;

    @ManyToMany()
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    @ToString.Exclude
    private Set<Role> roles = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;

        return Objects.equals(id, user.id) &&
                Objects.equals(email, user.email) &&
                Objects.equals(password, user.password) &&
                Objects.equals(roles, user.roles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, password, roles);
    }
}
