package dev.toke.kiteapi.dtos;

import dev.toke.kiteapi.models.Category;
import dev.toke.kiteapi.models.Subject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TranslationWriteDto {
    private Long id;
    private Long wordEntryId;
    private String karenText;
    private Category category;
    private Subject subject;
    private Long userId;
}
