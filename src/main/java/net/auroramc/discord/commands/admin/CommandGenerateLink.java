/*
 * Copyright (c) 2022-2023 AuroraMC Ltd. All Rights Reserved.
 *
 * PRIVATE AND CONFIDENTIAL - Distribution and usage outside the scope of your job description is explicitly forbidden except in circumstances where a company director has expressly given written permission to do so.
 */

package net.auroramc.discord.commands.admin;

import net.auroramc.discord.DiscordBot;
import net.auroramc.discord.entities.Command;
import net.auroramc.discord.entities.Rank;
import net.auroramc.discord.entities.SubRank;
import net.auroramc.discord.managers.GuildManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CommandGenerateLink extends Command {


    public CommandGenerateLink() {
        super("generatelink", "Generate a link to join a Discord.", Collections.singletonList(new OptionData(OptionType.USER, "user", "The user to generate a link for.", true, false)));
    }

    @Override
    public void execute(SlashCommandInteraction message, Member member, Map<String, String> args) {
        long id;
        try {
            id = Long.parseLong(args.get("user"));
        } catch (NumberFormatException e) {
            message.reply("Invalid syntax. Correct syntax: **/generatelink [user ID]**").queue();
            return;
        }
        message.deferReply().queue();
        Guild guild = message.getJDA().getGuildById(DiscordBot.getSettings().getMasterDiscord());
        assert guild != null;
        User user = member.getJDA().getUserById(id);
        if (user != null) {
            if (guild.isMember(user)) {
                if (!member.getGuild().isMember(user)) {
                    UUID uuid = DiscordBot.getDatabaseManager().getUUID(user.getIdLong());
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
                            message.getHook().sendMessage("Invite link generated!").queue();
                        } else {
                            message.getHook().sendMessage("That user does not have the required permissions to join this Discord.").setEphemeral(true).queue();
                        }

                    } else {
                        message.getHook().sendMessage("That user does not have an active link, so I cannot create an invite for them!").setEphemeral(true).queue();
                    }
                } else {
                    message.getHook().sendMessage("That user is already in this Discord, so I cannot create an invite for them!").setEphemeral(true).queue();
                }
            } else {
                message.getHook().sendMessage("That user is not in the main Discord, so I cannot create an invite for them!").setEphemeral(true).queue();
            }
        } else {
            message.getHook().sendMessage("I don't know who that user is, so I cannot create an invite for them!").setEphemeral(true).queue();
        }
    }
}
