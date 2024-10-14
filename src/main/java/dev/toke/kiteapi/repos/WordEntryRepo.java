package dev.toke.kiteapi.repos;

import dev.toke.kiteapi.models.WordEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WordEntryRepo extends JpaRepository<WordEntry, Long> {
    @Query(value = "SELECT * FROM word_entry WHERE text = :word", nativeQuery = true)
    Optional<WordEntry> findWordEntry(String word);
    @Query(value = "SELECT * FROM word_entry WHERE text ILIKE '%:word%'", nativeQuery = true)
    List<WordEntry> findWordEntries(String word);
}
