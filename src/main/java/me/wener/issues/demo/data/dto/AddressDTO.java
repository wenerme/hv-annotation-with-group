package me.wener.issues.demo.data.dto;

import lombok.Data;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 07/06/2017
 */
public class AddressDTO {

    @Data
    public static class Summary {

        Long id;
        String street;
    }
}
