package ru.khkhlv.messenger.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.khkhlv.messenger.dto.UserDto;
import ru.khkhlv.messenger.model.User;
import ru.khkhlv.messenger.repository.UserRepository;
import ru.khkhlv.messenger.service.UserService;
import ru.khkhlv.messenger.util.SecurityContextHelper;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final SecurityContextHelper securityContextHelper;

    @GetMapping("/users/search")
    public ResponseEntity<List<UserDto>> searchUsers(@RequestParam String query) {
        List<UserDto> users = userService.searchUsers(query);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser() {
        Long userId = securityContextHelper.getCurrentUserId();
        User user = userRepository.findById(userId).orElseThrow();
        UserDto dto = new UserDto(user.getId(), user.getUsername(), user.getEmail());
        return ResponseEntity.ok(dto);
    }
}
