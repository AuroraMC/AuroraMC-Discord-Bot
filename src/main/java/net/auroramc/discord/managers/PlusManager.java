/*
 * Copyright (c) 2022-2023 AuroraMC Ltd. All Rights Reserved.
 *
 * PRIVATE AND CONFIDENTIAL - Distribution and usage outside the scope of your job description is explicitly forbidden except in circumstances where a company director has expressly given written permission to do so.
 */

package net.auroramc.discord.managers;

import net.auroramc.discord.DiscordBot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;

import java.awt.*;
import java.util.Objects;
import java.util.UUID;

public class PlusManager {

    public static void onJoin(User user, UUID uuid) {
        long expire = DiscordBot.getDatabaseManager().getExpire(uuid);
        if (expire != -1) {
            Guild guild = DiscordBot.getJda().getGuildById(DiscordBot.getSettings().getMasterDiscord());
            assert guild != null;
            guild.addRoleToMember(Objects.requireNonNull(guild.getMember(user)), Objects.requireNonNull(guild.getRoleById(955562965355085824L))).queue();
            user.openPrivateChannel().complete().sendMessageEmbeds(new EmbedBuilder()
                    .setAuthor("The AuroraMC Network Leadership Team", "https://auroramc.net", "https://auroramc.net/styles/pie/img/AuroraMCLogoStaffPadded.png")
                    .setTitle("Plus Subscriber!")
                    .setDescription("Because you are an AuroraMC Plus subscriber, you've been given a special role in the Discord!\n" +
                            " \n" +
                            "This gives you access to all sorts of cool features in the Discord, including exclusive commands and channels!\n" +
                            " \n" +
                            "Your perks expire: <t:" + (expire / 1000) + ":f>  (<t:" + (expire / 1000) + ":R>)\n" +
                            " \n" +
                            "Thanks for being a loyal subscriber!\n" +
                            "**~AuroraMC Leadership Team**")
                    .setColor(new Color(255, 170, 0))
                    .build()).queue();
        }
    }

    public static void onCommand(Member member, SlashCommandInteraction message, UUID uuid) {
        long expire = DiscordBot.getDatabaseManager().getExpire(uuid);
        if (expire != -1) {
            Guild guild = DiscordBot.getJda().getGuildById(DiscordBot.getSettings().getMasterDiscord());
            assert guild != null;
            guild.addRoleToMember(member, Objects.requireNonNull(guild.getRoleById(955562965355085824L))).queue();
            message.getHook().sendMessageEmbeds(new EmbedBuilder()
                    .setAuthor("The AuroraMC Network Leadership Team", "https://auroramc.net", "https://auroramc.net/styles/pie/img/AuroraMCLogoStaffPadded.png")
                    .setTitle("Plus Subscriber!")
                    .setDescription("Because you are an AuroraMC Plus subscriber, you've been given a special role in the Discord!\n" +
                            " \n" +
                            "This gives you access to all sorts of cool features in the Discord, including exclusive commands and channels!\n" +
                            " \n" +
                            "Your perks expire: <t:" + (expire / 1000) + ":f>  (<t:" + (expire / 1000) + ":R>)\n" +
                            " \n" +
                            "Thanks for being a loyal subscriber!\n" +
                            "**~AuroraMC Leadership Team**")
                    .setColor(new Color(255, 170, 0))
                    .build()).queue();
        } else {
            message.getHook().sendMessage("You do not have an active Plus subscription. Please allow up to 24 hours for your store purchases to go through.").queue();
        }
    }

    public static void onCheck(Member member) {
        UUID uuid = DiscordBot.getDatabaseManager().getUUID(member.getIdLong());
        long expire = DiscordBot.getDatabaseManager().getExpire(uuid);
        if (expire == -1) {
            Guild guild = DiscordBot.getJda().getGuildById(DiscordBot.getSettings().getMasterDiscord());
            assert guild != null;
            guild.removeRoleFromMember(member, Objects.requireNonNull(guild.getRoleById(955562965355085824L))).queue();
            member.getUser().openPrivateChannel().complete().sendMessageEmbeds(new EmbedBuilder()
                    .setAuthor("The AuroraMC Network Leadership Team", "https://auroramc.net", "https://auroramc.net/styles/pie/img/AuroraMCLogoStaffPadded.png")
                    .setTitle("Your Plus Subscription Expired!")
                    .setDescription("Your Plus Subscription has ended and you no longer have the role in our Discord.\n" +
                            " \n" +
                            "If you decide to join Plus again, don't forge it gives you access to all sorts of cool features in the Discord, including exclusive commands and channels!\n" +
                            " \n" +
                            "Thanks for subscribing, and we hope you will again!\n" +
                            "**~AuroraMC Leadership Team**")
                    .setColor(new Color(255, 170, 0))
                    .build()).queue();
        }
    }

}
