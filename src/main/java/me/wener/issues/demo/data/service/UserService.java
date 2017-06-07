package me.wener.issues.demo.data.service;

import java.util.List;
import java.util.Optional;
import me.wener.issues.demo.Validations.ValidCreate;
import me.wener.issues.demo.Validations.ValidModify;
import me.wener.issues.demo.Validations.ValidPatch;
import me.wener.issues.demo.data.dto.UserDTO;
import me.wener.issues.demo.data.entity.UserEntity;
import org.springframework.validation.annotation.Validated;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 08/06/2017
 */
@Validated
public interface UserService {

    UserEntity create(@ValidCreate UserDTO.Create dto);

    UserEntity update(UserEntity user, @ValidModify UserDTO.Update dto);

    UserEntity patch(UserEntity user, @ValidPatch UserDTO.Update dto);

    Optional<UserEntity> findById(String id);

    void delete(String user);

    default UserEntity require(String user) {
        return findById(user).orElseThrow(() -> new RuntimeException("NotFound"));
    }

    List<UserEntity> findAll();
}
