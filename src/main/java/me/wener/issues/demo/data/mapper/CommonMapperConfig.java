package me.wener.issues.demo.data.mapper;

import static org.mapstruct.CollectionMappingStrategy.ADDER_PREFERRED;

import org.mapstruct.MapperConfig;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 08/06/2017
 */
@MapperConfig(
    componentModel = "spring",
    uses = {EntityMapper.class},
    collectionMappingStrategy = ADDER_PREFERRED
)
public interface CommonMapperConfig {

}
