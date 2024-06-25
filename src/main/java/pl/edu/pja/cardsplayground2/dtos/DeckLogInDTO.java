package pl.edu.pja.cardsplayground2.dtos;

public class DeckLogInDTO {
    private DeckDTO deck;
    private UserDTO user;

    public DeckDTO getDeck() {
        return deck;
    }

    public void setDeck(DeckDTO deck) {
        this.deck = deck;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }
}
