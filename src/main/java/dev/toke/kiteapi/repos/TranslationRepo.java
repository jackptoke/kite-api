package dev.toke.kiteapi.repos;

import dev.toke.kiteapi.models.Translation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TranslationRepo extends JpaRepository<Translation, Long> {
}
