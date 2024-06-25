package pl.edu.pja.cardsplayground2.controllers.api;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.edu.pja.cardsplayground2.dtos.*;
import pl.edu.pja.cardsplayground2.entities.Card;
import pl.edu.pja.cardsplayground2.entities.UserEntity;
import pl.edu.pja.cardsplayground2.services.CardService;
import pl.edu.pja.cardsplayground2.services.UserService;
import java.util.List;


@RestController
@RequestMapping("/api")
public class ApiCardController {

    private final CardService cardService;
    private final UserService userService;

    @Autowired
    public ApiCardController(CardService cardService, UserService userService) {
        this.cardService = cardService;
        this.userService = userService;
    }


    @PostMapping("/cards/create")
    public ResponseEntity<?> createCard(@Valid @RequestBody CardLogInDTO cardLogInDTO){

        UserDTO userDTO = cardLogInDTO.getUser();
        CardDTO cardDTO = cardLogInDTO.getCard();

        if(userService.logIn(userDTO)){
            Card card = cardService.extractCard(cardDTO);
            UserEntity user = userService.getUser(userDTO.getId());

            cardService.saveCard(card, user);
            userService.bindCard(card, user);
            cardDTO.setId(card.getId());
            return ResponseEntity.ok(cardDTO);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @GetMapping("/cards/{id}")
    public ResponseEntity<?> getCardById(@PathVariable Long id){
        CardDTO cardDTO = cardService.getCardDTOById(id);
        if(cardDTO != null){
            return ResponseEntity.ok(cardDTO);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PatchMapping("/cards/update/{id}")
    public ResponseEntity<?> updateCardById(@PathVariable Long id,
                                            @Valid @RequestBody CardLogInDTO cardLogInDTO){
        UserDTO userDTO = cardLogInDTO.getUser();
        CardDTO cardDTO = cardLogInDTO.getCard();

        if(userService.logIn(userDTO)){
            Card card = cardService.extractCard(cardDTO);
            Card original = cardService.getCardById(id);
            UserEntity user = userService.getUser(userDTO.getId());

            if(userService.checkOwner(original, user)){
                if(cardService.updateCard(id, card)){
                    cardDTO.setId(original.getId());
                    return ResponseEntity.ok(cardDTO);
                }
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @DeleteMapping("/cards/remove/{id}")
    public ResponseEntity<?> removeCardById(@PathVariable Long id,
                                            @Valid @RequestBody UserDTO userDTO){

        if(userService.logIn(userDTO)){

            Card card = cardService.getCardById(id);
            UserEntity user = userService.getUser(userDTO.getId());
            if(userService.checkOwner(card, user)){
                cardService.removeCard(id);
                return ResponseEntity.status(HttpStatus.OK).build();
            }
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @GetMapping("/cards")
    public ResponseEntity<?> getCardsByUser(@Valid @RequestBody UserDTO userDTO) {

        if (userService.logIn(userDTO)) {
            UserEntity user = userService.getUser(userDTO.getId());
            List<CardDTO> list = cardService.getCardsByUser(user);
            return ResponseEntity.ok(list);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

}
