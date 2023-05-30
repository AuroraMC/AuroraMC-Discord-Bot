/*
 * Copyright (c) 2022-2023 AuroraMC Ltd. All Rights Reserved.
 *
 * PRIVATE AND CONFIDENTIAL - Distribution and usage outside the scope of your job description is explicitly forbidden except in circumstances where a company director has expressly given written permission to do so.
 */

package net.auroramc.discord.entities;

import net.auroramc.discord.DiscordBot;

import java.util.UUID;

public final class PlusSubscription {

    private final long discordId;
    private final long endTimestamp;
    private final int daysSubscribed;
    private final int subscriptionStreak;
    private final long streakStartTimestamp;

    public PlusSubscription(long discordId) {
        this.discordId = discordId;

        UUID uuid = DiscordBot.getDatabaseManager().getUUID(discordId);

        this.endTimestamp = DiscordBot.getDatabaseManager().getExpire(uuid);
        this.daysSubscribed = DiscordBot.getDatabaseManager().getDaysSubscribed(uuid);
        this.streakStartTimestamp = DiscordBot.getDatabaseManager().getStreakStartTimestamp(uuid);
        this.subscriptionStreak = DiscordBot.getDatabaseManager().getStreak(uuid);
    }

    public long getDiscordId() {
        return discordId;
    }

    public long getEndTimestamp() {
        return endTimestamp;
    }

    public int getDaysSubscribed() {
        return daysSubscribed;
    }

    public int getSubscriptionStreak() {
        return subscriptionStreak;
    }

    public long getStreakStartTimestamp() {
        return streakStartTimestamp;
    }
}

