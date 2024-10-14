package dev.toke.kiteapi.repos;

import dev.toke.kiteapi.models.Approval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApprovalRepo extends JpaRepository<Approval, Long> {
    @Query(value = "SELECT * FROM approval WHERE user_id = :userId AND translation_id = :translationId LIMIT 1", nativeQuery = true)
    Optional<Approval> findApprovalByUserIdAndTranslationId(long userId, long translationId);
    @Query(value = "SELECT * FROM approval WHERE translation_id = :translationId", nativeQuery = true)
    List<Approval> findApprovalsByTranslationId(long translationId);
}
