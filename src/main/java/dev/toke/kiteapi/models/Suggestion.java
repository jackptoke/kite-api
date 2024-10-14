package dev.toke.kiteapi.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Suggestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String text; // suggested change
    private String comment;
    private ApprovalState state; // pending, accepted or rejected
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "translation_id", nullable = false)
    @JsonBackReference
    private Translation translation;
    private Long userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
