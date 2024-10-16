package dev.toke.kiteapi.controllers;

import dev.toke.kiteapi.models.*;
import dev.toke.kiteapi.services.TranslationService;
import dev.toke.kiteapi.services.WordEntryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class WordEntryController {
    public static final String WORDS_PATH = "/api/v1/words";
    public static final String WORDS_ID_PATH = WORDS_PATH + "/{id}";
    private final WordEntryService wordEntryService;
    private final TranslationService translationService;

    @GetMapping(WORDS_PATH)
    public ResponseEntity<List<WordEntry>> findAll() {
        return ResponseEntity.ok(wordEntryService.findAll());
    }

    @GetMapping(value = WORDS_ID_PATH)
    public ResponseEntity<WordEntry> findWordById(@PathVariable("id") Long id) {
        log.debug("Retrieving word with id: {}", id);

        var result = wordEntryService.findById(id);

        return result.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping(WORDS_PATH + "/search")
    public ResponseEntity<List<WordEntry>> findWords(@RequestParam("text") String text) {
        log.debug("Searching for words containing: {}", text);

        return ResponseEntity.ok(wordEntryService.findWords(text));
    }

    @PostMapping(WORDS_PATH)
    public ResponseEntity<WordEntry> save(@RequestBody WordEntry wordEntry) {
        log.debug("Saving word: {}", wordEntry);
        var existing = wordEntryService.findWordEntry(wordEntry.getText());
        if(existing.isPresent())
            return ResponseEntity.notFound().build();
        var newWordEntry = wordEntryService.save(wordEntry);
        URI uri = URI.create("/api/v1/words/" + newWordEntry.getId());
        return ResponseEntity.created(uri).body(newWordEntry);
    }

    @PatchMapping(WORDS_ID_PATH)
    public ResponseEntity<WordEntry> updateWord(@PathVariable("id") Long id, @RequestBody WordEntry wordEntry) {
        log.debug("Updating word with id: {}", id);
        var existing = wordEntryService.findById(id);
        if(existing.isPresent()) {
            var existingWordEntry = existing.get();
            existingWordEntry.setText(wordEntry.getText());
            existingWordEntry.setUpdatedAt(LocalDateTime.now());
            existingWordEntry.setDifficultyLevel(wordEntry.getDifficultyLevel());
            return ResponseEntity.ok(wordEntryService.save(existingWordEntry));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping(WORDS_ID_PATH)
    public ResponseEntity<Object> deleteWord(@PathVariable("id") Long id) {
        log.debug("Deleting word with id: {}", id);
        var existing = wordEntryService.findById(id);
        if(existing.isPresent()) {
            var existingWordEntry = existing.get();
            wordEntryService.delete(existingWordEntry);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

//
//    @PostMapping("/{id}")
//    @Transactional
//    public ResponseEntity<KiteResponse<WordEntry>> addTranslation(@PathVariable("id") Long id,
//                                                                  @RequestBody Translation translation) {
//        var result = wordEntryService.findById(id);
//        if(result.isPresent()) {
//            var word = result.get();
//            translation.setWordEntry(word);
//            translation = translationService.save(translation);
//            //            word.getTranslations().add(translation);
//            String url = "/api/v1/words/" + word.getId();
//            URI uri = URI.create(url);
//            return ResponseEntity.created(uri)
//                    .body(new KiteResponse<>(wordEntryService.save(word), 201, "translation added"));
//        }
//        return ResponseEntity.ok(new KiteResponse<>(null, 404, "Word not found"));
//    }
//
//    @PostMapping("/{wordId}/translations/{translationId}/approve")
//    @Transactional
//    public ResponseEntity<KiteResponse<WordEntry>> approveTranslation(@PathVariable("wordId") Long wordId,
//                                                                      @PathVariable("translationId") Long translationId,
//                                                                      @RequestBody Approval approval) {
//        var wordEntryResult = wordEntryService.findById(wordId);
//        if(wordEntryResult.isPresent()) {
//            var word = wordEntryResult.get();
//            var translationResult = word
//                    .getTranslations().stream()
//                    .filter(t -> t.getId().longValue() == translationId.longValue())
//                    .findFirst();
//            if(translationResult.isPresent()) {
//                var translation = translationResult.get();
//                approval.setTranslation(translation);
//                translation.getApprovals().add(approval);
//                wordEntryService.save(word);
//                String url = "/api/v1/words/" + word.getId();
//                URI uri = URI.create(url);
//                return ResponseEntity.created(uri)
//                        .body(new KiteResponse<>(wordEntryService.save(word), 201, "approval saved"));
//            }
//            return ResponseEntity.ok(new KiteResponse<>(null, 404, "Translation not found"));
//        }
//        return ResponseEntity.ok(new KiteResponse<>(null, 404, "Word not found"));
//    }
//
//    @PostMapping("/{wordId}/translations/{translationId}/suggest")
//    @Transactional
//    public ResponseEntity<KiteResponse<WordEntry>> suggestTranslationImprovement(@PathVariable("wordId") Long wordId,
//                                                                                 @PathVariable("translationId") Long translationId,
//                                                                                 @RequestBody Suggestion suggestion) {
//        var wordEntryResult = wordEntryService.findById(wordId);
//        if(wordEntryResult.isPresent()) {
//            var word = wordEntryResult.get();
//            var translationResult = word
//                    .getTranslations().stream()
//                    .filter(t -> t.getId().longValue() == translationId.longValue())
//                    .findFirst();
//            if(translationResult.isPresent()) {
//                var translation = translationResult.get();
//                var improvementResult = translation.getSuggestions()
//                        .stream()
//                        .filter(s -> s.getId().longValue() == suggestion.getId().longValue())
//                        .findFirst();
//                if(improvementResult.isPresent()) {
//                    var improvement = improvementResult.get();
//                    improvement.setText(suggestion.getText());
//                    improvement.setComment(suggestion.getComment());
//                    improvement.setState(suggestion.getState());
//                    improvement.setUpdatedAt(LocalDateTime.now());
//                    return ResponseEntity.ok(new KiteResponse<>(wordEntryService.save(word), 200, "suggestion updated"));
//                }
//                else {
//                    suggestion.setTranslation(translation);
//                    translation.getSuggestions().add(suggestion);
//                    String url = "/api/v1/words/" + word.getId();
//                    URI uri = URI.create(url);
//                    return ResponseEntity.created(uri)
//                            .body(new KiteResponse<>(wordEntryService.save(word), 201, "suggestion saved"));
//                }
//            }
//            else{
//                return ResponseEntity.ok(new KiteResponse<>(null, 404, "Translation not found"));
//            }
//        }
//        return ResponseEntity.ok(new KiteResponse<>(null, 404, "Word not found"));
//    }
}
