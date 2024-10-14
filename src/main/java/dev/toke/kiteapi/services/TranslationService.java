package dev.toke.kiteapi.services;

import dev.toke.kiteapi.models.Translation;
import dev.toke.kiteapi.repos.TranslationRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TranslationService {
    private final TranslationRepo translationRepo;

    public List<Translation> findAll() {
        return translationRepo.findAll();
    }


    @Transactional
    public Translation save(Translation translation) {
        return translationRepo.save(translation);
    }

    public Optional<Translation> findById(long id) {
        return translationRepo.findById(id);
    }

    @Transactional
    public void delete(Translation translation) {
        translationRepo.delete(translation);
    }
}
