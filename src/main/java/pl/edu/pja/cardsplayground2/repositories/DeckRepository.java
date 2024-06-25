package pl.edu.pja.cardsplayground2.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.edu.pja.cardsplayground2.entities.Deck;
import java.util.List;
import java.util.Optional;

@Repository
public interface DeckRepository extends CrudRepository<Deck,Long> {
    List<Deck> getAllByUserId(Long userId);

   /* Optional<Deck> getDeckById(Long deckId);*/
}
