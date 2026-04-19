package com.company.restaurantplatform.core.domain.entity;

import com.company.restaurantplatform.core.domain.entity.base.BaseEntity;
import com.company.restaurantplatform.core.domain.enums.CandidateSelectionSource;
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
@Table(name = "voting_round_candidate_songs")
public class VotingRoundCandidateSong extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "voting_round_id", nullable = false)
    private VotingRound votingRound;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "song_id", nullable = false)
    private Song song;

    @Column(name = "candidate_no", nullable = false)
    private Short candidateNo;

    @Enumerated(EnumType.STRING)
    @Column(name = "selection_source", nullable = false)
    private CandidateSelectionSource selectionSource;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = OffsetDateTime.now();
        }
    }
}
