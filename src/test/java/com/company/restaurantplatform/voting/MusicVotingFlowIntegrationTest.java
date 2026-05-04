package com.company.restaurantplatform.voting;

import static org.assertj.core.api.Assertions.assertThat;

import com.company.restaurantplatform.core.domain.entity.Restaurant;
import com.company.restaurantplatform.core.domain.entity.RestaurantTable;
import com.company.restaurantplatform.core.domain.entity.RestaurantUser;
import com.company.restaurantplatform.core.domain.entity.TableQrCode;
import com.company.restaurantplatform.core.domain.entity.User;
import com.company.restaurantplatform.core.domain.enums.MembershipStatus;
import com.company.restaurantplatform.core.domain.enums.RestaurantStatus;
import com.company.restaurantplatform.core.domain.enums.TableStatus;
import com.company.restaurantplatform.core.domain.enums.UserStatus;
import com.company.restaurantplatform.core.repository.RestaurantRepository;
import com.company.restaurantplatform.core.repository.RestaurantTableRepository;
import com.company.restaurantplatform.core.repository.RestaurantUserRepository;
import com.company.restaurantplatform.core.repository.TableQrCodeRepository;
import com.company.restaurantplatform.core.repository.UserRepository;
import com.company.restaurantplatform.customer.api.dto.CustomerSessionResponse;
import com.company.restaurantplatform.music.api.dto.AddPlaylistSongRequest;
import com.company.restaurantplatform.music.api.dto.CreatePlaylistRequest;
import com.company.restaurantplatform.music.api.dto.CreateSongRequest;
import com.company.restaurantplatform.music.api.dto.PlaylistResponse;
import com.company.restaurantplatform.music.api.dto.SongResponse;
import com.company.restaurantplatform.playback.api.dto.PlaybackQueueItemResponse;
import com.company.restaurantplatform.playback.api.dto.StartPlaybackSessionRequest;
import com.company.restaurantplatform.voting.api.dto.VoteRequest;
import com.company.restaurantplatform.voting.api.dto.VotingRoundResponse;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnabledIfEnvironmentVariable(named = "DB_USERNAME", matches = ".+")
class MusicVotingFlowIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestaurantUserRepository restaurantUserRepository;

    @Autowired
    private RestaurantTableRepository restaurantTableRepository;

    @Autowired
    private TableQrCodeRepository tableQrCodeRepository;

    @Test
    void shouldRunLocalMusicVotingFlowAndQueueWinner() {
        String suffix = UUID.randomUUID().toString().substring(0, 8);

        Restaurant restaurant = new Restaurant();
        restaurant.setName("Music Restaurant " + suffix);
        restaurant.setSlug("music-restaurant-" + suffix);
        restaurant.setStatus(RestaurantStatus.ACTIVE);
        restaurant = restaurantRepository.save(restaurant);

        User adminUser = new User();
        adminUser.setEmail("music-admin-" + suffix + "@example.com");
        adminUser.setPasswordHash("hashed");
        adminUser.setFullName("Music Admin");
        adminUser.setStatus(UserStatus.ACTIVE);
        adminUser = userRepository.save(adminUser);

        RestaurantUser restaurantUser = new RestaurantUser();
        restaurantUser.setRestaurant(restaurant);
        restaurantUser.setUser(adminUser);
        restaurantUser.setMembershipStatus(MembershipStatus.ACTIVE);
        restaurantUser = restaurantUserRepository.save(restaurantUser);

        RestaurantTable table = new RestaurantTable();
        table.setRestaurant(restaurant);
        table.setTableNumber("M-" + suffix);
        table.setName("Music Table");
        table.setCapacity(4);
        table.setStatus(TableStatus.ACTIVE);
        table = restaurantTableRepository.save(table);

        TableQrCode qrCode = new TableQrCode();
        qrCode.setRestaurantTable(table);
        qrCode.setToken("qr-" + suffix);
        qrCode.setActive(true);
        qrCode = tableQrCodeRepository.save(qrCode);

        SongResponse song1 = createSong(restaurant.getId(), "Song 1 " + suffix, 180);
        SongResponse song2 = createSong(restaurant.getId(), "Song 2 " + suffix, 185);
        SongResponse song3 = createSong(restaurant.getId(), "Song 3 " + suffix, 190);

        ResponseEntity<PlaylistResponse> playlistResponse = restTemplate.postForEntity(
                url("/api/admin/restaurants/" + restaurant.getId() + "/music/playlists"),
                new CreatePlaylistRequest("Main Playlist " + suffix, restaurantUser.getId()),
                PlaylistResponse.class
        );
        assertThat(playlistResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(playlistResponse.getBody()).isNotNull();
        Long playlistId = playlistResponse.getBody().id();

        addSongToPlaylist(restaurant.getId(), playlistId, song1.id());
        addSongToPlaylist(restaurant.getId(), playlistId, song2.id());
        addSongToPlaylist(restaurant.getId(), playlistId, song3.id());

        ResponseEntity<String> startPlaybackResponse = restTemplate.postForEntity(
                url("/api/admin/restaurants/" + restaurant.getId() + "/playback/sessions"),
                new StartPlaybackSessionRequest(playlistId, song1.id()),
                String.class
        );
        assertThat(startPlaybackResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ResponseEntity<VotingRoundResponse> openRoundResponse = restTemplate.postForEntity(
                url("/api/admin/restaurants/" + restaurant.getId() + "/voting/rounds/open"),
                null,
                VotingRoundResponse.class
        );
        assertThat(openRoundResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(openRoundResponse.getBody()).isNotNull();
        assertThat(openRoundResponse.getBody().candidates()).hasSize(2);
        assertThat(openRoundResponse.getBody().candidates().stream().map(candidate -> candidate.songId()))
                .containsExactly(song2.id(), song3.id());

        CustomerSessionResponse session1 = createCustomerSession(qrCode.getToken());
        CustomerSessionResponse session2 = createCustomerSession(qrCode.getToken());

        ResponseEntity<VotingRoundResponse> currentVotingResponse = restTemplate.getForEntity(
                url("/api/public/customer-sessions/" + session1.sessionToken() + "/voting/current"),
                VotingRoundResponse.class
        );
        assertThat(currentVotingResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(currentVotingResponse.getBody()).isNotNull();
        assertThat(currentVotingResponse.getBody().candidates()).hasSize(2);

        ResponseEntity<String> vote1Response = restTemplate.postForEntity(
                url("/api/public/customer-sessions/" + session1.sessionToken() + "/votes"),
                new VoteRequest(song2.id()),
                String.class
        );
        assertThat(vote1Response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ResponseEntity<String> vote2Response = restTemplate.postForEntity(
                url("/api/public/customer-sessions/" + session2.sessionToken() + "/votes"),
                new VoteRequest(song2.id()),
                String.class
        );
        assertThat(vote2Response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ResponseEntity<VotingRoundResponse> resolveResponse = restTemplate.postForEntity(
                url("/api/admin/restaurants/" + restaurant.getId() + "/voting/rounds/" + openRoundResponse.getBody().id() + "/resolve"),
                null,
                VotingRoundResponse.class
        );
        assertThat(resolveResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resolveResponse.getBody()).isNotNull();
        assertThat(resolveResponse.getBody().winningSongId()).isEqualTo(song2.id());

        ResponseEntity<PlaybackQueueItemResponse[]> queueResponse = restTemplate.getForEntity(
                url("/api/admin/restaurants/" + restaurant.getId() + "/playback/queue"),
                PlaybackQueueItemResponse[].class
        );
        assertThat(queueResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(queueResponse.getBody()).isNotNull();
        assertThat(queueResponse.getBody()).hasSize(1);
        assertThat(queueResponse.getBody()[0].songId()).isEqualTo(song2.id());
        assertThat(queueResponse.getBody()[0].sourceType()).isEqualTo("VOTING_WINNER");
    }

    private SongResponse createSong(Long restaurantId, String title, int durationSeconds) {
        ResponseEntity<SongResponse> response = restTemplate.postForEntity(
                url("/api/admin/restaurants/" + restaurantId + "/music/songs"),
                new CreateSongRequest(title, "Artist", durationSeconds, "local:" + title),
                SongResponse.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        return response.getBody();
    }

    private void addSongToPlaylist(Long restaurantId, Long playlistId, Long songId) {
        ResponseEntity<PlaylistResponse> response = restTemplate.postForEntity(
                url("/api/admin/restaurants/" + restaurantId + "/music/playlists/" + playlistId + "/songs"),
                new AddPlaylistSongRequest(songId),
                PlaylistResponse.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
    }

    private CustomerSessionResponse createCustomerSession(String qrToken) {
        ResponseEntity<CustomerSessionResponse> response = restTemplate.postForEntity(
                url("/api/public/qr/" + qrToken + "/sessions"),
                null,
                CustomerSessionResponse.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        return response.getBody();
    }

    private String url(String path) {
        return "http://localhost:" + port + path;
    }
}
