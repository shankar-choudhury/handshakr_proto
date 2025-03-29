package com.handshakr.handshakr_prototype.handshake;

import com.handshakr.handshakr_prototype.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

import static com.handshakr.handshakr_prototype.handshake.HandshakeStatus.*;

@Entity
@Getter
@Setter
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
    private String acceptorUsername;
    @ManyToOne
    @JoinColumn(name = "initiator_id")
    private User initiator;
    @ManyToOne
    @JoinColumn(name = "acceptor_id")
    private User acceptor;


    public Handshake(String handshakeName,
                     String encryptedDetails,
                     Date createdDate,
                     String initiatorUsername,
                     String acceptorUsername,
                     User initiator,
                     User acceptor) {
        this.handshakeName = handshakeName;
        this.encryptedDetails = encryptedDetails;
        this.createdDate = createdDate;
        this.initiatorUsername = initiatorUsername;
        this.acceptorUsername = acceptorUsername;
        this.initiator = initiator;
        this.acceptor = acceptor;
    }
}
