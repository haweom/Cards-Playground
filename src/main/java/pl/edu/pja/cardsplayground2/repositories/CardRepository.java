package pl.edu.pja.cardsplayground2.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.edu.pja.cardsplayground2.entities.Card;
import java.util.List;
import java.util.Optional;

@Repository
public interface CardRepository extends CrudRepository<Card, Long> {

    Optional<Card> getCardById(Long id);

    List<Card> getAllByUserId(Long userId);

    @Query("SELECT c FROM Card c WHERE c.user.id = :userId AND c.cost >= :cost")
    List<Card> getAllByUserIdAndCostPlus(@Param("userId") Long userId, @Param("cost") int cost);
}
