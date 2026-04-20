package com.company.restaurantplatform.support.seed;

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
import com.company.restaurantplatform.core.repository.CustomerSessionRepository;
import com.company.restaurantplatform.core.repository.MenuCategoryRepository;
import com.company.restaurantplatform.core.repository.OrderItemRepository;
import com.company.restaurantplatform.core.repository.OrderRepository;
import com.company.restaurantplatform.core.repository.PlaybackQueueItemRepository;
import com.company.restaurantplatform.core.repository.PlaybackQueueRepository;
import com.company.restaurantplatform.core.repository.PlaybackSessionRepository;
import com.company.restaurantplatform.core.repository.PlaylistRepository;
import com.company.restaurantplatform.core.repository.PlaylistSongRepository;
import com.company.restaurantplatform.core.repository.ProductRepository;
import com.company.restaurantplatform.core.repository.RestaurantRepository;
import com.company.restaurantplatform.core.repository.RestaurantTableRepository;
import com.company.restaurantplatform.core.repository.RestaurantUserRepository;
import com.company.restaurantplatform.core.repository.RestaurantUserRoleRepository;
import com.company.restaurantplatform.core.repository.RoleRepository;
import com.company.restaurantplatform.core.repository.SongRepository;
import com.company.restaurantplatform.core.repository.TableQrCodeRepository;
import com.company.restaurantplatform.core.repository.UserRepository;
import com.company.restaurantplatform.core.repository.VoteRepository;
import com.company.restaurantplatform.core.repository.VotingRoundCandidateSongRepository;
import com.company.restaurantplatform.core.repository.VotingRoundRepository;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class CoreDomainSeedFactory {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RestaurantRepository restaurantRepository;
    private final RestaurantUserRepository restaurantUserRepository;
    private final RestaurantUserRoleRepository restaurantUserRoleRepository;
    private final RestaurantTableRepository restaurantTableRepository;
    private final TableQrCodeRepository tableQrCodeRepository;
    private final MenuCategoryRepository menuCategoryRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final SongRepository songRepository;
    private final PlaylistRepository playlistRepository;
    private final PlaylistSongRepository playlistSongRepository;
    private final PlaybackQueueRepository playbackQueueRepository;
    private final PlaybackQueueItemRepository playbackQueueItemRepository;
    private final PlaybackSessionRepository playbackSessionRepository;
    private final VotingRoundRepository votingRoundRepository;
    private final VotingRoundCandidateSongRepository votingRoundCandidateSongRepository;
    private final CustomerSessionRepository customerSessionRepository;
    private final VoteRepository voteRepository;

    public CoreDomainSeedFactory(
            UserRepository userRepository,
            RoleRepository roleRepository,
            RestaurantRepository restaurantRepository,
            RestaurantUserRepository restaurantUserRepository,
            RestaurantUserRoleRepository restaurantUserRoleRepository,
            RestaurantTableRepository restaurantTableRepository,
            TableQrCodeRepository tableQrCodeRepository,
            MenuCategoryRepository menuCategoryRepository,
            ProductRepository productRepository,
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            SongRepository songRepository,
            PlaylistRepository playlistRepository,
            PlaylistSongRepository playlistSongRepository,
            PlaybackQueueRepository playbackQueueRepository,
            PlaybackQueueItemRepository playbackQueueItemRepository,
            PlaybackSessionRepository playbackSessionRepository,
            VotingRoundRepository votingRoundRepository,
            VotingRoundCandidateSongRepository votingRoundCandidateSongRepository,
            CustomerSessionRepository customerSessionRepository,
            VoteRepository voteRepository
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.restaurantRepository = restaurantRepository;
        this.restaurantUserRepository = restaurantUserRepository;
        this.restaurantUserRoleRepository = restaurantUserRoleRepository;
        this.restaurantTableRepository = restaurantTableRepository;
        this.tableQrCodeRepository = tableQrCodeRepository;
        this.menuCategoryRepository = menuCategoryRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.songRepository = songRepository;
        this.playlistRepository = playlistRepository;
        this.playlistSongRepository = playlistSongRepository;
        this.playbackQueueRepository = playbackQueueRepository;
        this.playbackQueueItemRepository = playbackQueueItemRepository;
        this.playbackSessionRepository = playbackSessionRepository;
        this.votingRoundRepository = votingRoundRepository;
        this.votingRoundCandidateSongRepository = votingRoundCandidateSongRepository;
        this.customerSessionRepository = customerSessionRepository;
        this.voteRepository = voteRepository;
    }

    public CoreDomainSeedData seedFullScenario(String suffix) {
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
        restaurantUserRole = restaurantUserRoleRepository.save(restaurantUserRole);

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
        orderItem = orderItemRepository.save(orderItem);

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
        playlistSongOne = playlistSongRepository.save(playlistSongOne);

        PlaylistSong playlistSongTwo = new PlaylistSong();
        playlistSongTwo.setPlaylist(playlist);
        playlistSongTwo.setSong(songTwo);
        playlistSongTwo.setPosition(2);
        playlistSongTwo = playlistSongRepository.save(playlistSongTwo);

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
        queueItem = playbackQueueItemRepository.save(queueItem);

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
        candidateOne = votingRoundCandidateSongRepository.save(candidateOne);

        VotingRoundCandidateSong candidateTwo = new VotingRoundCandidateSong();
        candidateTwo.setVotingRound(votingRound);
        candidateTwo.setSong(songTwo);
        candidateTwo.setCandidateNo((short) 2);
        candidateTwo.setSelectionSource(CandidateSelectionSource.PLAYLIST_AUTO);
        candidateTwo = votingRoundCandidateSongRepository.save(candidateTwo);

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
        vote = voteRepository.save(vote);

        return new CoreDomainSeedData(
                user,
                role,
                restaurant,
                restaurantUser,
                restaurantUserRole,
                table,
                qrCode,
                category,
                product,
                order,
                orderItem,
                songOne,
                songTwo,
                playlist,
                playlistSongOne,
                playlistSongTwo,
                playbackQueue,
                queueItem,
                playbackSession,
                votingRound,
                candidateOne,
                candidateTwo,
                customerSession,
                vote
        );
    }
}
