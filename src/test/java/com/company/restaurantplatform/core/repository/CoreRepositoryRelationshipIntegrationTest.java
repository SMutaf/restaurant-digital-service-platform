package com.company.restaurantplatform.core.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.company.restaurantplatform.core.domain.entity.CustomerSession;
import com.company.restaurantplatform.core.domain.entity.MenuCategory;
import com.company.restaurantplatform.core.domain.entity.Order;
import com.company.restaurantplatform.core.domain.entity.OrderItem;
import com.company.restaurantplatform.core.domain.entity.PlaybackQueue;
import com.company.restaurantplatform.core.domain.entity.PlaybackQueueItem;
import com.company.restaurantplatform.core.domain.entity.PlaybackSession;
import com.company.restaurantplatform.core.domain.entity.Playlist;
import com.company.restaurantplatform.core.domain.entity.PlaylistSong;
import com.company.restaurantplatform.core.domain.entity.Product;
import com.company.restaurantplatform.core.domain.entity.Restaurant;
import com.company.restaurantplatform.core.domain.entity.RestaurantTable;
import com.company.restaurantplatform.core.domain.entity.RestaurantUser;
import com.company.restaurantplatform.core.domain.entity.RestaurantUserRole;
import com.company.restaurantplatform.core.domain.entity.Role;
import com.company.restaurantplatform.core.domain.entity.Song;
import com.company.restaurantplatform.core.domain.entity.TableQrCode;
import com.company.restaurantplatform.core.domain.entity.User;
import com.company.restaurantplatform.core.domain.entity.Vote;
import com.company.restaurantplatform.core.domain.entity.VotingRound;
import com.company.restaurantplatform.core.domain.entity.VotingRoundCandidateSong;
import com.company.restaurantplatform.core.domain.enums.CandidateSelectionSource;
import com.company.restaurantplatform.core.domain.enums.CustomerSessionStatus;
import com.company.restaurantplatform.core.domain.enums.MembershipStatus;
import com.company.restaurantplatform.core.domain.enums.OrderStatus;
import com.company.restaurantplatform.core.domain.enums.PlaybackQueueItemStatus;
import com.company.restaurantplatform.core.domain.enums.PlaybackQueueStatus;
import com.company.restaurantplatform.core.domain.enums.PlaybackSessionStatus;
import com.company.restaurantplatform.core.domain.enums.PlaylistStatus;
import com.company.restaurantplatform.core.domain.enums.RestaurantStatus;
import com.company.restaurantplatform.core.domain.enums.TableStatus;
import com.company.restaurantplatform.core.domain.enums.UserStatus;
import com.company.restaurantplatform.core.domain.enums.VotingRoundCreatedByType;
import com.company.restaurantplatform.core.domain.enums.VotingRoundStatus;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@ActiveProfiles("integration")
@EnabledIfEnvironmentVariable(named = "DB_USERNAME", matches = ".+")
class CoreRepositoryRelationshipIntegrationTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private RestaurantUserRepository restaurantUserRepository;

    @Autowired
    private RestaurantUserRoleRepository restaurantUserRoleRepository;

    @Autowired
    private RestaurantTableRepository restaurantTableRepository;

    @Autowired
    private TableQrCodeRepository tableQrCodeRepository;

    @Autowired
    private MenuCategoryRepository menuCategoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private PlaylistRepository playlistRepository;

    @Autowired
    private PlaylistSongRepository playlistSongRepository;

    @Autowired
    private PlaybackQueueRepository playbackQueueRepository;

    @Autowired
    private PlaybackQueueItemRepository playbackQueueItemRepository;

    @Autowired
    private PlaybackSessionRepository playbackSessionRepository;

    @Autowired
    private VotingRoundRepository votingRoundRepository;

    @Autowired
    private VotingRoundCandidateSongRepository votingRoundCandidateSongRepository;

    @Autowired
    private CustomerSessionRepository customerSessionRepository;

    @Autowired
    private VoteRepository voteRepository;

    @Test
    void shouldPersistAndLoadCoreRelationshipsThroughRepositories() {
        String suffix = UUID.randomUUID().toString().substring(0, 8);

        User user = new User();
        user.setEmail("owner-" + suffix + "@example.com");
        user.setPasswordHash("hashed-password");
        user.setFullName("Owner " + suffix);
        user.setStatus(UserStatus.ACTIVE);
        user = userRepository.save(user);

        Role role = new Role();
        role.setCode("TEST_ROLE_" + suffix);
        role.setName("Test Role " + suffix);
        role = roleRepository.save(role);

        Restaurant restaurant = new Restaurant();
        restaurant.setName("Restaurant " + suffix);
        restaurant.setSlug("restaurant-" + suffix);
        restaurant.setStatus(RestaurantStatus.ACTIVE);
        restaurant = restaurantRepository.save(restaurant);

        RestaurantUser restaurantUser = new RestaurantUser();
        restaurantUser.setRestaurant(restaurant);
        restaurantUser.setUser(user);
        restaurantUser.setMembershipStatus(MembershipStatus.ACTIVE);
        restaurantUser = restaurantUserRepository.save(restaurantUser);

        RestaurantUserRole restaurantUserRole = new RestaurantUserRole();
        restaurantUserRole.setRestaurantUser(restaurantUser);
        restaurantUserRole.setRole(role);
        restaurantUserRoleRepository.save(restaurantUserRole);

        RestaurantTable table = new RestaurantTable();
        table.setRestaurant(restaurant);
        table.setTableNumber("T-" + suffix);
        table.setName("Main Table");
        table.setCapacity(4);
        table.setStatus(TableStatus.ACTIVE);
        table = restaurantTableRepository.save(table);

        TableQrCode qrCode = new TableQrCode();
        qrCode.setRestaurantTable(table);
        qrCode.setToken("qr-" + suffix);
        qrCode.setQrImageUrl("https://example.com/" + suffix + ".png");
        qrCode = tableQrCodeRepository.save(qrCode);

        MenuCategory category = new MenuCategory();
        category.setRestaurant(restaurant);
        category.setName("Burgers " + suffix);
        category.setDisplayOrder(1);
        category = menuCategoryRepository.save(category);

        Product product = new Product();
        product.setRestaurant(restaurant);
        product.setCategory(category);
        product.setName("Classic Burger " + suffix);
        product.setDescription("Test burger");
        product.setPrice(new BigDecimal("250.00"));
        product = productRepository.save(product);

        Order order = new Order();
        order.setRestaurant(restaurant);
        order.setRestaurantTable(table);
        order.setCreatedByRestaurantUser(restaurantUser);
        order.setStatus(OrderStatus.RECEIVED);
        order.setTotalAmount(new BigDecimal("250.00"));
        order.setSubmittedAt(OffsetDateTime.now());
        order = orderRepository.save(order);

        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setProduct(product);
        orderItem.setProductNameSnapshot(product.getName());
        orderItem.setUnitPrice(product.getPrice());
        orderItem.setQuantity(1);
        orderItem.setNote("No onions");
        orderItemRepository.save(orderItem);

        Song songOne = new Song();
        songOne.setRestaurant(restaurant);
        songOne.setTitle("Song One " + suffix);
        songOne.setArtist("Artist A");
        songOne.setDurationSeconds(180);
        songOne.setSourceReference("source-1");
        songOne = songRepository.save(songOne);

        Song songTwo = new Song();
        songTwo.setRestaurant(restaurant);
        songTwo.setTitle("Song Two " + suffix);
        songTwo.setArtist("Artist B");
        songTwo.setDurationSeconds(190);
        songTwo.setSourceReference("source-2");
        songTwo = songRepository.save(songTwo);

        Playlist playlist = new Playlist();
        playlist.setRestaurant(restaurant);
        playlist.setName("Night Playlist " + suffix);
        playlist.setStatus(PlaylistStatus.ACTIVE);
        playlist.setCreatedByRestaurantUser(restaurantUser);
        playlist = playlistRepository.save(playlist);

        PlaylistSong playlistSongOne = new PlaylistSong();
        playlistSongOne.setPlaylist(playlist);
        playlistSongOne.setSong(songOne);
        playlistSongOne.setPosition(1);
        playlistSongRepository.save(playlistSongOne);

        PlaylistSong playlistSongTwo = new PlaylistSong();
        playlistSongTwo.setPlaylist(playlist);
        playlistSongTwo.setSong(songTwo);
        playlistSongTwo.setPosition(2);
        playlistSongRepository.save(playlistSongTwo);

        PlaybackQueue playbackQueue = new PlaybackQueue();
        playbackQueue.setRestaurant(restaurant);
        playbackQueue.setStatus(PlaybackQueueStatus.ACTIVE);
        playbackQueue = playbackQueueRepository.save(playbackQueue);

        PlaybackSession playbackSession = new PlaybackSession();
        playbackSession.setRestaurant(restaurant);
        playbackSession.setPlaylist(playlist);
        playbackSession.setPlaybackQueue(playbackQueue);
        playbackSession.setCurrentSong(songOne);
        playbackSession.setStatus(PlaybackSessionStatus.ACTIVE);
        playbackSession.setCurrentSongStartedAt(OffsetDateTime.now());
        playbackSession.setCurrentSongEndsAt(OffsetDateTime.now().plusMinutes(3));
        playbackSession.setVotingGraceEndsAt(OffsetDateTime.now().plusMinutes(3).plusSeconds(10));
        playbackSession = playbackSessionRepository.save(playbackSession);

        PlaybackQueueItem queueItem = new PlaybackQueueItem();
        queueItem.setPlaybackQueue(playbackQueue);
        queueItem.setSong(songTwo);
        queueItem.setSourceType("VOTING_WINNER");
        queueItem.setSourceReferenceId(1L);
        queueItem.setPosition(1);
        queueItem.setStatus(PlaybackQueueItemStatus.QUEUED);
        playbackQueueItemRepository.save(queueItem);

        VotingRound votingRound = new VotingRound();
        votingRound.setRestaurant(restaurant);
        votingRound.setPlaybackSession(playbackSession);
        votingRound.setPlaylist(playlist);
        votingRound.setStatus(VotingRoundStatus.OPEN);
        votingRound.setCreatedByType(VotingRoundCreatedByType.SYSTEM);
        votingRound.setScheduledCloseAt(OffsetDateTime.now().plusMinutes(3));
        votingRound = votingRoundRepository.save(votingRound);

        VotingRoundCandidateSong candidateOne = new VotingRoundCandidateSong();
        candidateOne.setVotingRound(votingRound);
        candidateOne.setSong(songOne);
        candidateOne.setCandidateNo((short) 1);
        candidateOne.setSelectionSource(CandidateSelectionSource.PLAYLIST_AUTO);
        votingRoundCandidateSongRepository.save(candidateOne);

        VotingRoundCandidateSong candidateTwo = new VotingRoundCandidateSong();
        candidateTwo.setVotingRound(votingRound);
        candidateTwo.setSong(songTwo);
        candidateTwo.setCandidateNo((short) 2);
        candidateTwo.setSelectionSource(CandidateSelectionSource.PLAYLIST_AUTO);
        votingRoundCandidateSongRepository.save(candidateTwo);

        CustomerSession customerSession = new CustomerSession();
        customerSession.setRestaurant(restaurant);
        customerSession.setRestaurantTable(table);
        customerSession.setTableQrCode(qrCode);
        customerSession.setSessionToken("session-" + suffix);
        customerSession.setStatus(CustomerSessionStatus.ACTIVE);
        customerSession.setExpiresAt(OffsetDateTime.now().plusHours(2));
        customerSession = customerSessionRepository.save(customerSession);

        Vote vote = new Vote();
        vote.setVotingRound(votingRound);
        vote.setSong(songTwo);
        vote.setCustomerSession(customerSession);
        vote.setRestaurantTable(table);
        voteRepository.save(vote);

        entityManager.flush();
        entityManager.clear();

        assertThat(userRepository.findByEmail(user.getEmail())).isPresent();
        assertThat(roleRepository.findByCode(role.getCode())).isPresent();
        assertThat(restaurantRepository.findBySlug(restaurant.getSlug())).isPresent();
        assertThat(restaurantUserRepository.findByRestaurantIdAndUserId(restaurant.getId(), user.getId())).isPresent();
        assertThat(restaurantUserRoleRepository.findAllByRestaurantUserId(restaurantUser.getId())).hasSize(1);
        assertThat(restaurantTableRepository.findByRestaurantIdAndTableNumber(restaurant.getId(), table.getTableNumber())).isPresent();
        assertThat(tableQrCodeRepository.findByTokenAndActiveTrue(qrCode.getToken())).isPresent();
        assertThat(menuCategoryRepository.findAllByRestaurantIdOrderByDisplayOrderAsc(restaurant.getId())).hasSize(1);
        assertThat(productRepository.findAllByRestaurantIdAndVisibleToCustomerTrue(restaurant.getId())).hasSize(1);
        assertThat(orderRepository.existsByRestaurantTableIdAndStatusIn(table.getId(), List.of(OrderStatus.RECEIVED))).isTrue();
        assertThat(orderItemRepository.findAllByOrderId(order.getId())).hasSize(1);
        assertThat(songRepository.findAllByRestaurantIdAndActiveTrue(restaurant.getId())).hasSize(2);
        assertThat(playlistRepository.findAllByRestaurantId(restaurant.getId())).hasSize(1);
        assertThat(playlistSongRepository.findAllByPlaylistIdOrderByPositionAsc(playlist.getId())).hasSize(2);
        assertThat(playbackQueueRepository.findByRestaurantId(restaurant.getId())).isPresent();
        assertThat(playbackQueueItemRepository.findAllByPlaybackQueueIdOrderByPositionAsc(playbackQueue.getId())).hasSize(1);
        assertThat(playbackSessionRepository.findByRestaurantId(restaurant.getId())).isPresent();
        assertThat(votingRoundRepository.findAllByRestaurantIdOrderByOpenedAtDesc(restaurant.getId())).hasSize(1);
        assertThat(votingRoundCandidateSongRepository.findAllByVotingRoundIdOrderByCandidateNoAsc(votingRound.getId())).hasSize(2);
        assertThat(customerSessionRepository.findBySessionToken(customerSession.getSessionToken())).isPresent();
        assertThat(voteRepository.countByVotingRoundIdAndSongId(votingRound.getId(), songTwo.getId())).isEqualTo(1);
        assertThat(voteRepository.findAllByVotingRoundId(votingRound.getId())).hasSize(1);
    }
}
