package com.handshakr.handshakr_prototype.handshake;

import com.handshakr.handshakr_prototype.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

import static com.handshakr.handshakr_prototype.handshake.HandshakeStatus.*;

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
    private Date createdDate; // Date handshake was agreed on
    @Column
    private Date mostRecentUpdateDate; // Date for most recent handshake update
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


    public Handshake(String handshakeName,
                     String encryptedDetails,
                     Date createdDate,
                     String initiatorUsername,
                     String receiverUsername,
                     User initiator,
                     User receiver) {
        this.handshakeName = handshakeName;
        this.encryptedDetails = encryptedDetails;
        this.createdDate = createdDate;
        this.initiatorUsername = initiatorUsername;
        this.receiverUsername = receiverUsername;
        this.initiator = initiator;
        this.receiver = receiver;
    }
}
