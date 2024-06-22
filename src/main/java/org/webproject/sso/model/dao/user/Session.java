package org.webproject.sso.model.dao.user;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "session_tbl")
@Getter
@Setter
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public UUID sessionId;

    @Column(nullable = false)
    public String deviceOwner;

    //    @Column(nullable = false)
    public int server_secret;

    //    @Column(nullable = false)
    public int user_rik;

    @Column(nullable = false)
    public boolean isOtpVerified = false;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    public User user;

    @CreatedDate
    public LocalDateTime created_at = LocalDateTime.now(ZoneId.of("UTC"));

    public Session(String deviceOwner, int server_secret, int user_rik) {
        this.deviceOwner = deviceOwner;
        this.server_secret = server_secret;
        this.user_rik = user_rik;
    }
}
