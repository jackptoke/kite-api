package dev.toke.kiteapi.services;

import dev.toke.kiteapi.models.WordEntry;
import dev.toke.kiteapi.repos.WordEntryRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WordEntryService {
    private final WordEntryRepo wordEntryRepo;

    @Transactional
    public WordEntry save(WordEntry wordEntry) {
        return wordEntryRepo.save(wordEntry);
    }

    public List<WordEntry> findAll() {
        return wordEntryRepo.findAll();
    }

    public Optional<WordEntry> findById(Long id) {
        return wordEntryRepo.findById(id);
    }

    public Optional<WordEntry> findWordEntry(String word) {
        return wordEntryRepo.findWordEntry(word);
    }

    public List<WordEntry> findWords(String word) {
        return wordEntryRepo.findWordEntries(word);
    }
}
