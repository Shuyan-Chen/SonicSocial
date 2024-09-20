package com.shuyan.sonicsocial.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Indexed;


@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tracks")
public class Track {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long trackId;

    private String name;

    private String artist;

    private double danceability;

    private double energy;

    private double  tempo;

    private double  acousticness;

    private double  instrumentalness;

    private double  liveness;

    private double  loudness;

    private double speechiness;


    public Track(String trackId, String name, String artist) {
    }
}
