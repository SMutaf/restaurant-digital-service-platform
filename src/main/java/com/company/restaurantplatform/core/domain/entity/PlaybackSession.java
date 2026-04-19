package com.company.restaurantplatform.core.domain.entity;

import com.company.restaurantplatform.core.domain.entity.base.AuditableEntity;
import com.company.restaurantplatform.core.domain.enums.PlaybackSessionStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "playback_sessions")
public class PlaybackSession extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "playlist_id", nullable = false)
    private Playlist playlist;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "playback_queue_id", nullable = false)
    private PlaybackQueue playbackQueue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_song_id")
    private Song currentSong;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PlaybackSessionStatus status;

    @Column(name = "current_song_started_at")
    private OffsetDateTime currentSongStartedAt;

    @Column(name = "current_song_ends_at")
    private OffsetDateTime currentSongEndsAt;

    @Column(name = "voting_grace_ends_at")
    private OffsetDateTime votingGraceEndsAt;
}
