package com.vrmart.service;

import com.vrmart.dao.OtpDAO;
import org.mindrot.jbcrypt.BCrypt;

import java.security.SecureRandom;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Objects;

/** Generates and verifies secure one-time passwords for VR Mart. */
public final class OtpService {
    /** OTP length. */
    private static final int OTP_LENGTH = 6;
    /** OTP upper bound. */
    private static final int OTP_UPPER_BOUND = 1_000_000;
    /** Minimum OTP numeric value. */
    private static final int OTP_MINIMUM = 100_000;
    /** OTP validity in minutes. */
    private static final int OTP_VALIDITY_MINUTES = 10;
    /** Maximum failed attempts. */
    private static final int MAX_ATTEMPTS = 5;
    /** BCrypt work factor. */
    private static final int BCRYPT_ROUNDS = 12;
    /** Secure random generator. */
    private final SecureRandom secureRandom = new SecureRandom();
    /** OTP data access object. */
    private final OtpDAO otpDAO;

    /**
     * Creates the OTP service.
     *
     * @param dao OTP data access object
     */
    public OtpService(final OtpDAO dao) {
        otpDAO = Objects.requireNonNull(
                dao, "OTP DAO cannot be null");
    }

    /**
     * Generates and stores a new OTP.
     *
     * @param userId user identifier
     * @param purpose challenge purpose
     * @return six-digit OTP
     * @throws SQLException when database access fails
     */
    public String createOtp(final long userId, final String purpose)
            throws SQLException {
        final int value = secureRandom.nextInt(
                OTP_UPPER_BOUND - OTP_MINIMUM) + OTP_MINIMUM;
        final String otp = String.format(
                "%0" + OTP_LENGTH + "d", value);
        final String hash = BCrypt.hashpw(
                otp, BCrypt.gensalt(BCRYPT_ROUNDS));
        otpDAO.createChallenge(
                userId,
                purpose,
                hash,
                LocalDateTime.now().plusMinutes(
                        OTP_VALIDITY_MINUTES));
        return otp;
    }

    /**
     * Verifies and consumes the current OTP.
     *
     * @param userId user identifier
     * @param purpose challenge purpose
     * @param otp supplied OTP
     * @return true when the OTP is valid
     * @throws SQLException when database access fails
     */
    public boolean verifyOtp(final long userId, final String purpose,
                             final String otp) throws SQLException {
        if (otp == null
                || !otp.matches("\\d{" + OTP_LENGTH + "}")) {
            return false;
        }
        final OtpDAO.OtpChallenge challenge =
                otpDAO.findActiveChallenge(userId, purpose);
        if (challenge == null
                || challenge.attempts() >= MAX_ATTEMPTS
                || LocalDateTime.now().isAfter(
                        challenge.expiresAt())) {
            return false;
        }
        if (!BCrypt.checkpw(otp, challenge.otpHash())) {
            otpDAO.incrementAttempts(challenge.id());
            return false;
        }
        otpDAO.markUsed(challenge.id());
        return true;
    }
}
