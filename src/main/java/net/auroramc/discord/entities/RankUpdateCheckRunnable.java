/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.discord.entities;

import net.auroramc.discord.DiscordBot;
import net.auroramc.discord.managers.GuildManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

public class RankUpdateCheckRunnable implements Runnable {
    @Override
    public void run() {
        List<RankUpdate> rankUpdates = DiscordBot.getDatabaseManager().getRankUpdates();
        for (RankUpdate update : rankUpdates) {
            User user = DiscordBot.getJda().getUserById(update.getDiscordId());
            if (user != null) {
                if (update.getNewRank() != null) {
                    for (Guild guild : user.getMutualGuilds()) {
                        guild.addRoleToMember(user.getIdLong(), Objects.requireNonNull(guild.getRoleById(GuildManager.getRankMappings(guild.getIdLong()).get(update.getNewRank())))).queue();
                        guild.removeRoleFromMember(user.getIdLong(), Objects.requireNonNull(guild.getRoleById(GuildManager.getRankMappings(guild.getIdLong()).get(update.getOldRank())))).queue();
                        Objects.requireNonNull(guild.getTextChannelById(GuildManager.getLinkLogId(guild.getIdLong()))).sendMessageEmbeds(new EmbedBuilder()
                                .setTitle("User Rank Update")
                                .setThumbnail(user.getAvatarUrl())
                                .addField("Old Rank", update.getOldRank().getName(), false)
                                .addField("New Rank", update.getNewRank().getName(), false)
                                .setTimestamp(Instant.now())
                                .setColor(new Color(0, 170,170))
                                .build()).queue();
                    }
                } else if (update.getAddedSubrank() != null) {
                    for (Guild guild : user.getMutualGuilds()) {
                        guild.addRoleToMember(user.getIdLong(), Objects.requireNonNull(guild.getRoleById(GuildManager.getSubrankMappings(guild.getIdLong()).get(update.getAddedSubrank())))).queue();
                        Objects.requireNonNull(guild.getTextChannelById(GuildManager.getLinkLogId(guild.getIdLong()))).sendMessageEmbeds(new EmbedBuilder()
                                .setTitle("User SubRank Update")
                                .setThumbnail(user.getAvatarUrl())
                                .addField("SubRank Added", update.getAddedSubrank().getName(), false)
                                .setTimestamp(Instant.now())
                                .setColor(new Color(85, 255,85))
                                .build()).queue();
                    }
                } else if (update.getRemovedSubrank() != null) {
                    for (Guild guild : user.getMutualGuilds()) {
                        guild.removeRoleFromMember(user.getIdLong(), Objects.requireNonNull(guild.getRoleById(GuildManager.getSubrankMappings(guild.getIdLong()).get(update.getAddedSubrank())))).queue();
                        Objects.requireNonNull(guild.getTextChannelById(GuildManager.getLinkLogId(guild.getIdLong()))).sendMessageEmbeds(new EmbedBuilder()
                                .setTitle("User SubRank Update")
                                .setThumbnail(user.getAvatarUrl())
                                .addField("SubRank Removed", update.getRemovedSubrank().getName(), false)
                                .setTimestamp(Instant.now())
                                .setColor(new Color(255, 85,85))
                                .build()).queue();
                    }
                }
            }
        }
    }
}
