/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.discord.managers;

import net.auroramc.discord.DiscordBot;
import net.auroramc.discord.entities.BotSettings;
import net.auroramc.discord.entities.Rank;
import net.auroramc.discord.entities.SubRank;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;

import java.awt.*;
import java.time.Instant;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class LinkManager {


    public static void onLink(User user, UUID uuid) {
        Guild guild = user.getJDA().getGuildById(DiscordBot.getSettings().getMasterDiscord());
        assert guild != null;
        Role role = guild.getRoleById(886329879002505217L);
        assert role != null;
        guild.removeRoleFromMember(user.getId(), role).queue();

        Rank rank = DiscordBot.getDatabaseManager().getRank(uuid);
        List<SubRank> subranks = DiscordBot.getDatabaseManager().getSubRanks(uuid);

        Map<Rank, Long> rankMappings = GuildManager.getRankMappings(DiscordBot.getSettings().getMasterDiscord());
        Map<SubRank, Long> subrankMappings = GuildManager.getSubrankMappings(DiscordBot.getSettings().getMasterDiscord());

        role = guild.getRoleById(rankMappings.get(rank));
        assert role != null;
        guild.addRoleToMember(user.getId(), role).queue();

        List<String> subranksAdded = new ArrayList<>();

        for (SubRank subRank : subranks) {
            role = guild.getRoleById(subrankMappings.get(subRank));
            assert role != null;
            guild.addRoleToMember(user.getId(), role).queue();
        }

        TextChannel channel = guild.getTextChannelById(GuildManager.getLinkLogId(DiscordBot.getSettings().getMasterDiscord()));
        assert channel != null;
        channel.sendMessageEmbeds(new EmbedBuilder()
                .setTitle("User Link")
                .setThumbnail(user.getAvatarUrl())
                .addField("Rank", rank.getName(), false)
                .addField("SubRanks", (subranks.size() == 0?"None":String.join("\n", subranksAdded)), false)
                .setTimestamp(Instant.now())
                .setColor(new Color(0, 170,170))
                .build()).queue();
    }

    public static void onJoin(Guild guild, User user, UUID uuid) {
        Rank rank = DiscordBot.getDatabaseManager().getRank(uuid);
        List<SubRank> subranks = DiscordBot.getDatabaseManager().getSubRanks(uuid);

        Map<Rank, Long> rankMappings = GuildManager.getRankMappings(guild.getIdLong());
        Map<SubRank, Long> subrankMappings = GuildManager.getSubrankMappings(guild.getIdLong());

        Role role = guild.getRoleById(rankMappings.get(rank));
        assert role != null;
        guild.addRoleToMember(user.getId(), role).queue();

        List<String> subranksAdded = new ArrayList<>();

        for (SubRank subRank : subranks) {
            role = guild.getRoleById(subrankMappings.get(subRank));
            assert role != null;
            guild.addRoleToMember(user.getId(), role).queue();
        }

        TextChannel channel = guild.getTextChannelById(GuildManager.getLinkLogId(guild.getIdLong()));
        assert channel != null;
        channel.sendMessageEmbeds(new EmbedBuilder()
                .setTitle("User Link")
                .setThumbnail(user.getAvatarUrl())
                .addField("Rank", rank.getName(), false)
                .addField("SubRanks", (subranks.size() == 0?"None":String.join("\n", subranksAdded)), false)
                .setTimestamp(Instant.now())
                .setColor(new Color(0, 170,170))
                .build()).queue();
    }

    public static void processOtherInvites(User user, Message message) {

    }

}
