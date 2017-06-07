# hv-annotation-with-group
Hibernate Validator - Constraint Annotation with Default group

From [Can I add a bean validation annotation with group?](https://stackoverflow.com/questions/44366294)

## About this project
This project is a demo that `hv-annotation-with-group`, use a lot of magic to make this `@ConstraintGroup({Create.class, Modify.class}) @interface NotNullOnChange {}` work.

This project contain full rest,dto,mapper,service,entity, the data is simulated, in real project, spring-data will replace the data layer, but the whole structure keep unchanged.   

## Run & Test
```bash
./mvnw spring-boot:run
# Open http://localhost:8080/swagger-ui.html
# Try 
# user id: 1,2
# address id: 1,2
```

For create, and full update, `name` is required, for patch `name` is optional.

## Use case

### Share DTO with different operation
CRUD RESTful update have PUT or PATCH, PATCH used for partial update, update nonnull field only, so, every field can be null.

If Patch and Put share same DTO, with default group can code like this

```java
@Data
public static class Update {

    @NotNullOnChange// When create or full update this is required
    private String name;
    private Integer age;
    private String address;
}
```

Valid like this

```java
interface UserService{
    UserEntity update(UserEntity user, @ValidModify UserDTO.Update dto);
    UserEntity patch(UserEntity user, @ValidPatch UserDTO.Update dto);
}
```

`@NotNullOnChange` is

```java
@NotNull
@ConstraintGroup({Create.class, Modify.class})// Notice this annotation
@interface NotNullOnChange {}
```
