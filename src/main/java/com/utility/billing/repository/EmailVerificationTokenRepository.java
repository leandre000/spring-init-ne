package com.utility.billing.repository;

import com.utility.billing.entity.EmailVerificationToken;
import com.utility.billing.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for EmailVerificationToken JPA operations.
 */
@Repository
public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Long> {

    /**
     * Resolves a verification token object by the token value string.
     *
     * @param token the token string
     * @return an Optional holding the token entity if found
     */
    Optional<EmailVerificationToken> findByToken(String token);

    /**
     * Resolves a token associated with a specific user.
     *
     * @param user the user entity
     * @return an Optional holding the token entity if found
     */
    Optional<EmailVerificationToken> findByUser(User user);

    /**
     * Deletes any existing verification token associated with a user.
     *
     * @param user the user entity
     */
    void deleteByUser(User user);
}
