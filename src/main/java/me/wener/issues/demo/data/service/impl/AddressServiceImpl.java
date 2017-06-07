package me.wener.issues.demo.data.service.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import java.util.concurrent.ConcurrentMap;
import javax.annotation.PostConstruct;
import me.wener.issues.demo.data.entity.AddressEntity;
import me.wener.issues.demo.data.service.AddressService;
import org.springframework.stereotype.Service;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 08/06/2017
 */
@Service
public class AddressServiceImpl implements AddressService {

    private final ConcurrentMap<String, AddressEntity> entities = Maps.newConcurrentMap();

    @PostConstruct
    private void init() {
        entities.put("1", new AddressEntity().setId("1").setStreet("A"));
        entities.put("2", new AddressEntity().setId("2").setStreet("B"));
        entities.put("3", new AddressEntity().setId("3").setStreet("C"));
    }

    @Override
    public AddressEntity require(String id) {
        return Preconditions.checkNotNull(entities.get(id), "Not found");
    }
}
