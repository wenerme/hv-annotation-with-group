package me.wener.issues.demo.data.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 07/06/2017
 */
@Getter
@Setter
public class AddressEntity implements Persistable<String> {

    private String id;
    private String country;
    private String street;
}
