# hv-annotation-with-group
Hibernate Validator - Constraint Annotation with Default group

From [Can I add a bean validation annotation with group?](https://stackoverflow.com/questions/44366294)

## Why

For RESTful api service, most time is dealing with crud, so, we follow

```
GET   -> Query
POST  -> Create
PUT   -> Update
PATCH -> Partial Update - Update nonnull field
DELETE-> Delete
```

For an entity, we defined several dto, like this

```
public class UserDTO {

    @Data// Used for list
    public static class Summary {

        private String id;
        private String code;
        private String name;
    }

    @Data// Used for getById
    public static class Detail extends Summary {

        private String phone;
        private String department;
        private String position;
        private String email;
        private String avatar;
        private List<String> permissions;
        private List<RoleDTO.Summary> roles;
    }

}
```



## Demo


```java
@RestController
@RequestMapping("/demo")
@Validated
public class DemoRest {

  @PostMapping// Some field is required
  public Object post(@RequestBody @ValidCreate UserCreateDTO user) {
    return "OK";
  }

  @PutMapping// Some field is required
  public Object put(@RequestBody @ValidModify UserDTO user) {
    return "OK";
  }

  @PatchMapping// Every field can be null
  public Object patch(@RequestBody @ValidPatch UserDTO user) {
    return "OK";
  }

  @Data
  public static class UserCreateDTO extends UserDTO {

    @NotEmpty
    private String phone;
  }

  @Data
  public static class UserDTO {

    @Size(min = 1)
    @NotNullOnChange// Required when change or create
    private String name;

    private Integer age;
  }
}
```

```bash
####
# Create
####
# Invalid
curl -X POST -H 'Content-Type: application/json' -d '{"name": "abc"}' http://localhost:8080/demo
# Valid
curl -X POST -H 'Content-Type: application/json' -d '{"name": "abc","phone":"123"}' http://localhost:8080/demo

####
# Modify
####
# Invalid - name is required for full update
curl -X PUT -H 'Content-Type: application/json' -d '{"age": 123}' http://localhost:8080/demo
# Valid
curl -X PUT -H 'Content-Type: application/json' -d '{"name":"abcd","age": 123}' http://localhost:8080/demo

# Valid - Only update nonnull field
curl -X PATCH -H 'Content-Type: application/json' -d '{}' http://localhost:8080/demo
```
