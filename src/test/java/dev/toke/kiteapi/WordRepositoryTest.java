package dev.toke.kiteapi;

import dev.toke.kiteapi.repos.WordEntryRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SpringBootTest
public class WordRepositoryTest {
    @Autowired
    WordEntryRepo wordEntryRepo;

    @BeforeEach
    void setUp() {

    }

    @Test
    void getWords_returns_words() {
        var words = wordEntryRepo.findAll();
        assertNotEquals(0, words.size());
    }
}
