package pl.edu.pja.cardsplayground2.services;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.edu.pja.cardsplayground2.dtos.CardDTO;
import pl.edu.pja.cardsplayground2.entities.Card;
import pl.edu.pja.cardsplayground2.entities.Deck;
import pl.edu.pja.cardsplayground2.entities.DeckCard;
import pl.edu.pja.cardsplayground2.entities.UserEntity;
import pl.edu.pja.cardsplayground2.repositories.CardRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CardService {

    private final CardRepository cardRepository;

    @Autowired
    public CardService(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    @Transactional
    public void saveCard(Card card, UserEntity user) {
        card.setUser(user);
        emptyFields(card);
        cardRepository.save(card);
    }

    public CardDTO getCardDTOById(Long id) {
        Optional<Card> original = cardRepository.getCardById(id);
        return original.map(this::copyDto).orElse(null);
    }

    public Card getCardById(Long id) {
        Optional<Card> card = cardRepository.getCardById(id);
        return card.orElse(null);
    }

    public boolean updateCard(Long id, Card updatedCard) {
        Optional<Card> original = cardRepository.getCardById(id);
        if(original.isPresent()){
            original.map(card -> {
                if(updatedCard.getName() != null){
                    card.setName(updatedCard.getName());
                }
                if(updatedCard.getCost() != original.get().getCost()){
                    card.setCost(updatedCard.getCost());
                }
                if(updatedCard.getType() != null){
                    card.setType(updatedCard.getType());
                }
                if(updatedCard.getAttack() != original.get().getAttack()){
                    card.setAttack(updatedCard.getAttack());
                }
                if(updatedCard.getToughness() != original.get().getToughness()){
                    card.setToughness(updatedCard.getToughness());
                }
                if(updatedCard.getText() != null){
                    card.setText(updatedCard.getText());
                }
                if(updatedCard.getImage() != null){
                    card.setImage(updatedCard.getImage());
                }
                emptyFields(card);
                cardRepository.save(card);
                return true;
            });
            return true;
        }
        return false;
    }

    public void emptyFields(Card card){
        if(card.getName().isBlank()){
            card.setName("\u200B");
        }
        if(card.getType().isBlank()){
            card.setType("\u200B");
        }
        if(card.getText().isBlank()){
            card.setText("\u200B");
        }
    }

    public List<CardDTO> getCardsByUser(UserEntity user) {
        List<Card> list = cardRepository.getAllByUserId(user.getId());
        List<CardDTO> listDTO = new ArrayList<>();
        for(Card c : list){
            CardDTO dto = copyDto(c);
            listDTO.add(dto);
        }
        return listDTO;
    }

    public CardDTO copyDto(Card card){
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

    public Card extractCard(CardDTO dto){
        Card card = new Card();
        card.setName(dto.getName());
        card.setCost(dto.getCost());
        card.setType(dto.getType());
        card.setAttack(dto.getAttack());
        card.setToughness(dto.getToughness());
        card.setText(dto.getText());
        card.setImage(dto.getImage());
        return card;
    }

    public Card getUserCardById(Long cardId, UserEntity user) {
        Optional<Card> card = cardRepository.findById(cardId);
        if(card.isPresent()){
            if(card.get().getUser() == user){
                return card.get();
            }
        }
        return null;
    }

    public List<CardDTO> getCardDTOsByDeck(Deck deck) {
        List<CardDTO> cardDTOList = new ArrayList<>();
        for (DeckCard deckCard : deck.getDeckCards()) {
            CardDTO dto = copyDto(deckCard.getCard());
            cardDTOList.add(dto);
        }
        return cardDTOList;
    }

    @Transactional
    public void removeCard(Long id) {
        Optional<Card> cardOpt = cardRepository.findById(id);
        if (cardOpt.isPresent()) {
            Card card = cardOpt.get();
            for (DeckCard deckCard : card.getDeckCards()) {
                deckCard.getDeck().removeCard(card);
            }
            if (card.getUser() != null) {
                card.getUser().removeCard(card);
            }
            cardRepository.delete(card);
        }
    }
}
