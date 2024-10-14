package dev.toke.kiteapi.controllers;

import dev.toke.kiteapi.dtos.KiteResponse;
import dev.toke.kiteapi.dtos.TranslationWriteDto;
import dev.toke.kiteapi.models.Translation;
import dev.toke.kiteapi.services.TranslationService;
import dev.toke.kiteapi.services.WordEntryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/translations")
@RequiredArgsConstructor
public class TranslationController {
    private final TranslationService translationService;
    private final WordEntryService wordEntryService;

    @GetMapping
    public ResponseEntity<KiteResponse<List<Translation>>> findAllTranslations() {
        var result = translationService.findAll();
        return ResponseEntity.ok(new KiteResponse<>(result, 200, "Success"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<KiteResponse<Translation>> findTranslationById(@PathVariable("id") Long id) {
        var result = translationService.findById(id);
        return result.map(translation -> ResponseEntity.ok(new KiteResponse<>(translation, 200, "Success")))
                .orElseGet(() -> ResponseEntity.ok(new KiteResponse<>(null, 404, "Translation not found")));
    }

    @PostMapping
    public ResponseEntity<KiteResponse<Translation>> newTranslation(@RequestBody TranslationWriteDto newTranslation) {
        var wordResult = wordEntryService.findById(newTranslation.getWordEntryId());
        if(wordResult.isPresent()) {
            var word = wordResult.get();
            var translation = new Translation();
            translation.setKarenText(newTranslation.getKarenText());
            translation.setSubject(newTranslation.getSubject());
            translation.setCategory(newTranslation.getCategory());
            translation.setCreatedAt(LocalDateTime.now());
            translation.setUpdatedAt(LocalDateTime.now());
            translation.setUserId(newTranslation.getUserId());
            translation.setApproved(false);
            translation.setWordEntry(word);
            translationService.save(translation);

            String url = "/api/v1/translations/" + translation.getId();
            URI uri = URI.create(url);
            return ResponseEntity.created(uri)
                    .body(new KiteResponse<>(translation, 201, "translation added"));
        }
        return ResponseEntity.ok(new KiteResponse<>(null, 404, "Word not found"));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<KiteResponse<Translation>> updateTranslation(@PathVariable("id") Long id,
                                                                       @RequestBody TranslationWriteDto updatedTranslation) {
        var result = translationService.findById(id);

        if(result.isPresent()) {
            var translation = result.get();
            if(!updatedTranslation.getKarenText().isBlank() && updatedTranslation.getKarenText() != translation.getKarenText())
                translation.setKarenText(updatedTranslation.getKarenText());
            if(updatedTranslation.getSubject() != null && updatedTranslation.getSubject() == translation.getSubject())
                translation.setSubject(updatedTranslation.getSubject());
            if(updatedTranslation.getCategory() != null && updatedTranslation.getCategory() == translation.getCategory())
                translation.setCategory(updatedTranslation.getCategory());
            translation.setUpdatedAt(LocalDateTime.now());
            translationService.save(translation);
            return ResponseEntity.ok(new KiteResponse<>(translation, 200, "translation updated"));
        }
        return ResponseEntity.ok(new KiteResponse<>(null, 404, "Translation not found"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteTranslation(@PathVariable("id") Long id) {
        var result = translationService.findById(id);
        if(result.isPresent()) {
            var translation = result.get();
            translationService.delete(translation);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

}
