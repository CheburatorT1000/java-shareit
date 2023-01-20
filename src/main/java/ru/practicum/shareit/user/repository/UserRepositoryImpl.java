package ru.practicum.shareit.user.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final HashMap<Long, User> users = new HashMap<>();
    private long id = 0;

    private long makeId() {
        return ++id;
    }

    @Override
    public boolean checkExistId(long id) {
        return users.containsKey(id);
    }

    @Override
    public boolean checkExistEmail(String email) {
        if(email != null) {
            return users.values().stream()
                    .anyMatch(user -> email.equals(user.getEmail()));
        }
        return false;
    }

    @Override
    public User create(User user) {
        user.setId(makeId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User read(long id) {
        return users.get(id);
    }


    @Override
    public void delete(long id) {
        users.remove(id);
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }
}