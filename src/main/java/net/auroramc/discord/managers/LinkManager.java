/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.discord.managers;

import net.auroramc.discord.DiscordBot;
import net.auroramc.discord.entities.Rank;
import net.auroramc.discord.entities.SubRank;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;

import java.awt.*;
import java.time.Instant;
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
                .addField("SubRanks", ((subranks.size() == 0) ? "None" : String.join("\n", subranksAdded)), false)
                .setTimestamp(Instant.now())
                .setColor(new Color(0, 170,170))
                .build()).queue();
    }

    public static void onInviteFail(long intendedRecipient, User actualUser, String code, Guild guild) {
        TextChannel channel = guild.getTextChannelById(GuildManager.getLinkLogId(guild.getIdLong()));
        assert channel != null;
        channel.sendMessageEmbeds(new EmbedBuilder()
                .setTitle("Illegal User Join")
                .setThumbnail(actualUser.getAvatarUrl())
                .setDescription("User " + actualUser.getAsMention() + " joined with a invite link intended for ID: `" + intendedRecipient + "` with invite link code: `" + code + "`")
                .setTimestamp(Instant.now())
                .setColor(new Color(0, 170,170))
                .build()).queue();
    }

    public static void processOtherInvites(User user, Message message, UUID uuid) {
        List<Long> allowedGuilds = new ArrayList<>();


        Rank rank = DiscordBot.getDatabaseManager().getRank(uuid);
        List<SubRank> subranks = DiscordBot.getDatabaseManager().getSubRanks(uuid);
        for (long id : GuildManager.getSetupServers()) {
            if (GuildManager.getAllowedRanks(id).contains(rank)) {
                allowedGuilds.add(id);
                continue;
            }
            if (GuildManager.getAllowedSubRanks(id).stream().anyMatch(subranks::contains)) {
                allowedGuilds.add(id);
            }
        }
        if (allowedGuilds.size() > 0) {
            StringBuilder sb = new StringBuilder();
            for (long id : allowedGuilds) {
                Guild guild = user.getJDA().getGuildById(id);
                assert guild != null;
                if (guild.isMember(user)) {
                    continue;
                }
                TextChannel channel = guild.getTextChannelById(GuildManager.getMainChannel(id));
                assert channel != null;
                Invite invite = channel.createInvite()
                        .setMaxAge(0)
                        .setMaxUses(0)
                        .setUnique(true)
                        .complete();
                sb.append("**");
                sb.append(guild.getName());
                sb.append(":** http://discord.gg/");
                sb.append(invite.getCode());
                sb.append("\n");
                DiscordBot.getDatabaseManager().addInviteLink(guild.getIdLong(), user.getIdLong(), invite.getCode());
            }


            message.getChannel().sendMessageEmbeds(new EmbedBuilder()
                    .setAuthor("The AuroraMC Network Leadership Team", "auroramc.net", "https://auroramc.net/styles/pie/img/AuroraMCLogoStaffPadded.png")
                    .setTitle("Account linked!")
                    .setDescription("__**You've been invited!**__\n" +
                            "Because of your ranks, you have access to some additional\n" +
                            "Discord servers for your duties! The invite links are listed below:\n" +
                            " \n" +
                            sb.toString() +
                            " \n" +
                            "These invites are individual to you, and you should not share them with\n" +
                            "anyone, including your mentor/admin.\n" +
                            " \n" +
                            "These Discord invites are only for intended recipients only. Discord\n" +
                            "invite links and their intended recipient are logged. Any attempt to\n" +
                            "join from any other account will result in that account being permanently\n" +
                            "banned from that server and _you_ receive an automatic reprimand.\n" +
                            "**~AuroraMC Leadership Team**")
                    .setColor(new Color(0, 170,170))
                    .build()).queue();
        }
    }

    public static void processOtherInvites(User user, PrivateChannel privateChannel, UUID uuid) {
        List<Long> allowedGuilds = new ArrayList<>();


        Rank rank = DiscordBot.getDatabaseManager().getRank(uuid);
        List<SubRank> subranks = DiscordBot.getDatabaseManager().getSubRanks(uuid);
        for (long id : GuildManager.getSetupServers()) {
            if (GuildManager.getAllowedRanks(id).contains(rank)) {
                allowedGuilds.add(id);
                continue;
            }
            if (GuildManager.getAllowedSubRanks(id).stream().anyMatch(subranks::contains)) {
                allowedGuilds.add(id);
            }
        }
        if (allowedGuilds.size() > 0) {
            StringBuilder sb = new StringBuilder();
            for (long id : allowedGuilds) {
                Guild guild = user.getJDA().getGuildById(id);
                assert guild != null;
                if (guild.isMember(user)) {
                    continue;
                }
                TextChannel channel = guild.getTextChannelById(GuildManager.getMainChannel(id));
                assert channel != null;
                Invite invite = channel.createInvite()
                        .setMaxAge(0)
                        .setMaxUses(1)
                        .setUnique(true)
                        .complete();
                sb.append("**");
                sb.append(guild.getName());
                sb.append(":** http://discord.gg/");
                sb.append(invite.getCode());
                sb.append("\n");
                DiscordBot.getDatabaseManager().addInviteLink(guild.getIdLong(), user.getIdLong(), invite.getCode());
            }


            privateChannel.sendMessageEmbeds(new EmbedBuilder()
                    .setAuthor("The AuroraMC Network Leadership Team", "auroramc.net", "https://auroramc.net/styles/pie/img/AuroraMCLogoStaffPadded.png")
                    .setTitle("Account linked!")
                    .setDescription("__**You've been invited!**__\n" +
                            "Because of your ranks, you have access to some additional\n" +
                            "Discord servers for your duties! The invite links are listed below:\n" +
                            " \n" +
                            sb.toString() +
                            " \n" +
                            "These invites are individual to you, and you should not share them with\n" +
                            "anyone, including your mentor/admin.\n" +
                            " \n" +
                            "These Discord invites are only for intended recipients only. Discord\n" +
                            "invite links and their intended recipient are logged. Any attempt to\n" +
                            "join from any other account will result in that account being permanently\n" +
                            "banned from that server and _you_ receive an automatic reprimand.\n" +
                            "**~AuroraMC Leadership Team**")
                    .setColor(new Color(0, 170,170))
                    .build()).queue();
        }
    }

}
