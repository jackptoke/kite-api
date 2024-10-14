package dev.toke.kiteapi.services;

import dev.toke.kiteapi.models.Suggestion;
import dev.toke.kiteapi.repos.SuggestionRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SuggestionService {
    private final SuggestionRepo suggestionRepo;

    @Transactional
    public Suggestion save(Suggestion suggestion) {
        var result = suggestionRepo.findSuggestionByUserIdAndTranslationId(suggestion.getUserId(), suggestion.getTranslation().getId());
        if(result.isPresent()) {
            var currentSuggestion = result.get();
            currentSuggestion.setText(suggestion.getText());
            currentSuggestion.setComment(suggestion.getComment());
            currentSuggestion.setUpdatedAt(LocalDateTime.now());
            return suggestionRepo.save(currentSuggestion);
        }
        else {
            suggestion.setCreatedAt(LocalDateTime.now());
            suggestion.setUpdatedAt(LocalDateTime.now());
            return suggestionRepo.save(suggestion);
        }
    }
}
