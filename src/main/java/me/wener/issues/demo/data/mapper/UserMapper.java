package me.wener.issues.demo.data.mapper;

import jodd.bean.BeanCopy;
import me.wener.issues.demo.data.dto.UserDTO;
import me.wener.issues.demo.data.dto.UserDTO.Create;
import me.wener.issues.demo.data.dto.UserDTO.Update;
import me.wener.issues.demo.data.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 07/06/2017
 */
@Mapper(config = CommonMapperConfig.class, uses = AddressMapper.class)
public interface UserMapper {

    UserDTO.Summary summary(UserEntity s);

    UserDTO.Detail detail(UserEntity s);

    UserEntity create(Create dto);

    UserEntity update(Update dto, @MappingTarget UserEntity user);

    default UserEntity patch(Update dto, UserEntity user) {
        if (dto == null) {
            return user;
        }
        // Propose here https://github.com/mapstruct/mapstruct/issues/879
        Update update = update(user);
        BeanCopy.beans(dto, update).ignoreNulls(true).copy();
        return update(update, user);
    }

    Update update(UserEntity s);
}
