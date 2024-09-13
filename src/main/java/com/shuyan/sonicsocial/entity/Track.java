package com.shuyan.sonicsocial.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Builder
@Data
@NoArgsConstructor
@Table(name = "tracks")
public class Track {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String trackId;

    private String name;

    private String artist;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ElementCollection
    private List<String> mostFrequentGenres;

    private double energy;

    private double danceability;

    private String genres;

    @ElementCollection
    private List<String> favoriteArtists;

    private double tempo;

    private LocalDateTime lastUpdated;

    public Track(String trackId, String name, String artist) {
    }
}
