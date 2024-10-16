package dev.toke.kiteapi.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
