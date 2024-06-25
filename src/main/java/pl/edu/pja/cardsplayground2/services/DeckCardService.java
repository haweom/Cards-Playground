package pl.edu.pja.cardsplayground2.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.edu.pja.cardsplayground2.dtos.CardDTO;
import pl.edu.pja.cardsplayground2.entities.Card;
import pl.edu.pja.cardsplayground2.entities.Deck;
import pl.edu.pja.cardsplayground2.entities.DeckCard;
import pl.edu.pja.cardsplayground2.repositories.DeckCardRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class DeckCardService {

    private final DeckCardRepository deckCardRepository;

    @Autowired
    public DeckCardService(DeckCardRepository deckCardRepository) {
        this.deckCardRepository = deckCardRepository;
    }

    public void addCardToDeck(Deck deck, Card card) {
        DeckCard deckCard = deckCardRepository.findByDeckAndCard(deck, card);
        if (deckCard != null) {
            deckCard.setQuantity(deckCard.getQuantity() + 1);
        } else {
            deckCard = new DeckCard(deck, card, 1);
        }
        deckCardRepository.save(deckCard);
    }

    public void removeCardFromDeck(Deck deck, Card card) {
        DeckCard deckCard = deckCardRepository.findByDeckAndCard(deck, card);
        if (deckCard != null) {
            if (deckCard.getQuantity() > 1) {
                deckCard.setQuantity(deckCard.getQuantity() - 1);
                deckCardRepository.save(deckCard);
            } else {
                deckCardRepository.delete(deckCard);
            }
        }
    }

    public List<CardDTO> getCardsDTOByDeckId(Long deckId) {
        List<DeckCard> deckCards = deckCardRepository.findByDeckId(deckId);
        List<CardDTO> cardDTOs = new ArrayList<>();
        for (DeckCard deckCard : deckCards) {
            cardDTOs.add(copyDto(deckCard.getCard()));
        }
        return cardDTOs;
    }

    public List<DeckCard> getDeckCardsByDeckId(Long deckId) {
        return deckCardRepository.findByDeckId(deckId);
    }

    private CardDTO copyDto(Card card) {
        CardDTO dto = new CardDTO();
        dto.setId(card.getId());
        dto.setName(card.getName());
        dto.setCost(card.getCost());
        dto.setType(card.getType());
        dto.setAttack(card.getAttack());
        dto.setToughness(card.getToughness());
        dto.setText(card.getText());
        dto.setImage(card.getImage());
        return dto;
    }
}
