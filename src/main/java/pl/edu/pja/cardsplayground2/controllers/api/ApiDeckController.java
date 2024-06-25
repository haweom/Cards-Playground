package pl.edu.pja.cardsplayground2.controllers.api;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.edu.pja.cardsplayground2.dtos.*;
import pl.edu.pja.cardsplayground2.entities.Card;
import pl.edu.pja.cardsplayground2.entities.Deck;
import pl.edu.pja.cardsplayground2.entities.UserEntity;
import pl.edu.pja.cardsplayground2.services.CardService;
import pl.edu.pja.cardsplayground2.services.DeckCardService;
import pl.edu.pja.cardsplayground2.services.DeckService;
import pl.edu.pja.cardsplayground2.services.UserService;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ApiDeckController {
    private final CardService cardService;
    private final DeckService deckService;
    private final UserService userService;
    private final DeckCardService deckCardService;

    @Autowired
    public ApiDeckController(CardService cardService, DeckService deckService, UserService userService, DeckCardService deckCardService) {
        this.cardService = cardService;
        this.deckService = deckService;
        this.userService = userService;
        this.deckCardService = deckCardService;
    }

    @GetMapping("/decks/{id}")
    public ResponseEntity<?> getDeckList(@PathVariable Long id,  @Valid @RequestBody UserDTO userDTO){

        if(userService.logIn(userDTO)){
            UserEntity user = userService.getUser(userDTO.getId());
            Deck deck = deckService.getUserDeckById(id,user);

            if(deck!=null){
                DeckDTO deckDTO = deckService.copyDto(deck);
                List<CardDTO> deckList = cardService.getCardDTOsByDeck(deck);
                deckDTO.setCardDTOList(deckList);
                return ResponseEntity.ok(deckDTO);
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PostMapping("/decks/create")
    public ResponseEntity<?> createDeck(@Valid @RequestBody DeckLogInDTO deckLogInDTO){
        UserDTO userDTO = deckLogInDTO.getUser();
        DeckDTO deckDTO = deckLogInDTO.getDeck();

        if(userService.logIn(userDTO)){
            Deck deck = deckService.extractDeck(deckDTO);
            UserEntity user = userService.getUser(userDTO.getId());

            deckService.saveDeck(deck, user);
            deckDTO.setId(deck.getId());
            return ResponseEntity.ok(deckDTO);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PostMapping("/decks/add/{deckId}/{cardId}")
    public ResponseEntity<?> addCardToDeck(@PathVariable Long cardId, @PathVariable Long deckId,
                                           @Valid @RequestBody UserDTO userDTO){
        if(userService.logIn(userDTO)){
            UserEntity user = userService.getUser(userDTO.getId());
            Deck deck = deckService.getUserDeckById(deckId,user);
            Card card = cardService.getUserCardById(cardId,user);

            if(deck!=null && card!=null){
                deckCardService.addCardToDeck(deck, card);

                DeckDTO deckDTO = deckService.copyDto(deck);
                List<CardDTO> deckList = cardService.getCardDTOsByDeck(deck);
                deckDTO.setCardDTOList(deckList);
                return ResponseEntity.ok(deckDTO);
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
    @DeleteMapping("/decks/remove/{deckId}/{cardId}")
    public ResponseEntity<?> removeCardFromDeck(@PathVariable Long cardId, @PathVariable Long deckId,
                                                @Valid @RequestBody UserDTO userDTO){
        if(userService.logIn(userDTO)){
            UserEntity user = userService.getUser(userDTO.getId());
            Deck deck = deckService.getUserDeckById(deckId,user);
            Card card = cardService.getUserCardById(cardId,user);

            if(deck!=null && card!=null){
                if(deckService.checkOwner(deck, card)){
                    deckCardService.removeCardFromDeck(deck, card);

                    DeckDTO deckDTO = deckService.copyDto(deck);
                    List<CardDTO> deckList = cardService.getCardDTOsByDeck(deck);
                    deckDTO.setCardDTOList(deckList);
                    return ResponseEntity.ok(deckDTO);
                }
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @DeleteMapping("/decks/remove/{id}")
    public ResponseEntity<?> removeDeck(@PathVariable Long id, @Valid @RequestBody UserDTO userDTO){

        if(userService.logIn(userDTO)){
            UserEntity user = userService.getUser(userDTO.getId());
            Deck deck = deckService.getUserDeckById(id,user);

            if(deck!=null){
                deckService.removeDeck(deck.getId());
                return ResponseEntity.status(HttpStatus.OK).build();
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }




}
