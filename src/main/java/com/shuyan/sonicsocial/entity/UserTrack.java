package com.shuyan.sonicsocial.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_tracks")
public class UserTrack {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "FK_USER"))
    private User user;

    @ManyToOne
    @JoinColumn(name = "track_id", foreignKey = @ForeignKey(name = "FK_TRACK"))
    private Track track;

    @CreationTimestamp
    private LocalDateTime addedAt;

}
