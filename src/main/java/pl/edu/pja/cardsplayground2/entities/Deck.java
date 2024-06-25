package pl.edu.pja.cardsplayground2.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Deck {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 100)
    @Column(unique = true)
    private String name;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @OneToMany(mappedBy = "deck", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DeckCard> deckCards = new ArrayList<>();

    public Deck(String name) {
        this.name = name;
    }

    public Deck() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<DeckCard> getDeckCards() {
        return deckCards;
    }

    public void setDeckCards(List<DeckCard> deckCards) {
        this.deckCards = deckCards;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /*public void addCard(Card card) {
        for (DeckCard deckCard : deckCards) {
            if (deckCard.getCard().equals(card)) {
                deckCard.setQuantity(deckCard.getQuantity() + 1);
                return;
            }
        }
        deckCards.add(new DeckCard(this, card, 1));
    }*/

    public void removeCard(Card card) {
        for (DeckCard deckCard : deckCards) {
            if (deckCard.getCard().equals(card)) {
                if (deckCard.getQuantity() > 1) {
                    deckCard.setQuantity(deckCard.getQuantity() - 1);
                } else {
                    deckCards.remove(deckCard);
                }
                return;
            }
        }
    }
}
