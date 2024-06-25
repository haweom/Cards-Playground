package pl.edu.pja.cardsplayground2.controllers.html;

import jakarta.transaction.Transactional;
import pl.edu.pja.cardsplayground2.entities.Card;
import pl.edu.pja.cardsplayground2.services.CardService;
import pl.edu.pja.cardsplayground2.services.DeckCardService;
import pl.edu.pja.cardsplayground2.services.DeckService;
import pl.edu.pja.cardsplayground2.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.edu.pja.cardsplayground2.dtos.*;
import pl.edu.pja.cardsplayground2.entities.Deck;
import pl.edu.pja.cardsplayground2.entities.DeckCard;
import pl.edu.pja.cardsplayground2.entities.UserEntity;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Controller
public class HtmlController {
    private final CardService cardService;
    private final DeckService deckService;
    private final UserService userService;
    private final DeckCardService deckCardService;

    @Autowired
    public HtmlController(CardService cardService, DeckService deckService, UserService userService, DeckCardService deckCardService) {
        this.cardService = cardService;
        this.deckService = deckService;
        this.userService = userService;
        this.deckCardService = deckCardService;
    }

    @GetMapping("/")
    public String start() {
        return "redirect:/login";
    }

    @GetMapping("/collection")
    public String collectionPage(@RequestParam Long userId, @AuthenticationPrincipal User userAuth, Model model) {
        UserEntity authenticatedUser = userService.findByUsername(userAuth.getUsername());

        UserEntity user = userService.getUser(userId);
        boolean admin = authenticatedUser.getAdmin();
        if (Objects.equals(user.getId(), authenticatedUser.getId()) || admin) {
            List<CardDTO> cards = cardService.getCardsByUser(user);

            List<Integer> costs = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8);
            model.addAttribute("costs", costs);

            model.addAttribute("cards", cards);
            model.addAttribute("userId", userId);
            model.addAttribute("user", user);
            return "collection";
        }
        return "redirect:/logout";
    }

    @GetMapping("/collection/decks")
    public String collectionDecksPage(@RequestParam Long userId, @AuthenticationPrincipal User userAuth, Model model){
        UserEntity authenticatedUser = userService.findByUsername(userAuth.getUsername());

        UserEntity user = userService.getUser(userId);
        boolean admin = authenticatedUser.getAdmin();
        if (Objects.equals(user.getId(), authenticatedUser.getId()) || admin) {
            List<DeckDTO> decks = deckService.getDecksByUser(userId);
            model.addAttribute("decks", decks);
            model.addAttribute("userId", userId);
            return "deckCollection";
        }
        return "redirect:/logout";
    }

    @GetMapping("/deck-builder")
    public String deckBuilderPage(
            @RequestParam Long userId,
            @RequestParam(required = false) Long deckId,
            @AuthenticationPrincipal User userAuth,
            Model model) {
        UserEntity authenticatedUser = userService.findByUsername(userAuth.getUsername());

        UserEntity user = userService.getUser(userId);
        boolean admin = authenticatedUser.getAdmin();
        if (Objects.equals(user.getId(), authenticatedUser.getId()) || admin){
            List<CardDTO> cards = cardService.getCardsByUser(user);

            Deck deck;

            if(deckId == null){
                deck = new Deck();
                deck.setName("New Deck");
                deckService.saveDeck(deck, user);
                deckId = deck.getId();
            }else{
                deck = deckService.getUserDeckById(deckId,user);
                if(deck == null){
                    deck = new Deck();
                    deck.setName("New Deck");
                    deckService.saveDeck(deck, user);
                    deckId = deck.getId();
                }
            }

            List<DeckCard> deckCards = deckCardService.getDeckCardsByDeckId(deckId);

            List<Integer> costs = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8);
            model.addAttribute("costs", costs);

            model.addAttribute("userId", userId);
            model.addAttribute("user", user);
            model.addAttribute("cards", cards);
            model.addAttribute("deck", deck);
            model.addAttribute("deckId", deck.getId());
            model.addAttribute("deckCards", deckCards);
            return "deck-builder";
        }

        return "redirect:/logout";
    }

    @GetMapping("/card-builder")
    public String cardBuilderPage(@RequestParam Long userId, @AuthenticationPrincipal User userAuth, Model model) {
        UserEntity authenticatedUser = userService.findByUsername(userAuth.getUsername());

        UserEntity user = userService.getUser(userId);
        boolean admin = authenticatedUser.getAdmin();
        if (Objects.equals(user.getId(), authenticatedUser.getId()) || admin){
            model.addAttribute("userId", userId);
            model.addAttribute("user", user);
            model.addAttribute("card", new CardDTO());
            return "card-builder";
        }

        return "redirect:/logout";
    }

    private static final String UPLOAD_DIR = "src/main/resources/static/uploads/";

    @PostMapping("/upload")
    public ResponseEntity<?> handleFileUpload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty");
        }
        try {
            byte[] bytes = file.getBytes();
            Path path = Paths.get(UPLOAD_DIR);

            Path filePath = path.resolve(file.getOriginalFilename());
            Files.write(filePath, bytes);
            return ResponseEntity.ok().body("{\"filename\": \"" + file.getOriginalFilename() + "\"}");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Failed to upload file");
        }
    }

    @Transactional
    public void dummyUsers() {
        UserEntity admin = new UserEntity("Admin", "qwerty");
        admin.setAdmin(true);
        userService.register(admin);
        userService.register(new UserEntity("haweom","abc"));
        //
        CardDTO cardDTO = new CardDTO("Dummy",0,"Construct",1,1,"Artificial","dummy.png");

        Card card1 = cardService.extractCard(cardDTO);
        cardDTO.setName("Artificer");
        cardDTO.setCost(1);
        cardDTO.setAttack(2);
        cardDTO.setToughness(3);
        cardDTO.setText("Create Artifact");

        Card card2 = cardService.extractCard(cardDTO);
        cardDTO.setName("Rogue");
        cardDTO.setCost(2);
        cardDTO.setAttack(3);
        cardDTO.setToughness(1);
        cardDTO.setText("Quick Attack");

        Card card3 = cardService.extractCard(cardDTO);

        UserEntity user = userService.getUser(2L);

        cardService.saveCard(card1, user);
        userService.bindCard(card1, user);

        cardService.saveCard(card2, user);
        userService.bindCard(card2, user);

        cardService.saveCard(card3, user);
        userService.bindCard(card3, user);
        //

        //
        DeckDTO deckDTO = new DeckDTO("Deck Premade");

        Deck deck = deckService.extractDeck(deckDTO);

        deckService.saveDeck(deck,user);
        userService.bindDeck(deck,user);

        deckCardService.addCardToDeck(deck,card1);
        deckCardService.addCardToDeck(deck,card1);

        deckCardService.addCardToDeck(deck,card2);

        deckCardService.addCardToDeck(deck,card3);
        deckCardService.addCardToDeck(deck,card3);
        deckCardService.addCardToDeck(deck,card3);
    }
}
