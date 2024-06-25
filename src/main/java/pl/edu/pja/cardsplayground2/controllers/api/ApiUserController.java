package pl.edu.pja.cardsplayground2.controllers.api;

import pl.edu.pja.cardsplayground2.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.edu.pja.cardsplayground2.dtos.*;
import pl.edu.pja.cardsplayground2.entities.UserEntity;


@RestController
@RequestMapping("/api")
public class ApiUserController {
    private final UserService userService;

    @Autowired
    public ApiUserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/users/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserDTO userDTO){
        UserEntity user = userService.extractUser(userDTO);
        if(userService.register(user)){
            userDTO.setId(user.getId());
            return ResponseEntity.ok(userDTO);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @PostMapping("/users/login")
    public ResponseEntity<?> logInUser(@Valid @RequestBody UserDTO userDTO){
        if(userService.logIn(userDTO)){
            UserEntity user = userService.getUser(userDTO.getId());
            userDTO.setId(user.getId());
            return ResponseEntity.ok(userDTO);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }






}
