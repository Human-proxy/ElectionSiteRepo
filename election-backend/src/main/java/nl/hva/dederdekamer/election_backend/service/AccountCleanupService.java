package nl.hva.dederdekamer.election_backend.service;

import nl.hva.dederdekamer.election_backend.entities.UserEntity;
import nl.hva.dederdekamer.election_backend.repository.UserRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for cleaning up soft-deleted accounts after 30 days
 */
@Service
public class AccountCleanupService {

    private final UserRepository userRepository;

    public AccountCleanupService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Runs weekly on Sunday at 3 AM to permanently delete accounts that have been soft-deleted for more than 30 days
     */
    @Scheduled(cron = "0 0 3 * * SUN")
    @Transactional
    public void cleanupExpiredAccounts() {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        
        List<UserEntity> expiredAccounts = userRepository.findByDeletedAtBefore(thirtyDaysAgo);
        
        if (!expiredAccounts.isEmpty()) {
            System.out.println("Found " + expiredAccounts.size() + " expired accounts to delete");
            
            for (UserEntity user : expiredAccounts) {
                System.out.println("Permanently deleting account: " + user.getUsername() + " (deleted at: " + user.getDeletedAt() + ")");
                userRepository.delete(user);
            }
            
            System.out.println("Cleanup completed: " + expiredAccounts.size() + " accounts permanently deleted");
        }
    }
}
