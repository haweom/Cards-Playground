package pl.edu.pja.cardsplayground2.controllers.html;

import org.springframework.dao.DataIntegrityViolationException;
import pl.edu.pja.cardsplayground2.services.CardService;
import pl.edu.pja.cardsplayground2.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pl.edu.pja.cardsplayground2.dtos.*;
import pl.edu.pja.cardsplayground2.entities.Card;
import pl.edu.pja.cardsplayground2.entities.UserEntity;
import java.util.Objects;


@Controller
public class HtmlCardController {

    private final CardService cardService;
    private final UserService userService;


    @Autowired
    public HtmlCardController(CardService cardService, UserService userService) {
        this.cardService = cardService;
        this.userService = userService;
    }

    @GetMapping("/html/cards/edit")
    public String editCard(@RequestParam Long userId, @RequestParam Long cardId ,
                           @AuthenticationPrincipal User userAuth, Model model){
        UserEntity authenticatedUser = userService.findByUsername(userAuth.getUsername());
        CardDTO cardDTO = cardService.getCardDTOById(cardId);

        UserEntity user = userService.getUser(userId);
        boolean admin = authenticatedUser.getAdmin();
        if (Objects.equals(user.getId(), authenticatedUser.getId()) || admin){
            if(user.getCards().contains(cardService.getCardById(cardDTO.getId()))){
                model.addAttribute("userId", userId);
                model.addAttribute("user", user);
                model.addAttribute("card", cardDTO);
                return "card-builder";
            }
        }
        return "redirect:/logout";
    }

    @PostMapping("/html/cards/create")
    public ResponseEntity<?> createCard(@Valid @RequestBody CardLogInDTO cardLogInDTO,
                                        @AuthenticationPrincipal User userAuth){
        try {
            UserEntity authenticatedUser = userService.findByUsername(userAuth.getUsername());
            UserDTO userDTO = cardLogInDTO.getUser();
            CardDTO cardDTO = cardLogInDTO.getCard();

            boolean admin = authenticatedUser.getAdmin();
            if (userDTO.getPassword().equals(authenticatedUser.getPassword()) || admin) {
                Card card = cardService.extractCard(cardDTO);
                UserEntity user = userService.getUser(userDTO.getId());

                cardService.saveCard(card, user);
                userService.bindCard(card, user);
                cardDTO.setId(card.getId());
                return ResponseEntity.ok(cardDTO);
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }catch (DataIntegrityViolationException e){
            return ResponseEntity.badRequest().body("Card name must be unique");
        }
    }

    @PatchMapping("/html/cards/update/{id}")
    public ResponseEntity<?> updateCardById(@PathVariable Long id,
                                            @Valid @RequestBody CardLogInDTO cardLogInDTO,
                                            @AuthenticationPrincipal User userAuth) {
        try{
            UserEntity authenticatedUser = userService.findByUsername(userAuth.getUsername());
            UserDTO userDTO = cardLogInDTO.getUser();
            CardDTO cardDTO = cardLogInDTO.getCard();

            boolean admin = authenticatedUser.getAdmin();
            if (userDTO.getPassword().equals(authenticatedUser.getPassword()) || admin) {

                Card card = cardService.extractCard(cardDTO);
                Card original = cardService.getCardById(id);
                UserEntity user = userService.getUser(userDTO.getId());

                if (userService.checkOwner(original, user)) {
                    if (cardService.updateCard(id, card)) {
                        cardDTO.setId(original.getId());
                        return ResponseEntity.ok(cardDTO);
                    }
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
                }
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }catch (DataIntegrityViolationException e){
            return ResponseEntity.badRequest().body("Card name must be unique");
        }
    }

    @DeleteMapping("/html/cards/remove/{cardId}")
    public ResponseEntity<?> removeCardById(@PathVariable Long cardId,
                                            @RequestParam Long userId,
                                            @AuthenticationPrincipal User userAuth){
        UserEntity authenticatedUser = userService.findByUsername(userAuth.getUsername());
        UserDTO userDTO = userService.getUserDto(userId);

        boolean admin = authenticatedUser.getAdmin();
        if(userDTO.getPassword().equals(authenticatedUser.getPassword()) || admin){
            Card card = cardService.getCardById(cardId);
            UserEntity user = userService.getUser(userDTO.getId());
            if(userService.checkOwner(card, user)){
                cardService.removeCard(cardId);
                return ResponseEntity.status(HttpStatus.OK).build();
            }
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
