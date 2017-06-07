package me.wener.issues.demo.data.dto;

import lombok.Data;
import me.wener.issues.demo.Validations.NotNullOnChange;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 07/06/2017
 */
public class UserDTO {


    @Data
    public static class Summary {

        private String id;
        private String email;
        private String name;
    }

    @Data
    public static class Detail extends Summary {

        private Integer age;
        private AddressDTO.Summary address;
    }

    @Data
    public static class Create extends Update {

        @NotEmpty// Unmodifiable
        private String email;
    }

    @Data
    public static class Update {

        @NotNullOnChange// When create or full update this is required
        private String name;
        private Integer age;
        private String address;

    }
}
