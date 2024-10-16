package dev.toke.kiteapi.controllers;

import dev.toke.kiteapi.dtos.ApprovalWriteDto;
import dev.toke.kiteapi.models.Approval;
import dev.toke.kiteapi.services.ApprovalService;
import dev.toke.kiteapi.services.TranslationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ApprovalController {
    private static final String APPROVALS_PATH = "/api/v1/approvals";
    private static final String APPROVALS_ID_PATH = APPROVALS_PATH + "/{id}";

    private final ApprovalService approvalService;
    private final TranslationService translationService;

    @GetMapping(APPROVALS_PATH)
    public ResponseEntity<List<Approval>> allApprovals() {
        var approvals = approvalService.findAllApprovals();
        return ResponseEntity.ok(approvals);
    }

    @GetMapping(APPROVALS_ID_PATH)
    public ResponseEntity<Approval> findApproval(@PathVariable("id") Long id) {
        var result = approvalService.findById(id);
        return result.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping(APPROVALS_PATH + "/user/{userId}/translation/{translationId}")
    public ResponseEntity<Approval> findApproval(@PathVariable Long userId, @PathVariable Long translationId) {
        var result = approvalService.findApprovalByUserIdAndTranslationId(userId, translationId);
        return result
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping(APPROVALS_PATH + "/translation/{translationId}")
    public ResponseEntity<List<Approval>> findApprovalsByTranslationId(@PathVariable Long translationId) {
        var result = approvalService.findApprovals(translationId);
        return ResponseEntity.ok(result);
    }

    @PostMapping(APPROVALS_PATH)
    public ResponseEntity<Approval> addApproval(@RequestBody ApprovalWriteDto approval) {
        // TODO - will have to implement proper user validation after authentication and authorisation has been added
        var result = translationService.findById(approval.getTranslationId());
        if(result.isPresent()) {
            var translation = result.get();

            Approval newApproval = new Approval(
                    0L,
                    approval.getUserId(),
                    approval.getState(),
                    LocalDateTime.now(),
                    translation);

            newApproval = approvalService.save(newApproval);
            return ResponseEntity.ok(newApproval);
        }
        return ResponseEntity.notFound().build();
    }

    @PatchMapping(APPROVALS_PATH)
    public ResponseEntity<Approval> updateApproval(@RequestBody ApprovalWriteDto approval) {
        var existing = approvalService.findApprovalByUserIdAndTranslationId(approval.getUserId(), approval.getTranslationId());
        if(existing.isPresent()) {
            var existingApproval = existing.get();
            if(existingApproval.getUserId().longValue() == approval.getUserId().longValue()) {
                existingApproval.setState(approval.getState());
                existingApproval.setApprovedAt(LocalDateTime.now());
                existingApproval = approvalService.save(existingApproval);
                return ResponseEntity.ok(existingApproval);
            }
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping(APPROVALS_ID_PATH)
    public ResponseEntity<Object> deleteApproval(@PathVariable("id") Long id) {
        var result = approvalService.findById(id);
        if(result.isPresent()) {
            var approval = result.get();
            approvalService.delete(approval);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
