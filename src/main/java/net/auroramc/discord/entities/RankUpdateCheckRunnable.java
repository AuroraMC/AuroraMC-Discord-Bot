/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.discord.entities;

import net.auroramc.discord.DiscordBot;
import net.auroramc.discord.managers.GuildManager;
import net.auroramc.discord.managers.LinkManager;
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
            User user = DiscordBot.getJda().retrieveUserById(update.getDiscordId()).complete();
            if (user != null) {
                if (update.getNewRank() != null) {
                    for (Guild guild : user.getMutualGuilds()) {
                        if (guild.isMember(user)) {
                            guild.addRoleToMember(user, Objects.requireNonNull(guild.getRoleById(GuildManager.getRankMappings(guild.getIdLong()).get(update.getNewRank())))).queue();
                            guild.removeRoleFromMember(user, Objects.requireNonNull(guild.getRoleById(GuildManager.getRankMappings(guild.getIdLong()).get(update.getOldRank())))).queue();
                            Objects.requireNonNull(guild.getTextChannelById(GuildManager.getLinkLogId(guild.getIdLong()))).sendMessageEmbeds(new EmbedBuilder()
                                    .setTitle("User Rank Update")
                                    .setAuthor(user.getAsTag())
                                    .setThumbnail(user.getAvatarUrl())
                                    .addField("Old Rank", update.getOldRank().getName(), false)
                                    .addField("New Rank", update.getNewRank().getName(), false)
                                    .setTimestamp(Instant.now())
                                    .setColor(new Color(0, 170,170))
                                    .build()).queue();
                            if (guild.getIdLong() != DiscordBot.getSettings().getMasterDiscord()) {
                                if (!GuildManager.getAllowedRanks(guild.getIdLong()).contains(update.getNewRank())) {
                                    user.openPrivateChannel().complete().sendMessageEmbeds(new EmbedBuilder()
                                            .setAuthor("The AuroraMC Network Leadership Team", "https://auroramc.net", "https://auroramc.net/styles/pie/img/AuroraMCLogoStaffPadded.png")
                                            .setTitle("Removed from Discord!")
                                            .setDescription("Your Rank was updated and as a result, you are no longer allowed in the **" + guild.getName() + "** Discord so you were kicked.")
                                            .build()).queue();
                                    guild.kick(Objects.requireNonNull(guild.getMember(user)), "No longer allowed in Discord.").queue();
                                }
                            }
                        }
                    }
                    LinkManager.processOtherInvites(user, user.openPrivateChannel().complete(), DiscordBot.getDatabaseManager().getUUID(user.getIdLong()));
                } else if (update.getAddedSubrank() != null) {
                    for (Guild guild : user.getMutualGuilds()) {
                        if (guild.isMember(user)) {
                            guild.addRoleToMember(user, Objects.requireNonNull(guild.getRoleById(GuildManager.getSubrankMappings(guild.getIdLong()).get(update.getAddedSubrank())))).queue();
                            Objects.requireNonNull(guild.getTextChannelById(GuildManager.getLinkLogId(guild.getIdLong()))).sendMessageEmbeds(new EmbedBuilder()
                                    .setTitle("User SubRank Update")
                                    .setAuthor(user.getAsTag())
                                    .setThumbnail(user.getAvatarUrl())
                                    .addField("SubRank Added", update.getAddedSubrank().getName(), false)
                                    .setTimestamp(Instant.now())
                                    .setColor(new Color(85, 255,85))
                                    .build()).queue();
                        }
                    }
                    LinkManager.processOtherInvites(user, user.openPrivateChannel().complete(), DiscordBot.getDatabaseManager().getUUID(user.getIdLong()));
                } else if (update.getRemovedSubrank() != null) {
                    for (Guild guild : user.getMutualGuilds()) {
                        if (guild.isMember(user)) {
                            guild.removeRoleFromMember(user, Objects.requireNonNull(guild.getRoleById(GuildManager.getSubrankMappings(guild.getIdLong()).get(update.getAddedSubrank())))).queue();
                            Objects.requireNonNull(guild.getTextChannelById(GuildManager.getLinkLogId(guild.getIdLong()))).sendMessageEmbeds(new EmbedBuilder()
                                    .setTitle("User SubRank Update")
                                    .setAuthor(user.getAsTag())
                                    .setThumbnail(user.getAvatarUrl())
                                    .addField("SubRank Removed", update.getRemovedSubrank().getName(), false)
                                    .setTimestamp(Instant.now())
                                    .setColor(new Color(255, 85,85))
                                    .build()).queue();
                            if (guild.getIdLong() != DiscordBot.getSettings().getMasterDiscord()) {
                                boolean allowed = false;
                                List<SubRank> subranks = DiscordBot.getDatabaseManager().getSubRanks(DiscordBot.getDatabaseManager().getUUID(user.getIdLong()));
                                for (SubRank subRank : subranks) {
                                    if (GuildManager.getAllowedSubRanks(guild.getIdLong()).contains(subRank)) {
                                        allowed = true;
                                        break;
                                    }
                                }
                                if (!allowed) {
                                    user.openPrivateChannel().complete().sendMessageEmbeds(new EmbedBuilder()
                                            .setAuthor("The AuroraMC Network Leadership Team", "https://auroramc.net", "https://auroramc.net/styles/pie/img/AuroraMCLogoStaffPadded.png")
                                            .setTitle("Removed from Discord!")
                                            .setDescription("Your SubRanks were updated and as a result, you are no longer allowed in the **" + guild.getName() + "** Discord so you were kicked.")
                                            .build()).queue();
                                    guild.kick(Objects.requireNonNull(guild.getMember(user)), "No longer allowed in Discord.").queue();
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
