package dev.toke.kiteapi.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WordEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String text;
    private DifficultyLevel difficultyLevel;
    private Long userId;
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "wordEntry", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<Translation> translations;
}
