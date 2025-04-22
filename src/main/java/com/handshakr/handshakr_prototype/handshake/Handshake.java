package com.handshakr.handshakr_prototype.handshake;

import com.handshakr.handshakr_prototype.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.Date;

import static com.handshakr.handshakr_prototype.handshake.HandshakeStatus.*;

/**
 * Represents a handshake between two users in the system.
 * The entity stores information related to the handshake's status, creation, and participants.
 */
@Entity
@Getter
@Setter
@Table(name="Handshakes")
@NoArgsConstructor
public class Handshake {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String handshakeName;
    @Column
    private String encryptedDetails;
    @Column(nullable = false)
    private Instant createdDate; // Date handshake was agreed on
    @Column
    private Instant mostRecentUpdateDate; // Date for most recent handshake update
    @Column(nullable = false)
    private HandshakeStatus handshakeStatus = CREATED;
    @Column(nullable = false)
    private String initiatorUsername;
    @Column(nullable = false)
    private String receiverUsername;
    @ManyToOne
    @JoinColumn(name = "initiator_id")
    private User initiator;
    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private User receiver;


    /**
     * Constructor for creating a new handshake.
     *
     * @param handshakeName The unique name of the handshake.
     * @param encryptedDetails The encrypted details of the handshake.
     * @param initiatorUsername The username of the initiator.
     * @param receiverUsername The username of the receiver.
     * @param initiator The User entity representing the initiator.
     * @param receiver The User entity representing the receiver.
     */
    public Handshake(String handshakeName,
                     String encryptedDetails,
                     String initiatorUsername,
                     String receiverUsername,
                     User initiator,
                     User receiver) {
        this.handshakeName = handshakeName;
        this.encryptedDetails = encryptedDetails;
        this.createdDate = Instant.now();
        this.initiatorUsername = initiatorUsername;
        this.receiverUsername = receiverUsername;
        this.initiator = initiator;
        this.receiver = receiver;
    }
}
