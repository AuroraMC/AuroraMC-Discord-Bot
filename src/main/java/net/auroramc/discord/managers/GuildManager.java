/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.discord.managers;

import net.auroramc.discord.DiscordBot;
import net.auroramc.discord.entities.Rank;
import net.auroramc.discord.entities.SubRank;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;

import java.util.*;

public class GuildManager {

    private static final List<Long> setupServers;
    private static final Map<Long, Long> mainChannelMappings;
    private static final Map<Long, Long> serverLogMappings;
    private static final Map<Long, Long> linkLogMappings;
    private static final Map<Long, Map<Rank, Long>> rankMappings;
    private static final Map<Long, Map<SubRank, Long>> subrankMappings;
    private static final Map<Long, List<Rank>> allowedRanks;
    private static final Map<Long, List<SubRank>> allowedSubRanks;

    static {
        setupServers = DiscordBot.getDatabaseManager().getRegisteredGuilds();
        serverLogMappings = new HashMap<>();
        linkLogMappings = new HashMap<>();
        mainChannelMappings = new HashMap<>();
        rankMappings = DiscordBot.getDatabaseManager().getRankMappings();
        subrankMappings = DiscordBot.getDatabaseManager().getSubRankMappings();
        allowedRanks = new HashMap<>();
        allowedSubRanks = new HashMap<>();

        for (long id : setupServers) {
            allowedRanks.put(id, DiscordBot.getDatabaseManager().getAllowedRanks(id));
            allowedSubRanks.put(id, DiscordBot.getDatabaseManager().getAllowedSubRanks(id));
            serverLogMappings.put(id, DiscordBot.getDatabaseManager().getLoggingChannel(id));
            linkLogMappings.put(id, DiscordBot.getDatabaseManager().getLinkingChannel(id));
            mainChannelMappings.put(id, DiscordBot.getDatabaseManager().getMainChannel(id));
        }
    }

    public static void onGuildSetup(Guild guild, long serverLog, long linkLog) {
        serverLogMappings.put(guild.getIdLong(), serverLog);
        linkLogMappings.put(guild.getIdLong(), linkLog);

        DiscordBot.getDatabaseManager().addChannelMappings(guild.getIdLong(), serverLog, linkLog);

        List<Rank> ranks = new ArrayList<>(Arrays.asList(Rank.values()));
        Collections.reverse(ranks);
        Map<Rank, Long> rankMappings = new HashMap<>();
        Map<SubRank, Long> subrankMappings = new HashMap<>();

        for (Rank rank : ranks) {
            Role role = guild.createRole()
                    .setColor(rank.getColor())
                    .setName(rank.getRankAppearance())
                    .setHoisted(true)
                    .setMentionable(false)
                    .setPermissions(Permission.MESSAGE_HISTORY, Permission.VOICE_CONNECT, Permission.MESSAGE_SEND, Permission.VOICE_SPEAK)
                    .complete();
            rankMappings.put(rank, role.getIdLong());
        }

        for (SubRank rank : SubRank.values()) {
            Role role = guild.createRole()
                    .setColor(rank.getColor())
                    .setName(rank.getName())
                    .setHoisted(true)
                    .setMentionable(false)
                    .setPermissions(Permission.MESSAGE_HISTORY, Permission.VOICE_CONNECT, Permission.MESSAGE_SEND, Permission.VOICE_SPEAK)
                    .complete();
            subrankMappings.put(rank, role.getIdLong());
        }

        addMappings(guild, rankMappings, subrankMappings);
        DiscordBot.getDatabaseManager().addSetupServer(guild.getIdLong());
    }

    public static long getServerLogId(long guildId) {
        return serverLogMappings.get(guildId);
    }

    public static long getLinkLogId(long guildId) {
        return linkLogMappings.get(guildId);
    }

    public static void addMappings(Guild guild, Map<Rank, Long> ranks, Map<SubRank, Long> subranks) {
        rankMappings.put(guild.getIdLong(), ranks);
        subrankMappings.put(guild.getIdLong(), subranks);
        DiscordBot.getDatabaseManager().addRankMappings(guild.getIdLong(), ranks, subranks);
    }


    public static Map<Rank, Long> getRankMappings(long guildId) {
        return rankMappings.get(guildId);
    }

    public static Map<SubRank, Long> getSubrankMappings(long guildId) {
        return subrankMappings.get(guildId);
    }

    public static List<Rank> getAllowedRanks(long guildId) {
        return allowedRanks.get(guildId);
    }

    public static List<SubRank> getAllowedSubRanks(long guildId) {
        return allowedSubRanks.get(guildId);
    }

    public static List<Long> getSetupServers() {
        return setupServers;
    }

    public static long getMainChannel(long guildId) {
        return mainChannelMappings.get(guildId);
    }
}