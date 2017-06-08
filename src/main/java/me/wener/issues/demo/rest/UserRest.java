package me.wener.issues.demo.rest;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import me.wener.issues.demo.data.dto.UserDTO;
import me.wener.issues.demo.data.dto.UserDTO.Detail;
import me.wener.issues.demo.data.dto.UserDTO.Summary;
import me.wener.issues.demo.data.mapper.UserMapper;
import me.wener.issues.demo.data.service.UserService;
import me.wener.issues.demo.validator.ValidCreate;
import me.wener.issues.demo.validator.ValidUpdate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author <a href=http://github.com/wenerme>wener</a>
 * @since 07/06/2017
 */
@RestController
@RequestMapping("/user")
@Validated
@RequiredArgsConstructor
public class UserRest {

    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping// Some field is required
    public Object post(@RequestBody @ValidCreate UserDTO.Create user) {
        return "OK";
    }

    @GetMapping
    public List<Summary> list(
        @RequestParam(name = "offset", defaultValue = "0") Integer offset,
        @RequestParam(name = "limit", defaultValue = "10") Integer limit
    ) {
        return userService.findAll().stream().skip(offset).limit(limit).map(userMapper::summary)
            .collect(
                Collectors.toList());
    }

    @PutMapping("/{user}")// Some field is required
    public Map<String, Object> put(@PathVariable String user,
        @RequestBody @ValidUpdate UserDTO.Update dto) {
        userService.update(userService.require(user), dto);
        return ImmutableMap.of("code", 0);
    }

    @PatchMapping("/{user}")// Every field can be null
    public Map<String, Object> patch(@PathVariable String user,
        @RequestBody @ValidUpdate UserDTO.Update dto) {
        userService.patch(userService.require(user), dto);
        return ImmutableMap.of("code", 0);
    }


    @GetMapping("/{user}")
    public Detail get(@PathVariable String user) {
        return userMapper.detail(userService.require(user));
    }

    @DeleteMapping("/{user}")
    public void delete(@PathVariable String user) {
        userService.delete(user);
    }

}
