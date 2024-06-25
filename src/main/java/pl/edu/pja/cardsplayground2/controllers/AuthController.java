package pl.edu.pja.cardsplayground2.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pl.edu.pja.cardsplayground2.dtos.UserDTO;
import pl.edu.pja.cardsplayground2.entities.UserEntity;
import pl.edu.pja.cardsplayground2.repositories.UserRepository;
import pl.edu.pja.cardsplayground2.services.UserService;

@Controller
public class AuthController {

    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String loginRed() {
        return "login";
    }

    @PostMapping("/login")
    public String logIn(@RequestParam String username, @RequestParam String password, HttpServletRequest request, Model model){

        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(username);
        userDTO.setPassword(password);
        if(userService.logIn(userDTO)){

            UserDetails userDetails = User.builder()
                    .username(username)
                    .password(password)
                    .roles("USER")
                    .build();

            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authToken);

            HttpSession session = request.getSession();
            session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

            Long userId = userDTO.getId();
            return "redirect:/collection?userId=" + userId;
        }
        model.addAttribute("error", "Invalid username or password");
        return "login";
    }

    @PostMapping("/register")
    public String register(@RequestParam String username, @RequestParam String password, Model model) {

        if (userService.findByUsername(username) != null) {
            model.addAttribute("error", "Username already exists");
            return "login";
        }
        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setPassword(password);
        userService.register(user);

        return "redirect:/login?register_success";
    }

    @RequestMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.logout(request, response, SecurityContextHolder.getContext().getAuthentication());
        return "redirect:/login?logout";
    }
}
