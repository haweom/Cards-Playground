package pl.edu.pja.cardsplayground2.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.edu.pja.cardsplayground2.entities.DeckCard;
import pl.edu.pja.cardsplayground2.entities.Deck;
import pl.edu.pja.cardsplayground2.entities.Card;

import java.util.List;

@Repository
public interface DeckCardRepository extends JpaRepository<DeckCard, Long> {

    List<DeckCard> findByDeckId(Long deckId);

    DeckCard findByDeckAndCard(Deck deck, Card card);
}
