package pl.edu.pja.cardsplayground2.dtos;

import pl.edu.pja.cardsplayground2.entities.DeckCard;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AnalyticsDTO {
    private Map<String, Long> manaCost;
    private Map<String, Long> toughness;
    private Map<String, Long> attack;

    public AnalyticsDTO(List<DeckCard> deckCards) {
        manaCost = deckCards.stream().collect(Collectors.groupingBy(deckCard -> String.valueOf(deckCard.getCard().getCost()), Collectors.summingLong(DeckCard::getQuantity)));
        toughness = deckCards.stream().collect(Collectors.groupingBy(deckCard -> String.valueOf(deckCard.getCard().getToughness()), Collectors.summingLong(DeckCard::getQuantity)));
        attack = deckCards.stream().collect(Collectors.groupingBy(deckCard -> String.valueOf(deckCard.getCard().getAttack()), Collectors.summingLong(DeckCard::getQuantity)));
    }

    public Map<String, Long> getManaCost() {
        return manaCost;
    }

    public void setManaCost(Map<String, Long> manaCost) {
        this.manaCost = manaCost;
    }

    public Map<String, Long> getToughness() {
        return toughness;
    }

    public void setToughness(Map<String, Long> toughness) {
        this.toughness = toughness;
    }

    public Map<String, Long> getAttack() {
        return attack;
    }

    public void setAttack(Map<String, Long> attack) {
        this.attack = attack;
    }
}
