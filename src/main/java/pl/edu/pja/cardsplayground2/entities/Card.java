package pl.edu.pja.cardsplayground2.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is mandatory")
    @Size(max = 100)
    @Column(unique = true)
    private String name;

    @PositiveOrZero
    private int cost;

    @NotBlank
    private String type;

    @PositiveOrZero
    private int attack;

    @PositiveOrZero
    private int toughness;

    @NotBlank
    private String text;

    private String image;


    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @OneToMany(mappedBy = "card", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DeckCard> deckCards = new ArrayList<>();

    public Card(String name, int cost, String type, int attack, int toughness, String text, String image) {
        this.name = name;
        this.cost = cost;
        this.type = type;
        this.attack = attack;
        this.toughness = toughness;
        this.text = text;
        this.image = image;
    }

    public Card() {}

    public Long getId() {
        return id;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getToughness() {
        return toughness;
    }

    public void setToughness(int toughness) {
        this.toughness = toughness;
    }

    public int getAttack() {
        return attack;
    }

    public void setAttack(int attack) {
        this.attack = attack;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
