package scpc.dutyhelper.auth.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import scpc.dutyhelper.auth.model.User;
import scpc.dutyhelper.auth.model.UserDetailsImpl;
import scpc.dutyhelper.auth.model.role.ERole;
import scpc.dutyhelper.auth.service.UserService;
import scpc.dutyhelper.exception.ForbiddenException;

import java.util.List;

@Slf4j
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService service;

    @GetMapping
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    public List<User> getAllUsers() {
        return service.getAll();
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    public User createUser(@RequestBody User user) {
        log(user);
        return service.create(user);
    }

    @PutMapping("/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody User user) {
        if (!isAdmin()) {
            user.setRoles(null);
            user.setEnabled(null);
        } else {
            if (!isOwnerOrAdmin(id))
                throw new ForbiddenException("You don't have permission to update this user");
        }
        log(user);
        return service.update(id, user);
    }

    @PreAuthorize("hasAuthority('USER')")
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        log(get(id));
        if (isOwnerOrAdmin(id))
            service.delete(id);
        else throw new ForbiddenException("You don't have permission to delete this user");
    }

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/{id}")
    public User get(@PathVariable Long id) {
        return service.get(id);
    }

    private boolean isOwnerOrAdmin(Long id) {
        return isOwner(id) || isAdmin();
    }

    private boolean isOwner(Long id) {
        UserDetailsImpl principal = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return principal.getId().equals(id);
    }

    private boolean isAdmin() {
        UserDetailsImpl principal = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return principal.getAuthorities().contains(new SimpleGrantedAuthority(ERole.ADMINISTRATOR.name()));

    }

    private void log(Object o) {
        UserDetailsImpl principal = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        String callingMethodName = stackTraceElements[2].getMethodName();
        log.info("{} {} {}", principal.getEmail(), callingMethodName, o);
    }

}
