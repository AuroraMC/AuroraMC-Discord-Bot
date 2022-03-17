/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.discord.listeners.member;

import net.auroramc.discord.DiscordBot;
import net.auroramc.discord.managers.LinkManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.List;
import java.util.UUID;

public class JoinListener extends ListenerAdapter {

    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent e) {
        UUID uuid = DiscordBot.getDatabaseManager().getDiscord(e.getMember().getIdLong());
        PrivateChannel channel = e.getUser().openPrivateChannel().complete();
        if (e.getGuild().getIdLong() == DiscordBot.getSettings().getMasterDiscord()) {
            if (uuid != null) {
                LinkManager.onJoin(e.getGuild(), e.getUser(), uuid);
                channel.sendMessageEmbeds(new EmbedBuilder()
                        .setAuthor("The AuroraMC Network Leadership Team", "auroramc.net", "https://auroramc.net/styles/pie/img/AuroraMCLogoStaffPadded.png")
                        .setTitle("Account linked!")
                        .setDescription("__**Welcome back to the AuroraMC Discord!**__\n" +
                                "Because you have joined the Discord before and linked your account, your ranks have been automatically applied!\n" +
                                " \n" +
                                "For security reasons, you cannot unlink your own account.\n" +
                                "If you wish to unlink your account, please contact support.\n" +
                                " \n" +
                                "We hope you enjoy your time in the AuroraMC Discord, and have fun!\n" +
                                "**~AuroraMC Leadership Team**")
                        .setColor(new Color(0, 170,170))
                        .build()).queue();
                LinkManager.processOtherInvites(e.getUser(), channel, uuid);
            } else {
                channel.sendMessageEmbeds(new EmbedBuilder()
                        .setAuthor("The AuroraMC Network Leadership Team", "auroramc.net", "https://auroramc.net/styles/pie/img/AuroraMCLogoStaffPadded.png")
                        .setTitle("Welcome!")
                        .setDescription("__**Welcome to the AuroraMC Discord! We hope you will enjoy your time here!**__\n" +
                                "In order to get started, we must link your Discord account with your In-game account!\n" +
                                "\n" +
                                "**NOTE:** You can only link your in-game account with ONE Discord account and vice versa." +
                                "In order to prevent abuse, the only way for your account to be unlinked from your in-game account is to contact customer support.\n" +
                                "\n" +
                                "We highly recommend you read the rules in #read-me before you link your account, as linking your account will" +
                                "be taken as a confirmation you will abide by these rules and punishments will be applied accordingly.\n" +
                                "\n" +
                                "To link your in-game account with your discord account, follow these steps:\n" +
                                "**1)** Log into the network with the Minecraft account you wish to link with.\n" +
                                "**2)** Type /link. This will give you an 8 digit code. This code only lasts 60 seconds.\n" +
                                "**3)** In this DM, type !link [8 Digit Code].\n" +
                                "**4)** Your account will be synced!\n" +
                                "\n" +
                                "Once your account is linked, your rank will automatically be applied.\n" +
                                "We hope you enjoy your time in the AuroraMC Discord, and have fun!\n" +
                                "~AuroraMC Leadership Team")
                        .setColor(new Color(0, 170,170))
                        .build()).queue();
            }
        } else {
            List<Invite> invites = e.getGuild().retrieveInvites().complete();
            List<String> codes = DiscordBot.getDatabaseManager().getCodes(e.getGuild().getIdLong());
            outer:
            for (String code : codes) {
                for (Invite invite : invites) {
                    if (invite.getCode().equals(code)) {
                        continue outer;
                    }
                }

                //Was not found. This is the invite.
                long userId = DiscordBot.getDatabaseManager().getRecipient(e.getGuild().getIdLong(), code);
                if (userId == e.getUser().getIdLong()) {
                    //This was used the person it should have been, deal with onJoin.
                    LinkManager.onJoin(e.getGuild(), e.getUser(), uuid);
                } else {
                    //This was used by someone it shouldn't have been.
                    e.getGuild().ban(e.getUser(), 0, "Joined through illegal invite link.").queue();
                    LinkManager.onInviteFail(userId, e.getUser(), code, e.getGuild());
                }
                DiscordBot.getDatabaseManager().removeInviteLink(e.getGuild().getIdLong(), code);
                return;
            }
        }
    }
}
