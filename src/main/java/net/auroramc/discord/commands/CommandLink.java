/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.discord.commands;

import net.auroramc.discord.DiscordBot;
import net.auroramc.discord.managers.LinkManager;
import net.auroramc.discord.managers.PlusManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;

import java.awt.*;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class CommandLink {

    public void execute(Message message, User user, List<String> args) {
        if (user.getMutualGuilds().size() > 0) {
            for (Guild guild : user.getMutualGuilds()) {
                if (guild.getIdLong() == DiscordBot.getSettings().getMasterDiscord()) {
                    //Only run the command if they're in the main discord.
                    Member member = guild.getMember(user);
                    assert member != null;
                    boolean unverified = false;
                    boolean found = false;
                    for (Role role : member.getRoles()) {
                        if (role.getIdLong() == 886329879002505217L) {
                            unverified = true;
                            found = true;
                            break;
                        } else if (role.getIdLong() == 886329856873332768L) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        guild.addRoleToMember(member, Objects.requireNonNull(guild.getRoleById(886329879002505217L))).queue();
                        unverified = true;
                    }
                    if (unverified) {
                        //They have unlinked, now go through the linking process.
                        if (args.size() == 1) {
                            String uuidstr = DiscordBot.getDatabaseManager().getUserFromCode(args.get(0));
                            if (uuidstr != null) {
                                UUID uuid = UUID.fromString(uuidstr);
                                DiscordBot.getDatabaseManager().addLink(uuid, user.getIdLong());

                                //Now link is done, hand over to the link manager to deal with it.
                                LinkManager.onLink(user, uuid);
                                message.replyEmbeds(new EmbedBuilder()
                                        .setAuthor("The AuroraMC Network Leadership Team", "https://auroramc.net", "https://auroramc.net/styles/pie/img/AuroraMCLogoStaffPadded.png")
                                        .setTitle("Account linked!")
                                        .setDescription("__**Your account has been successfully linked!**__\n" +
                                                "Your rank has been automatically applied to your account!\n" +
                                                " \n" +
                                                "For security reasons, you cannot unlink your own account.x" +
                                                "If you wish to unlink your account, please contact support.\n" +
                                                " \n" +
                                                "We hope you enjoy your time in the AuroraMC Discord, and have fun!\n" +
                                                "**~AuroraMC Leadership Team**")
                                        .setColor(new Color(0, 170, 170))
                                        .build()).queue();
                                LinkManager.processOtherInvites(user, message, uuid);
                                PlusManager.onJoin(user, uuid);
                            } else {
                                message.reply("You provided an invalid code, please try again!").queue();
                            }
                        } else {
                            message.reply("Invalid arguments. Correct arguments: **!link [8 digit code]**").queue();
                        }
                    }
                }
            }
        }
    }
}
