package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.entity.User;
import com.edu.ulab.app.exception.DAOException;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.mapper.UserMapper;
import com.edu.ulab.app.service.UserService;
import com.edu.ulab.app.storage.Storage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final Storage storage;

    private final UserMapper userMapper;

    @Autowired
    public UserServiceImpl(Storage storage, UserMapper userMapper) {
        this.storage = storage;
        this.userMapper = userMapper;
    }
    @Override
    public UserDto createUser(UserDto userDto) {
        User user = userMapper.userDtoToUser(userDto);
        Long userId = user.getId();

        if (userId != null) {
            Optional<User> userFind = storage.getUserById(userId);

            if (userFind.isPresent()) {
                throw new DAOException("Пользователь с ID: " + userId + " уже имеется в системе и не может быть создан");
            }
        }

        Optional<User> userOpt = storage.saveUser(user);

        if (userOpt.isPresent()) return userMapper.userToUserDto(userOpt.get());
        else throw new DAOException("Пользователь с ID: " + userId + " не был сохранен");
    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        User user = userMapper.userDtoToUser(userDto);
        Optional<User> userOpt = storage.updateUser(user);

        if (userOpt.isPresent()) return userMapper.userToUserDto(userOpt.get());
        else throw new NotFoundException("Пользователь не найден");
    }

    @Override
    public UserDto getUserById(Long id) {
        Optional<User> userOpt = storage.getUserById(id);

        if (userOpt.isPresent()) return userMapper.userToUserDto(userOpt.get());
        else throw new NotFoundException("Пользователь с ID: " + id + " не найден");
    }

    @Override
    public void deleteUserById(Long id) {
        if (!storage.deleteUser(id)) throw new NotFoundException("Пользователь с ID: " + id + " не был найден");
    }

    @Override
    public void setBooksForUser(Long userId, List<Long> bookIdList) {
        Optional<User> userOpt = storage.getUserById(userId);

        if (userOpt.isPresent()) {
            userOpt.get().setBookList(bookIdList);
            storage.saveUser(userOpt.get());
        } else throw new NotFoundException("Пользователь с ID: " + userId + " не найден");
    }
}
