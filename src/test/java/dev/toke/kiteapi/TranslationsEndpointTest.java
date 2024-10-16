package dev.toke.kiteapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.toke.kiteapi.controllers.TranslationController;
import dev.toke.kiteapi.dtos.TranslationWriteDto;
import dev.toke.kiteapi.models.*;
import dev.toke.kiteapi.services.TranslationService;
import dev.toke.kiteapi.services.WordEntryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.random.RandomGenerator;

import static dev.toke.kiteapi.controllers.TranslationController.TRANSLATIONS_PATH;
import static dev.toke.kiteapi.controllers.TranslationController.TRANSLATION_ID_PATH;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TranslationController.class)
public class TranslationsEndpointTest {
    @Autowired
    MockMvc mvc;

    @MockBean
    TranslationService translationService;

    @MockBean
    WordEntryService wordEntryService;

    WordEntry word;
    @Autowired
    private MockMvc mockMvc;

    ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        word = new WordEntry(1L,
                "abandon",
                DifficultyLevel.HARD,
                RandomGenerator.getDefault().nextLong(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                Collections.emptyList());
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
    }
    @Test
    void getTranslations() throws Exception {
        List<Translation> translations = List.of(
                new Translation(1L, "ပၥ်တီၢ်ကွံၥ်", Category.VERB, Subject.GENERAL, false,
                        LocalDateTime.now(),
                        LocalDateTime.now(),
                        RandomGenerator.getDefault().nextLong(1, 10),
                        word,
                        Collections.emptyList(),
                        Collections.emptyList()
                ),
                new Translation(2L, "တၢ်နါစိၤ", Category.VERB, Subject.GENERAL, false,
                        LocalDateTime.now(),
                        LocalDateTime.now(),
                        RandomGenerator.getDefault().nextLong(1, 10),
                        word,
                        Collections.emptyList(),
                        Collections.emptyList()
                ));

        given(translationService.findAll()).willReturn(translations);

        mockMvc.perform(get(TRANSLATIONS_PATH)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }

    @Test
    void newTranslation() throws Exception {
        TranslationWriteDto translationWriteDto = new TranslationWriteDto();
        translationWriteDto.setWordEntryId(10L);
        translationWriteDto.setKarenText("တၢ်တမံၤမံၤ");
        translationWriteDto.setSubject(Subject.GENERAL);
        translationWriteDto.setCategory(Category.NOUN);
        translationWriteDto.setUserId(1L);

        Translation translation= new Translation(13L, "တၢ်တမံၤမံၤ", Category.NOUN, Subject.GENERAL, false,
                LocalDateTime.now(),
                LocalDateTime.now(),
                RandomGenerator.getDefault().nextLong(1, 10),
                word,
                Collections.emptyList(),
                Collections.emptyList()
        );

        WordEntry word = new WordEntry(10L,
                "abandon",
                DifficultyLevel.HARD,
                RandomGenerator.getDefault().nextLong(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                Collections.emptyList());

        List<Translation> translations = new ArrayList<>();
        translations.add(translation);
        WordEntry word2 = new WordEntry(10L,
                "abandon",
                DifficultyLevel.HARD,
                RandomGenerator.getDefault().nextLong(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                translations
                );

        given(translationService.save(any(Translation.class))).willReturn(translation);
        given(wordEntryService.findById(any(Long.class))).willReturn(Optional.of(word));
        given(wordEntryService.save(any(WordEntry.class))).willReturn(word2);

        mockMvc.perform(post(TRANSLATIONS_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(translationWriteDto)));

        verify(translationService).save(any(Translation.class));
    }

    @Test
    void updateTranslation() throws Exception {
        Translation oldTranslation= new Translation(13L, "တၢ်တမံၤမံၤ", Category.NOUN, Subject.GENERAL, false,
                LocalDateTime.now(),
                LocalDateTime.now(),
                RandomGenerator.getDefault().nextLong(1, 10),
                word,
                Collections.emptyList(),
                Collections.emptyList()
        );

        Translation translation= new Translation(13L, "တၢ်တမံၤမံၤ", Category.NOUN, Subject.GENERAL, false,
                LocalDateTime.now(),
                LocalDateTime.now(),
                RandomGenerator.getDefault().nextLong(1, 10),
                word,
                Collections.emptyList(),
                Collections.emptyList()
        );

        TranslationWriteDto translationWriteDto = new TranslationWriteDto(0L, 13L, "တၢ်အီၣ်", Category.NOUN, Subject.GENERAL, 1L);

        given(translationService.findById(any(Long.class))).willReturn(Optional.of(oldTranslation));
        given(translationService.save(any(Translation.class))).willReturn(translation);

        mockMvc.perform(patch(TRANSLATION_ID_PATH, translation.getId())
                        .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(translationWriteDto))
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.karenText", is("တၢ်အီၣ်")))
                .andExpect(jsonPath("$.category", is("NOUN")));

        verify(translationService).save(any(Translation.class));


    }

    @Test
    void deleteTranslation() throws Exception {
        Translation translation= new Translation(13L, "တၢ်တမံၤမံၤ", Category.NOUN, Subject.GENERAL, false,
                LocalDateTime.now(),
                LocalDateTime.now(),
                RandomGenerator.getDefault().nextLong(1, 10),
                word,
                Collections.emptyList(),
                Collections.emptyList()
        );

        given(translationService.findById(any(Long.class))).willReturn(Optional.of(translation));

        mockMvc.perform(delete(TRANSLATION_ID_PATH, translation.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(translationService).delete(any(Translation.class));
    }
}
