package pl.edu.pja.cardsplayground2.dtos;
import java.util.List;


public class DeckDTO {

    private Long id;

    private String name;

    private List<CardDTO> cardList;

    public DeckDTO(String name) {
        this.name = name;
    }

    public DeckDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<CardDTO> getCardList() {
        return cardList;
    }

    public void setCardDTOList(List<CardDTO> cardList) {
        this.cardList = cardList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
