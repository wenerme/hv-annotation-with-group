package me.wener.issues.demo.data.service.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentMap;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import me.wener.issues.demo.data.dto.UserDTO.Create;
import me.wener.issues.demo.data.dto.UserDTO.Update;
import me.wener.issues.demo.data.entity.UserEntity;
import me.wener.issues.demo.data.mapper.UserMapper;
import me.wener.issues.demo.data.service.AddressService;
import me.wener.issues.demo.data.service.UserService;
import org.springframework.stereotype.Service;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 08/06/2017
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final ConcurrentMap<String, UserEntity> users = Maps.newConcurrentMap();
    private final UserMapper userMapper;
    private final AddressService addressService;

    @PostConstruct
    private void init() {
        users.put("1", new UserEntity().setId("1").setName("Zoe").setEmail("zoe@xyz.com")
            .setAddress(addressService.require("1")));
        users.put("2", new UserEntity().setId("2").setName("Joy").setEmail("joy@xyz.com")
            .setAddress(addressService.require("2")));
    }

    @Override
    public UserEntity create(Create dto) {
        UserEntity user = userMapper.create(dto);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public UserEntity update(UserEntity user, Update dto) {
        return userMapper.update(dto, user);
    }

    @Override
    public UserEntity patch(UserEntity user, Update dto) {
        return userMapper.patch(dto, user);
    }

    @Override
    public Optional<UserEntity> findById(String id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public void delete(String user) {
        users.remove(user);
    }

    @Override
    public List<UserEntity> findAll() {
        return ImmutableList.copyOf(users.values());
    }
}
