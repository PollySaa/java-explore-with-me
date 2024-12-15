package ru.practicum.service.user;

import ru.practicum.dto.user.UserDto;

import java.util.List;

public interface UserService {

    UserDto createUser(UserDto userDto);

    void deleteUser(Long id);

    List<UserDto> getUsersByIds(List<Long> ids, Integer from, Integer size);
}
