package pl.edu.pja.cardsplayground2.services;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.edu.pja.cardsplayground2.dtos.DeckDTO;
import pl.edu.pja.cardsplayground2.entities.Card;
import pl.edu.pja.cardsplayground2.entities.Deck;
import pl.edu.pja.cardsplayground2.entities.DeckCard;
import pl.edu.pja.cardsplayground2.entities.UserEntity;
import pl.edu.pja.cardsplayground2.repositories.DeckRepository;
import pl.edu.pja.cardsplayground2.repositories.DeckCardRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DeckService {

    private final DeckRepository deckRepository;
    private final DeckCardRepository deckCardRepository;

    @Autowired
    public DeckService(DeckRepository deckRepository, DeckCardRepository deckCardRepository) {
        this.deckRepository = deckRepository;
        this.deckCardRepository = deckCardRepository;
    }

    public Deck extractDeck(DeckDTO deckDTO) {
        Deck deck = new Deck();
        deck.setName(deckDTO.getName());
        return deck;
    }

    public DeckDTO copyDto(Deck deck) {
        DeckDTO deckDTO = new DeckDTO();
        deckDTO.setId(deck.getId());
        deckDTO.setName(deck.getName());
        return deckDTO;
    }

    public void saveDeck(Deck deck, UserEntity user) {
        deck.setUser(user);
        deckRepository.save(deck);
    }

    public Deck getUserDeckById(Long deckId, UserEntity user) {
        Optional<Deck> deck = deckRepository.findById(deckId);
        if (deck.isPresent()) {
            if (deck.get().getUser().equals(user)) {
                return deck.get();
            }
        }
        return null;
    }

    @Transactional
    public void removeDeck(Long deckId) {
        Optional<Deck> deckOpt = deckRepository.findById(deckId);
        if (deckOpt.isPresent()) {
            Deck deck = deckOpt.get();
            for (DeckCard deckCard : deck.getDeckCards()) {
                deckCardRepository.delete(deckCard);
            }
            if (deck.getUser() != null) {
                deck.getUser().removeDeck(deck);
            }
            deckRepository.delete(deck);
        }
    }

    public boolean checkOwner(Deck deck, Card card) {
        DeckCard deckCard = deckCardRepository.findByDeckAndCard(deck, card);
        return deckCard != null;
    }

    public List<DeckDTO> getDecksByUser(Long userId) {
        List<Deck> list = deckRepository.getAllByUserId(userId);
        List<DeckDTO> listDTO = new ArrayList<>();
        for (Deck d : list) {
            DeckDTO dto = copyDto(d);
            listDTO.add(dto);
        }
        return listDTO;
    }

}
