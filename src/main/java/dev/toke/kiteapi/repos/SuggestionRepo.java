package dev.toke.kiteapi.repos;

import dev.toke.kiteapi.models.Suggestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.Optional;

@Repository
public interface SuggestionRepo extends JpaRepository<Suggestion, Long> {
    @Query(value = "SELECT * FROM suggestion WHERE user_id = :userId AND translation_id = :translationId LIMIT 1", nativeQuery = true)
    Optional<Suggestion> findSuggestionByUserIdAndTranslationId(long userId, long translationId);
}
