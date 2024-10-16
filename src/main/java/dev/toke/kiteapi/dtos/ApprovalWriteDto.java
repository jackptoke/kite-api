package dev.toke.kiteapi.dtos;

import dev.toke.kiteapi.models.ApprovalState;
import lombok.Data;

@Data
public class ApprovalWriteDto {
    private Long id;
    private Long userId;
    private Long translationId;
    private ApprovalState state;
}
