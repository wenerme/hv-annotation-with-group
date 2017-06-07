package me.wener.issues.demo.data.mapper;

import me.wener.issues.demo.data.dto.AddressDTO;
import me.wener.issues.demo.data.entity.AddressEntity;
import org.mapstruct.Mapper;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 07/06/2017
 */
@Mapper(config = CommonMapperConfig.class)
public interface AddressMapper {

    AddressDTO.Summary summary(AddressEntity s);
}
