package dev.toke.kiteapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.toke.kiteapi.controllers.WordEntryController;
import dev.toke.kiteapi.models.*;
import dev.toke.kiteapi.services.ApprovalService;
import dev.toke.kiteapi.services.SuggestionService;
import dev.toke.kiteapi.services.TranslationService;
import dev.toke.kiteapi.services.WordEntryService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.random.RandomGenerator;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WordEntryController.class)
@Slf4j
public class WordsEndpointTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    ApprovalService approvalService;

    @MockBean
    SuggestionService suggestionService;

    @MockBean
    WordEntryService wordEntryService;

    @MockBean
    TranslationService translationService;

    private WordEntry word;
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
    void getWords() throws Exception{
        var words = List.of(
                new WordEntry(1L,
                    "abandon",
                    DifficultyLevel.EASY,
                    RandomGenerator.getDefault().nextLong(),
                    LocalDateTime.now(),
                    LocalDateTime.now(),
                    Collections.emptyList()),
                new WordEntry(2L,
                    "abandon",
                    DifficultyLevel.HARD,
                    RandomGenerator.getDefault().nextLong(),
                    LocalDateTime.now(),
                    LocalDateTime.now(),
                    Collections.emptyList()),
                new WordEntry(3L,
                    "abandon",
                    DifficultyLevel.ADVANCED,
                    RandomGenerator.getDefault().nextLong(),
                    LocalDateTime.now(),
                    LocalDateTime.now(),
                    Collections.emptyList()));

        given(wordEntryService.findAll()).willReturn(words);
        mockMvc.perform(get("/api/v1/words")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.length()", is(3)))
                .andExpect(jsonPath("$.data[0].id", is(words.getFirst().getId().intValue())))
                .andExpect(jsonPath("$.data[0].text", is(words.getFirst().getText())))
                .andExpect(jsonPath("$.data[0].difficultyLevel", is("EASY")));
    }

    @Test
    void getWordById() throws Exception{
        long id = 1L;
        word.setId(id);

        given(wordEntryService.findById(any(Long.class))).willReturn(Optional.of(word));

        mockMvc.perform(get("/api/v1/words/" + id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.id", is(word.getId().intValue())))
                .andExpect(jsonPath("$.data.text", is(word.getText())))
                .andExpect(jsonPath("$.data.difficultyLevel", is(DifficultyLevel.HARD.toString())));

    }

    @Test
    void addWord() throws Exception{

        word.setId(1L);

        given(wordEntryService.findWordEntry(any(String.class))).willReturn(Optional.empty());
        given(wordEntryService.save(any(WordEntry.class))).willReturn(word);

        WordEntry newWord = new WordEntry(null,
                "abandon",
                DifficultyLevel.HARD,
                2L,
                LocalDateTime.now(),
                LocalDateTime.now(),
                Collections.emptyList());

        String requestJsonString = objectMapper.writeValueAsString(newWord);
        mockMvc.perform(post("/api/v1/words")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestJsonString)
                )
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.statusCode", is(201)))
                .andExpect(jsonPath("$.message", is("success")))
                .andExpect(jsonPath("$.data.id", is(word.getId().intValue())))
                .andExpect(jsonPath("$.data.text", is(word.getText())))
                .andExpect(jsonPath("$.data.difficultyLevel", is("HARD")));
    }


}
