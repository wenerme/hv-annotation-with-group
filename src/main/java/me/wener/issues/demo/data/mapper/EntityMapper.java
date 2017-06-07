package me.wener.issues.demo.data.mapper;

import java.io.Serializable;
import me.wener.issues.demo.data.entity.AddressEntity;
import me.wener.issues.demo.data.entity.Persistable;
import me.wener.issues.demo.data.service.AddressService;
import org.mapstruct.Mapper;
import org.mapstruct.TargetType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 08/06/2017
 */
@Mapper(componentModel = "spring")
public class EntityMapper {

    @Autowired
    @Lazy
    private AddressService addressService;

    public <T extends Persistable<ID>, ID extends Serializable> ID idFromPersistable(T s) {
        if (s == null) {
            return null;
        }
        return s.getId();
    }

    public <T extends Persistable<String>> T persistableFromString(String id,
        @TargetType Class<T> type) {
        if (id == null) {
            return null;
        }
        if (!type.equals(AddressEntity.class)) {
            // For test only, only handle one type
            throw new IllegalArgumentException("Wrong");
        }
        return (T) addressService.require(id);
    }
}
