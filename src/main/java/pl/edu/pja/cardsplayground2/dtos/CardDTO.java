package pl.edu.pja.cardsplayground2.dtos;

public class CardDTO {
    private Long id;

    private String name;

    private int cost;

    private String type;

    private int attack;

    private int toughness;

    private String text;

    private String image;


    public CardDTO(String name, int cost, String type, int attack, int toughness, String text, String image) {
        this.name = name;
        this.cost = cost;
        this.type = type;
        this.attack = attack;
        this.toughness = toughness;
        this.text = text;
        this.image = image;
    }

    public CardDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getAttack() {
        return attack;
    }

    public void setAttack(int attack) {
        this.attack = attack;
    }

    public int getToughness() {
        return toughness;
    }

    public void setToughness(int toughness) {
        this.toughness = toughness;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
