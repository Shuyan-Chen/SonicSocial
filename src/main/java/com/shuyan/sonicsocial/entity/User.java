package com.shuyan.sonicsocial.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    private String salt;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate;

    @Column(nullable = false, unique = true)
    private String email;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Track> favorites;

    private String location;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private UserMusicPreference musicPreference;

    @PrePersist
    public void prePersist(){
        createDate = new Date();
    }


}
