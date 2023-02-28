package scpc.dutyhelper.faq.model;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import scpc.dutyhelper.auth.model.UserDetailsImpl;

import javax.persistence.*;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@Setter
@ToString
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class Feedback {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @PositiveOrZero
    protected Long id;

    private String subject;

    @Column(length = 1000)
    private String body;

    private String author;

    @CreatedDate
    private LocalDateTime createdDate;

    @PrePersist
    public void prePersist() {
        this.createdDate = LocalDateTime.now();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl author = (UserDetailsImpl) authentication.getPrincipal();
        this.setAuthor(String.format("%s %s", author.getFirstName(), author.getLastName()));
    }
}
