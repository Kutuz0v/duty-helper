package scpc.dutyhelper.faq.model;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.PositiveOrZero;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@Setter
@ToString
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class Question {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @PositiveOrZero
    protected Long id;

    private String question;

    @Column(columnDefinition = "TEXT")
    private String answer;
}
