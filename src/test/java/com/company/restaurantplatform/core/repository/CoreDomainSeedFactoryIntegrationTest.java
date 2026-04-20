package com.company.restaurantplatform.core.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.company.restaurantplatform.support.seed.CoreDomainSeedData;
import com.company.restaurantplatform.support.seed.CoreDomainSeedFactory;
import jakarta.persistence.EntityManager;
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
class CoreDomainSeedFactoryIntegrationTest {

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
    void shouldCreateReusableSeedScenario() {
        CoreDomainSeedFactory seedFactory = new CoreDomainSeedFactory(
                userRepository,
                roleRepository,
                restaurantRepository,
                restaurantUserRepository,
                restaurantUserRoleRepository,
                restaurantTableRepository,
                tableQrCodeRepository,
                menuCategoryRepository,
                productRepository,
                orderRepository,
                orderItemRepository,
                songRepository,
                playlistRepository,
                playlistSongRepository,
                playbackQueueRepository,
                playbackQueueItemRepository,
                playbackSessionRepository,
                votingRoundRepository,
                votingRoundCandidateSongRepository,
                customerSessionRepository,
                voteRepository
        );

        CoreDomainSeedData seed = seedFactory.seedFullScenario(UUID.randomUUID().toString().substring(0, 8));

        entityManager.flush();
        entityManager.clear();

        assertThat(seed.restaurant().getId()).isNotNull();
        assertThat(seed.order().getRestaurant().getId()).isEqualTo(seed.restaurant().getId());
        assertThat(seed.orderItem().getOrder().getId()).isEqualTo(seed.order().getId());
        assertThat(seed.playlistSongOne().getPlaylist().getId()).isEqualTo(seed.playlist().getId());
        assertThat(seed.playbackSession().getPlaybackQueue().getId()).isEqualTo(seed.playbackQueue().getId());
        assertThat(seed.candidateSongOne().getVotingRound().getId()).isEqualTo(seed.votingRound().getId());
        assertThat(seed.vote().getCustomerSession().getId()).isEqualTo(seed.customerSession().getId());
        assertThat(seed.vote().getSong().getId()).isEqualTo(seed.songTwo().getId());
    }
}
