package scpc.dutyhelper.auth.model.role;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.PositiveOrZero;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString(callSuper = true)
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @PositiveOrZero
    protected Long id;

//    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private ERole name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Role role)) return false;

        return Objects.equals(id, role.id) &&
                Objects.equals(name, role.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
