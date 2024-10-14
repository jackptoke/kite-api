package dev.toke.kiteapi.controllers;

import dev.toke.kiteapi.dtos.KiteResponse;
import dev.toke.kiteapi.models.*;
import dev.toke.kiteapi.services.WordEntryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/words")
@RequiredArgsConstructor
public class WordEntryController {
    private final WordEntryService wordEntryService;

    @GetMapping
    public ResponseEntity<KiteResponse<List<WordEntry>>> findAll() {
        return ResponseEntity.ok(new KiteResponse<>(wordEntryService.findAll(), 200, "success"));
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<KiteResponse<WordEntry>> findWordById(@PathVariable("id") Long id) {
        log.debug("Retrieving word with id: {}", id);

        var result = wordEntryService.findById(id);
        KiteResponse<WordEntry> payload = result
                .map(wordEntry -> new KiteResponse<>(wordEntry, 200, "success"))
                .orElseGet(() -> new KiteResponse<>(null, 400, "Word not found"));
        return ResponseEntity.ok(payload);
    }

    @GetMapping("/search")
    public ResponseEntity<KiteResponse<List<WordEntry>>> findWords(@RequestParam("text") String text) {
        log.debug("Searching for words containing: {}", text);
        KiteResponse<List<WordEntry>> payload = new KiteResponse<>(wordEntryService.findWords(text), 200, "success");
        return ResponseEntity.ok(payload);
    }

    @PostMapping
    public ResponseEntity<KiteResponse<WordEntry>> save(@RequestBody WordEntry wordEntry) {
        log.debug("Saving word: {}", wordEntry);
        var existing = wordEntryService.findWordEntry(wordEntry.getText());
        if(existing.isPresent())
            return ResponseEntity.ok(new KiteResponse<>(null, 400, "Word already exists"));
        URI uri = URI.create("/api/v1/words/" + wordEntry.getId());
        return ResponseEntity.created(uri).body(new KiteResponse<>(wordEntryService.save(wordEntry), 201, "success"));
    }

    @PostMapping("/{id}")
    @Transactional
    public ResponseEntity<KiteResponse<WordEntry>> addTranslation(@PathVariable("id") Long id,
                                                                  @RequestBody Translation translation) {
        var result = wordEntryService.findById(id);
        if(result.isPresent()) {
            var word = result.get();
            translation.setWordEntry(word);
            word.getTranslations().add(translation);
            String url = "/api/v1/words/" + word.getId();
            URI uri = URI.create(url);
            return ResponseEntity.created(uri)
                    .body(new KiteResponse<>(wordEntryService.save(word), 201, "translation added"));
        }
        return ResponseEntity.ok(new KiteResponse<>(null, 404, "Word not found"));
    }

    @PostMapping("/{wordId}/translations/{translationId}/approve")
    @Transactional
    public ResponseEntity<KiteResponse<WordEntry>> approveTranslation(@PathVariable("wordId") Long wordId,
                                                                      @PathVariable("translationId") Long translationId,
                                                                      @RequestBody Approval approval) {
        var wordEntryResult = wordEntryService.findById(wordId);
        if(wordEntryResult.isPresent()) {
            var word = wordEntryResult.get();
            var translationResult = word
                    .getTranslations().stream()
                    .filter(t -> t.getId().longValue() == translationId.longValue())
                    .findFirst();
            if(translationResult.isPresent()) {
                var translation = translationResult.get();
                approval.setTranslation(translation);
                translation.getApprovals().add(approval);
                wordEntryService.save(word);
                String url = "/api/v1/words/" + word.getId();
                URI uri = URI.create(url);
                return ResponseEntity.created(uri)
                        .body(new KiteResponse<>(wordEntryService.save(word), 201, "approval saved"));
            }
            return ResponseEntity.ok(new KiteResponse<>(null, 404, "Translation not found"));
        }
        return ResponseEntity.ok(new KiteResponse<>(null, 404, "Word not found"));
    }

    @PostMapping("/{wordId}/translations/{translationId}/suggest")
    @Transactional
    public ResponseEntity<KiteResponse<WordEntry>> suggestTranslationImprovement(@PathVariable("wordId") Long wordId,
                                                                                 @PathVariable("translationId") Long translationId,
                                                                                 @RequestBody Suggestion suggestion) {
        var wordEntryResult = wordEntryService.findById(wordId);
        if(wordEntryResult.isPresent()) {
            var word = wordEntryResult.get();
            var translationResult = word
                    .getTranslations().stream()
                    .filter(t -> t.getId().longValue() == translationId.longValue())
                    .findFirst();
            if(translationResult.isPresent()) {
                var translation = translationResult.get();
                var improvementResult = translation.getSuggestions()
                        .stream()
                        .filter(s -> s.getId().longValue() == suggestion.getId().longValue())
                        .findFirst();
                if(improvementResult.isPresent()) {
                    var improvement = improvementResult.get();
                    improvement.setText(suggestion.getText());
                    improvement.setComment(suggestion.getComment());
                    improvement.setState(suggestion.getState());
                    improvement.setUpdatedAt(LocalDateTime.now());
                    return ResponseEntity.ok(new KiteResponse<>(wordEntryService.save(word), 200, "suggestion updated"));
                }
                else {
                    suggestion.setTranslation(translation);
                    translation.getSuggestions().add(suggestion);
                    String url = "/api/v1/words/" + word.getId();
                    URI uri = URI.create(url);
                    return ResponseEntity.created(uri)
                            .body(new KiteResponse<>(wordEntryService.save(word), 201, "suggestion saved"));
                }
            }
            else{
                return ResponseEntity.ok(new KiteResponse<>(null, 404, "Translation not found"));
            }
        }
        return ResponseEntity.ok(new KiteResponse<>(null, 404, "Word not found"));
    }
}
