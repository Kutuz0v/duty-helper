package scpc.dutyhelper.auth.service;

import scpc.dutyhelper.auth.model.User;

import java.util.List;

public interface UserService {

    List<User> getAll();

    User get(Long id);

    User create(User user);

    User update(Long id, User user);

    void delete(Long id);

    User connectTelegram(Long chatId, String code);

    List<Long> getAllTelegramUsersIds();
}
