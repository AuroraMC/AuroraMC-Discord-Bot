/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.discord.entities;

public class RankUpdate {

    private final long discordId;
    private final Rank newRank;
    private final Rank oldRank;
    private final SubRank addedSubrank;
    private final SubRank removedSubrank;

    public RankUpdate(long discordId, Rank oldRank, Rank newRank, SubRank addedSubrank, SubRank removedSubrank) {
        this.addedSubrank = addedSubrank;
        this.oldRank = oldRank;
        this.removedSubrank = removedSubrank;
        this.discordId = discordId;
        this.newRank = newRank;
    }

    public Rank getNewRank() {
        return newRank;
    }

    public Rank getOldRank() {
        return oldRank;
    }

    public SubRank getAddedSubrank() {
        return addedSubrank;
    }

    public SubRank getRemovedSubrank() {
        return removedSubrank;
    }

    public long getDiscordId() {
        return discordId;
    }
}
