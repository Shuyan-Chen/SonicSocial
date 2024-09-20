package com.shuyan.sonicsocial.service.impl;


import com.shuyan.sonicsocial.context.UserContext;
import com.shuyan.sonicsocial.entity.Track;
import com.shuyan.sonicsocial.entity.User;
import com.shuyan.sonicsocial.repository.TrackRepository;
import com.shuyan.sonicsocial.repository.UserRepository;
import com.shuyan.sonicsocial.repository.UserTrackRepository;
import com.shuyan.sonicsocial.result.Result;
import com.shuyan.sonicsocial.service.MatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class MatchServiceImpl implements MatchService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserTrackRepository userTrackRepository;
    private static final double SIMILARITY_THRESHOLD = 0.5;

    @Override
    public List<User> findMatchingUsers(double latitude, double longitude, double radius) {
        Long currentUserId = UserContext.getCurrentUserId();
        List<Track> userTracks = userTrackRepository.findTracksByUserId(currentUserId);
        Map<String, Double> currentUserTaste = calculateAverageAudioFeatures(userTracks);
        List<User> potentialMatchingUsers = userRepository.findUsersByLocationAndRadius(latitude, longitude, radius);
        List<User> matchingUsers = new ArrayList<>();

        for (User user : potentialMatchingUsers) {
            if (!user.getId().equals(currentUserId)) {
                List<Track> otherUserTracks = userTrackRepository.findTracksByUserId(user.getId());
                Map<String, Double> otherUserTaste = calculateAverageAudioFeatures(otherUserTracks);

                if (isMusicTasteSimilar(currentUserTaste, otherUserTaste)) {
                    matchingUsers.add(user);
                }
            }
        }

        return matchingUsers;
    }

    public Map<String, Double> calculateAverageAudioFeatures(List<Track> tracks) {
        Map<String, Double> averageFeatures = new HashMap<>();
        double totalDanceability = 0;
        double totalEnergy = 0;
        double totalTempo = 0;
        double totalAcousticness = 0;
        double totalInstrumentalness = 0;
        double totalLiveness = 0;
        double totalLoudness = 0;
        double totalSpeechiness = 0;
        int trackCount = tracks.size();

        if (trackCount == 0) {
            return averageFeatures;
        }

        for (Track track : tracks) {
            totalDanceability += track.getDanceability();
            totalEnergy += track.getEnergy();
            totalTempo += track.getTempo();
            totalAcousticness += track.getAcousticness();
            totalInstrumentalness += track.getInstrumentalness();
            totalLiveness += track.getLiveness();
            totalLoudness += track.getLoudness();
            totalSpeechiness += track.getSpeechiness();
        }

        averageFeatures.put("danceability", totalDanceability / trackCount);
        averageFeatures.put("energy", totalEnergy / trackCount);
        averageFeatures.put("tempo", totalTempo / trackCount);
        averageFeatures.put("acousticness", totalAcousticness / trackCount);
        averageFeatures.put("instrumentalness", totalInstrumentalness / trackCount);
        averageFeatures.put("liveness", totalLiveness / trackCount);
        averageFeatures.put("loudness", totalLoudness / trackCount);
        averageFeatures.put("speechiness", totalSpeechiness / trackCount);

        return averageFeatures;
    }

    private boolean isMusicTasteSimilar(Map<String, Double> taste1, Map<String, Double> taste2) {
        double distance = Math.sqrt(
                Math.pow(taste1.get("danceability") - taste2.get("danceability"), 2) +
                        Math.pow(taste1.get("energy") - taste2.get("energy"), 2) +
                        Math.pow(taste1.get("tempo") - taste2.get("tempo"), 2) +
                        Math.pow(taste1.get("acousticness") - taste2.get("acousticness"), 2) +
                        Math.pow(taste1.get("instrumentalness") - taste2.get("instrumentalness"), 2) +
                        Math.pow(taste1.get("liveness") - taste2.get("liveness"), 2) +
                        Math.pow(taste1.get("loudness") - taste2.get("loudness"), 2) +
                        Math.pow(taste1.get("speechiness") - taste2.get("speechiness"), 2)
        );
        return distance < SIMILARITY_THRESHOLD;
    }
}
