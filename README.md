# Constraint annotation with explicit groups [HV-1355](https://hibernate.atlassian.net/browse/HV-1355)
Hibernate Validator - Constraint Annotation with Default group

Propose to add `@ConstraintGroup`

From [Can I add a bean validation annotation with group?](https://stackoverflow.com/questions/44366294)

## About this project
This project is a demo that `hv-annotation-with-group`, use a lot of magic to make this `@ConstraintGroup({Create.class, Modify.class}) @interface NotNullOnChange {}` work.

This project contain full rest,dto,mapper,service,entity, the data is simulated, in real project, spring-data will replace the data layer, but the whole structure keeps unchanged.   

Important content

```
validator
    @ConstraintGroup
    Groups
    @NotNullOnCreate
    @NotNullOnCreateOrUpdate
    @NullOnChange
    @NullOnPatch
    ValidatorHelper
    @ValidCreate
    @ValidPatch
    @ValidUpdate
```


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

### BeanValidation meet RESTful

HTTP Method | Operation | DTO | Note | Annotation
----|----|----|----
POST| Create | UserDTO.Create extend UserDTO.Update | Contain immutable(unmodifiable) and required field | `@ValidCreate`<br>`@NotNullOnCreate`<br>`@NotNullOnCreateOrUpdate`
PUT| Full update | UserDTO.Update | Contain required field, prohibit field | `@ValidUpdate`<br>`@NotNullOnCreateOrUpdate`<br>`@NullOnChange`
PATCH| Partial update | UserDTO.Update | Every field is option, prohibit field | `@ValidPatch`<br>`@NullOnChange`<br>`@NullOnPatch`


```java
public class UserDTO {
    @Data
    public static class Create extends Update {
        @NotEmpty// Unmodifiable
        private String email;
    }

    @Data
    public static class Update {
        @NotNullOnCreateOrUpdate// When create or full update this is required
        private String name;
        private Integer age;
        private String address;

    }
}
```

Validate like this

```java
interface UserService{
    UserEntity update(UserEntity user, @ValidCreate UserDTO.Create dto);
    UserEntity update(UserEntity user, @ValidUpdate UserDTO.Update dto);
    UserEntity patch(UserEntity user, @ValidPatch UserDTO.Update dto);
}
```

## What I've done

I implement a meta annotation(`@ConstraintGroup`) for constraint annotation to add explicit group, so, I don't have to set the `groups` for `@NotNull(groups={Create.class,Update.class})` everywhere.

Hibernate-Validator resist to accept any change, so, I have to do very nasty things to get this work.

* Code here `ValidatorHelper#patchHibernateValidator`
* Use reflection to get all `MetaDataProvider` out of Validator
* Replace all `MetaDataProvider` with a wrapper
    * Post process all `BeanConfiguration`
        * Alter groups in `MetaConstraint` if I found `ConstraintGroup`
        * Alter annotation `groups` attribute if I found `ConstraintGroup`
            * To workaround HIERARCHY `MetaDataBuilder#adaptOriginAndImplicitGroup`
            * __JDK internal__

## `@ConstraintGroup`
Add this annotation is very helpful, not only for constraint annotation, but also for `@Valid` annotation.

I have to use `@Validated` in spring to add extra groups, there is no such thing in bv 1.1, if `@ConstraintGroup` added, maybe can write customer valid annotation like this

```java
@Valid
@ConstraintGroup({Default.class, Patch.class})
public @interface ValidPatch {

}
```

So, I propose hibernate validator add some support for such annotation.
