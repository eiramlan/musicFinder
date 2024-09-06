package com.example.musicFinder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class MusicFinderController {

    // ObjectMapper to help with JSON formatting
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Basic status endpoint
    @GetMapping("/status")
    public String getStatus() {
        return "{\"status\":\"Application is running\"}";
    }

    // Fetch lyrics from Lyrics.ovh API and clean newline characters
    private String getFormattedLyrics(String artist, String song) {
        String apiUrl = "https://api.lyrics.ovh/v1/" + artist + "/" + song;
        RestTemplate restTemplate = new RestTemplate();
        try {
            // Fetch the raw lyrics
            String rawLyrics = restTemplate.getForObject(apiUrl, String.class);
            
            // Replace \n and \r with <br> for proper HTML formatting
            String formattedLyrics = rawLyrics.replace("\\n", "<br>").replace("\\r", "<br>");
            
            // Return formatted lyrics
            return formattedLyrics;
        } catch (Exception e) {
            return "{\"error\":\"Lyrics not found\"}";
        }
    }

    // Generate YouTube search link based on artist and song
    private String getYouTubeSearchUrl(String artist, String song) {
        String searchQuery = artist.replace(" ", "+") + "+" + song.replace(" ", "+");
        return "https://www.youtube.com/results?search_query=" + searchQuery;
    }

    // Fetch song details, YouTube search link, and formatted lyrics
    @GetMapping("/song/{artist}/{name}")
    public String getSongDetails(@PathVariable String artist, @PathVariable String name) throws Exception {
        // Get the YouTube search link
        String youtubeSearchUrl = getYouTubeSearchUrl(artist, name);

        // Get the formatted song lyrics
        String lyrics = getFormattedLyrics(artist, name);

        // Build a JSON response with pretty-print formatting
        ObjectNode response = objectMapper.createObjectNode();
        response.put("song", name);
        response.put("artist", artist);
        response.put("youtubeSearch", youtubeSearchUrl);
        response.put("lyrics", lyrics);

        // Return the JSON response with pretty-print enabled
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(response);
    }
}