package pl.edu.pja.cardsplayground2.dtos;

public class CardLogInDTO {
    private CardDTO card;
    private UserDTO user;

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public CardDTO getCard() {
        return card;
    }

    public void setCard(CardDTO card) {
        this.card = card;
    }
}
