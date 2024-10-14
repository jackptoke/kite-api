package dev.toke.kiteapi.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Translation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String karenText;
    private Category category;
    private Subject subject;
    private Boolean approved;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "word_entry_id", nullable = false)
    @JsonBackReference
    private WordEntry wordEntry;

    @OneToMany(mappedBy = "translation",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    @JsonManagedReference
    private List<Approval> approvals;

    @OneToMany(mappedBy = "translation",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    @JsonManagedReference
    private List<Suggestion> suggestions;
}
