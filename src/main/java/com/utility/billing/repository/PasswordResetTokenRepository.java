package com.utility.billing.repository;

import com.utility.billing.entity.PasswordResetToken;
import com.utility.billing.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for PasswordResetToken JPA operations.
 */
@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    /**
     * Resolves a password reset token object by the token value string.
     *
     * @param token the token string
     * @return an Optional holding the token entity if found
     */
    Optional<PasswordResetToken> findByToken(String token);

    /**
     * Resolves a token associated with a specific user.
     *
     * @param user the user entity
     * @return an Optional holding the token entity if found
     */
    Optional<PasswordResetToken> findByUser(User user);

    /**
     * Deletes any existing password reset token associated with a user.
     *
     * @param user the user entity
     */
    void deleteByUser(User user);
}
