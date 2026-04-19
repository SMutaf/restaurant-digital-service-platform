package com.company.restaurantplatform.core.domain.entity;

import com.company.restaurantplatform.core.domain.entity.base.BaseEntity;
import com.company.restaurantplatform.core.domain.enums.VotingRoundCreatedByType;
import com.company.restaurantplatform.core.domain.enums.VotingRoundStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "voting_rounds")
public class VotingRound extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "playback_session_id", nullable = false)
    private PlaybackSession playbackSession;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "playlist_id", nullable = false)
    private Playlist playlist;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private VotingRoundStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "created_by_type", nullable = false)
    private VotingRoundCreatedByType createdByType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_restaurant_user_id")
    private RestaurantUser createdByRestaurantUser;

    @Column(name = "opened_at", nullable = false)
    private OffsetDateTime openedAt;

    @Column(name = "scheduled_close_at", nullable = false)
    private OffsetDateTime scheduledCloseAt;

    @Column(name = "closed_at")
    private OffsetDateTime closedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "winning_song_id")
    private Song winningSong;

    @Column(name = "tie_break_applied", nullable = false)
    private boolean tieBreakApplied;

    @PrePersist
    protected void onCreate() {
        if (openedAt == null) {
            openedAt = OffsetDateTime.now();
        }
    }
}
