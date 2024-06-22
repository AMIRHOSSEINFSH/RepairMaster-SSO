package org.webproject.sso.model.dao.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.webproject.sso.model.enumModel.USERTYPE;
//import
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "User_tbl")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public UUID id;

    @Column(nullable = false)
    public String hashedPass;

    @Column(unique = true)
    //TODO write a validator for accepting gmail regex format only
    public String email;

    public String firstName;

    public String lastName;

    @Column(nullable = false)
    public int token_count= -1;

    @Column(nullable = false)
    public USERTYPE type;

    @OneToMany(mappedBy = "user", targetEntity = Session.class,cascade = CascadeType.PERSIST,fetch = FetchType.EAGER,orphanRemoval = true)
    public Set<Session> sessionList = new HashSet<>();

    @CreatedDate
    public LocalDateTime created_at = LocalDateTime.now(ZoneId.of("UTC"));

}
