package scpc.dutyhelper.auth.service;

import scpc.dutyhelper.auth.model.User;
import scpc.dutyhelper.auth.model.role.Role;

import java.util.List;
import java.util.Set;

public interface UserService {

    List<User> getAll();

    User get(Long id);

    User create(User user);

    User update(Long id, User user);

    User updateRoles(Long id, Set<Role> roles);

    void delete(Long id);
}
