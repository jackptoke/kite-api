package dev.toke.kiteapi.services;

import dev.toke.kiteapi.models.Approval;
import dev.toke.kiteapi.repos.ApprovalRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ApprovalService {
    private final ApprovalRepo approvalRepo;

    @Transactional
    public Approval save(Approval approval) {
        var existing = approvalRepo.findApprovalByUserIdAndTranslationId(approval.getUserId(), approval.getTranslation().getId());
        if (existing.isPresent()) {
            var existingApproval = existing.get();
            if(existingApproval.getState() != approval.getState()) {
                existingApproval.setState(approval.getState());
                existingApproval.setApprovedAt(LocalDateTime.now());
                return approvalRepo.save(existingApproval);
            }
            return existingApproval;
        }else return approvalRepo.save(approval);
    }

    public Optional<Approval> findApproval(long userId, long translationId) {
        return approvalRepo.findApprovalByUserIdAndTranslationId(userId, translationId);
    }

    public List<Approval> findApprovals(long translationId) {
        return approvalRepo.findApprovalsByTranslationId(translationId);
    }
}
