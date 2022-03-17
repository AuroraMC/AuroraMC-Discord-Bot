/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.discord.commands.admin;

import net.auroramc.discord.DiscordBot;
import net.auroramc.discord.entities.Command;
import net.auroramc.discord.entities.Permission;
import net.auroramc.discord.entities.Rank;
import net.auroramc.discord.entities.SubRank;
import net.auroramc.discord.managers.GuildManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class CommandGenerateLink extends Command {


    public CommandGenerateLink() {
        super("generatelink", Arrays.asList("genlink", "gencode"), Collections.singletonList(Permission.ADMIN));
    }

    @Override
    public void execute(Message message, Member member, String aliasUsed, List<String> args) {
        if (args.size() == 1) {
            long id;
            try {
                id = Long.parseLong(args.get(0));
            } catch (NumberFormatException e) {
                message.reply("Invalid syntax. Correct syntax: **!generatelink [user ID]**").queue();
                return;
            }
            Guild guild = message.getJDA().getGuildById(DiscordBot.getSettings().getMasterDiscord());
            assert guild != null;
            User user = member.getJDA().getUserById(id);
            if (user != null) {
                if (guild.isMember(user)) {
                    if (!member.getGuild().isMember(user)) {
                        UUID uuid = DiscordBot.getDatabaseManager().getDiscord(user.getIdLong());
                        if (uuid != null) {
                            Rank rank = DiscordBot.getDatabaseManager().getRank(uuid);
                            List<SubRank> subranks = DiscordBot.getDatabaseManager().getSubRanks(uuid);
                            if (GuildManager.getAllowedRanks(message.getGuild().getIdLong()).contains(rank) || GuildManager.getAllowedSubRanks(message.getGuild().getIdLong()).stream().anyMatch(subranks::contains)) {
                                guild = user.getJDA().getGuildById(message.getGuild().getIdLong());
                                assert guild != null;
                                TextChannel channel = guild.getTextChannelById(GuildManager.getMainChannel(id));
                                assert channel != null;
                                Invite invite = channel.createInvite()
                                        .setMaxAge(0)
                                        .setMaxUses(0)
                                        .setUnique(true)
                                        .complete();

                                PrivateChannel privateChannel = user.openPrivateChannel().complete();
                                privateChannel.sendMessageEmbeds(new EmbedBuilder()
                                        .setAuthor("The AuroraMC Network Leadership Team", "https://auroramc.net", "https://auroramc.net/styles/pie/img/AuroraMCLogoStaffPadded.png")
                                        .setTitle("You've been invited!")
                                        .setDescription("**__You've been invited!__**\n" +
                                                " \n" +
                                                "An admin has generated an invite for you to join the **" + guild.getName() + "** Discord server! Find the invite link below:\n" +
                                                "http://discord.gg/" + invite.getCode() + "\n" +
                                                " \n" +
                                                "This invite is individual to you, and you should not share it with\n" +
                                                "anyone, including your mentor/admin.\n" +
                                                " \n" +
                                                "This Discord invite are only for intended recipients only. Discord\n" +
                                                "invite links and their intended recipient are logged. Any attempt to\n" +
                                                "join from any other account will result in that account being permanently\n" +
                                                "banned from that server and _you_ receive an automatic reprimand.\n" +
                                                "**~AuroraMC Leadership Team**")
                                        .build()).queue();
                                message.reply("Invite link generated!").queue();
                            } else {
                                message.reply("That user does not have the required permissions to join this Discord.").queue();
                            }

                        } else {
                            message.reply("That user does not have an active link, so I cannot create an invite for them!").queue();
                        }
                    } else {
                        message.reply("That user is already in this Discord, so I cannot create an invite for them!").queue();
                    }
                } else {
                    message.reply("That user is not in the main Discord, so I cannot create an invite for them!").queue();
                }
            } else {
                message.reply("I don't know who that user is, so I cannot create an invite for them!").queue();
            }
        } else {
            message.reply("Invalid syntax. Correct syntax: **!generatelink [user ID]**").queue();
        }
    }
}