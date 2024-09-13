package com.shuyan.sonicsocial.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "user_music_preferences")
public class UserMusicPreference {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @ElementCollection
    private List<String> mostFrequentGenres;

    private double averageEnergy;

    private double averageDanceability;

    @ElementCollection
    private List<String> favoriteArtists;

    private double tempoMin;

    private double tempoMax;

    private LocalDateTime lastUpdated;

}
