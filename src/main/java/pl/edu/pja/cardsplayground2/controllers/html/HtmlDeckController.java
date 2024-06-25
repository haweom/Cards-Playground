package pl.edu.pja.cardsplayground2.controllers.html;

import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import pl.edu.pja.cardsplayground2.services.CardService;
import pl.edu.pja.cardsplayground2.services.DeckCardService;
import pl.edu.pja.cardsplayground2.services.DeckService;
import pl.edu.pja.cardsplayground2.services.UserService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import pl.edu.pja.cardsplayground2.dtos.*;
import pl.edu.pja.cardsplayground2.entities.Card;
import pl.edu.pja.cardsplayground2.entities.Deck;
import pl.edu.pja.cardsplayground2.entities.DeckCard;
import pl.edu.pja.cardsplayground2.entities.UserEntity;
import java.util.List;
import java.util.Objects;


@Controller
public class HtmlDeckController {
    private final CardService cardService;
    private final DeckService deckService;
    private final UserService userService;
    private final DeckCardService deckCardService;


    @Autowired
    public HtmlDeckController(CardService cardService, DeckService deckService, UserService userService, DeckCardService deckCardService) {
        this.cardService = cardService;
        this.deckService = deckService;
        this.userService = userService;
        this.deckCardService = deckCardService;
    }

    @Transactional
    @PostMapping("/html/decks/add")
    public String addCardToDeck(@RequestParam Long userId,
                                @RequestParam Long deckId,
                                @RequestParam Long cardId,
                                @AuthenticationPrincipal User userAuth){

        UserEntity authenticatedUser = userService.findByUsername(userAuth.getUsername());

        UserEntity user = userService.getUser(userId);
        boolean admin = authenticatedUser.getAdmin();
        if (Objects.equals(user.getId(), authenticatedUser.getId()) || admin){

            Deck deck = deckService.getUserDeckById(deckId,user);
            Card card = cardService.getCardById(cardId);

            deckCardService.addCardToDeck(deck, card);

            return "redirect:/deck-builder?userId=" + userId + "&deckId=" + deckId;
        }

        return "redirect:/logout";
    }

    @DeleteMapping("/html/decks/remove/{deckId}/{cardId}")
    public ResponseEntity<?> removeCardFromDeck(@RequestParam Long userId,
                                                @PathVariable Long deckId,
                                                @PathVariable Long cardId,
                                                @AuthenticationPrincipal User userAuth) {

        UserEntity authenticatedUser = userService.findByUsername(userAuth.getUsername());

        UserEntity user = userService.getUser(userId);
        boolean admin = authenticatedUser.getAdmin();
        if (Objects.equals(user.getId(), authenticatedUser.getId()) || admin) {

            Deck deck = deckService.getUserDeckById(deckId, user);
            Card card = cardService.getCardById(cardId);

            deckCardService.removeCardFromDeck(deck, card);

            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @PostMapping("/html/decks/analytics/{deckId}")
    public ResponseEntity<?> getDeckAnalytics(@PathVariable Long deckId, @Valid @RequestBody UserDTO userDTO,
                                              @AuthenticationPrincipal User userAuth) {
        UserEntity authenticatedUser = userService.findByUsername(userAuth.getUsername());

        UserEntity user = userService.getUser(userDTO.getId());

        boolean admin = authenticatedUser.getAdmin();
        if (Objects.equals(user.getId(), authenticatedUser.getId()) || admin) {
            Deck deck = deckService.getUserDeckById(deckId, user);

            if (deck != null) {
                List<CardDTO> cardDTOs = deckCardService.getCardsDTOByDeckId(deckId);
                if (!cardDTOs.isEmpty()) {
                    List<DeckCard> deckCards = deckCardService.getDeckCardsByDeckId(deckId);
                    AnalyticsDTO analyticsDTO = new AnalyticsDTO(deckCards);
                    return ResponseEntity.ok(analyticsDTO);
                }
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PatchMapping("/html/decks/save")
    public ResponseEntity<?> saveDeck(@Valid @RequestBody DeckLogInDTO deckLogInDTO,
                                      @AuthenticationPrincipal User userAuth) {
        try {

            UserEntity authenticatedUser = userService.findByUsername(userAuth.getUsername());

            UserDTO userDTO = deckLogInDTO.getUser();
            DeckDTO deckDTO = deckLogInDTO.getDeck();

            UserEntity user = userService.getUser(userDTO.getId());

            boolean admin = authenticatedUser.getAdmin();
            if (Objects.equals(user.getId(), authenticatedUser.getId()) || admin) {
                Deck deck = deckService.getUserDeckById(deckDTO.getId(), user);
                deck.setName(deckDTO.getName());


                deckService.saveDeck(deck, user);
                deckDTO.setId(deck.getId());
                return ResponseEntity.ok(deckDTO);
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.badRequest().body("Deck name must be unique and not empty");
        } catch (ConstraintViolationException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/html/decks/remove/{deckId}")
    public ResponseEntity<?> removeDeckById(@PathVariable Long deckId,
                                            @RequestParam Long userId,
                                            @AuthenticationPrincipal User userAuth){
        UserEntity authenticatedUser = userService.findByUsername(userAuth.getUsername());
        UserDTO userDTO = userService.getUserDto(userId);

        boolean admin = authenticatedUser.getAdmin();
        if(userDTO.getPassword().equals(authenticatedUser.getPassword()) || admin){

            UserEntity user = userService.getUser(userId);
            Deck deck = deckService.getUserDeckById(deckId, user);


            if(userService.checkOwnerDeck(deck, user)){
                deckService.removeDeck(deckId);
                return ResponseEntity.status(HttpStatus.OK).build();
            }
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

}
