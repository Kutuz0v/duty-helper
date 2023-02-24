package scpc.dutyhelper.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import scpc.dutyhelper.auth.model.role.ERole;
import scpc.dutyhelper.auth.model.role.Role;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);

}
