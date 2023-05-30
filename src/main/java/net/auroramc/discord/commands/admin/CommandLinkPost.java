/*
 * Copyright (c) 2022-2023 AuroraMC Ltd. All Rights Reserved.
 *
 * PRIVATE AND CONFIDENTIAL - Distribution and usage outside the scope of your job description is explicitly forbidden except in circumstances where a company director has expressly given written permission to do so.
 */

package net.auroramc.discord.commands.admin;

import net.auroramc.discord.entities.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.awt.*;
import java.util.Collections;
import java.util.Map;

public class CommandLinkPost extends Command {


    public CommandLinkPost() {
        super("linkpost", "Post the Link Announcement in your current channel.", Collections.emptyList());
    }

    @Override
    public void execute(SlashCommandInteraction message, Member member, Map<String, String> args) {
        MessageEmbed welcome = new EmbedBuilder()
                .setTitle("Welcome!")
                .setDescription("__**Welcome to the AuroraMC Discord! We hope you will enjoy your time here!**__\n" +
                        "This discord is the official Discord server for the AuroraMC Network! Here, you can connect with other people, take part " +
                        "in events and talk with fellow AuroraMC players!\n" +
                        "\n" +
                        "In order to use our Discord, you must first read through this, then you can link your account to in-game.\n" +
                        "\n" +
                        "In order to take full advantage of this Discord, you must keep your private messages enabled in order for the bot to be able to message " +
                        "you to let you know about punishments, warnings, and other information that may be relevant to you.\n" +
                        " \n" +
                        "Linking your account will be taken as confirmation that you agree to comply with our Terms of Service and Privacy Policy. These can be " +
                        "found at https://auroramc.net/terms and https://auroramc.net/privacy respectively.")
                .setColor(new Color(0, 170,170))
                .build();
        MessageEmbed linking = new EmbedBuilder()
                .setTitle("Linking your account!")
                .setDescription("__**Linking your In-Game Account**__\n" +
                        "In order to get into the Discord fully, we must link your Discord account with your In-Game account!\n" +
                        "\n" +
                        "**NOTE:** You can only link your in-game account with ONE Discord account and vice versa. " +
                        "In order to prevent abuse, the only way for your account to be unlinked from your in-game account is to contact customer support.\n" +
                        " \n" +
                        "Please only link your account when you have read the rules in <#698280487730544721>. Linking your account will be taken as confirmation that " +
                        "you will abide by these rules and punishments will be applied accordingly.\n" +
                        " \n" +
                        "To link your in-game account with your discord account, follow these steps:\n" +
                        "**1)** Log into the network with the Minecraft account you wish to link with.\n" +
                        "**2)** Type /link. This will give you an 8 digit code. This code only lasts 60 seconds.\n" +
                        "**3)** In your DM's with this bot, type /link [8 Digit Code].\n" +
                        "**4)** Your account will be synced!\n" +
                        " \n" +
                        "Once your account is linked, your rank will automatically be applied.")
                .setColor(new Color(0, 170,170))
                .build();
        MessageEmbed plus = new EmbedBuilder()
                .setTitle("A Note About Plus")
                .setDescription("__**Plus Subscriptions**__\n" +
                        "Once your account is linked, you are then able to sync your Plus subscription to your Discord!\n" +
                        " \n" +
                        "In order to do so, all you need to do is run `!plus` and the bot will sync your Plus subscription automatically! " +
                        "You will need to do this every time your plus expires before it is renewed. If you renew before the subscription expires, " +
                        "the bot will automatically take into account the new remaining time of your subscription.\n" +
                        " \n" +
                        "We hope you enjoy your time in the AuroraMC Discord, and have fun!\n" +
                        "~AuroraMC Leadership Team")
                .setColor(new Color(85, 255,255))
                .build();
        message.getChannel().sendMessageEmbeds(welcome, linking, plus).queue();
        message.reply("Post made.").setEphemeral(true).queue();
    }
}
