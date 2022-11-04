/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.discord.commands.admin;

import net.auroramc.discord.entities.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.awt.*;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class CommandReadMePost extends Command {


    public CommandReadMePost() {
        super("readme", "Post the Read-Me Announcement in your current channel.", Collections.emptyList());
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
        MessageEmbed light = new EmbedBuilder()
                .setTitle("Light Weight Rules")
                .setDescription("__**Light Weight Rules**__\n" +
                        "**These rules will result in a warning, followed by a temporary Timeout from chatting and voice in Discord.**\n")
                .addField("Trolling", "Intentionally misleading other users.", false)
                .addField("Spamming", "Sending 3 or more of the same or similar message, or spamming random characters.", false)
                .addField("Disrespect", "Being intentionally rude or disrespectful.", false)
                .setColor(new Color(0, 170,0))
                .build();
        MessageEmbed medium = new EmbedBuilder()
                .setTitle("Medium Weight Rules")
                .setDescription("__**Medium Weight Rules**__\n" +
                        "**These rules will result in a temporary Timeout from chatting and voice in Discord.**\n")
                .addField("Inappropriate Behavior", "Sending messages of an adult or inappropriate manner.", false)
                .addField("Level 1 Filter Evasion", "Bypassing a word in our chat filter or in a voice channel when the context does not suggest it is directed at another user or does not include a slur.", false)
                .addField("Staff Trolling", "Intentionally trying to trip up staff members up in conversation.", false)
                .setColor(new Color(85, 255,85))
                .build();
        MessageEmbed heavy = new EmbedBuilder()
                .setTitle("Heavy Weight Rules")
                .setDescription("__**Heavy Weight Rules**__\n" +
                        "**These rules will result in a temporary Timeout from chatting and voice in Discord.**\n")
                .addField("Harassment", "Aggressive pressure or intimidation.", false)
                .addField("Level 2 Filter Evasion", "Bypassing a word in our chat filter or in a voice channel when the context suggests it is directed at another player or includes a slur.", false)
                .setColor(new Color(255, 255,85))
                .build();
        MessageEmbed severe = new EmbedBuilder()
                .setTitle("Severe Weight Rules")
                .setDescription("__**Severe Weight Rules**__\n" +
                        "**These rules will result in a temporary Timeout from chatting and voice in Discord.**\n")
                .addField("Discrimination", "Targeting a specific player or group because of a protected category.", false)
                .addField("Malicious Threats", "Threatening a players safety or integrity.", false)
                .addField("Soundboards/Voice Changers", "Maliciously using soundboards/voice changers in a way that breaks the Discord ToS or is purposefully annoying to hear.", false)
                .setColor(new Color(255, 170,0))
                .build();
        MessageEmbed extreme = new EmbedBuilder()
                .setTitle("Extreme Weight Rules")
                .setDescription("__**Extreme Weight Rules**__\n" +
                        "**These rules will result in a permanent ban from Discord.**\n")
                .addField("Revealing Personal Information", "Maliciously revealing personal information about anyone.", false)
                .addField("Advertising", "Giving IPs or links in chat with the intention of recruiting players.", false)
                .addField("Malicious Links", "Giving dangerous links in chat.", false)
                .addField("Inappropriate Profile", "Having a severely inappropriate about me, status, name or profile picture. _A 5 minute warning " +
                        "will be given for this rule before a ban is issued_.", false)
                .setColor(new Color(170, 0,0))
                .build();
        MessageEmbed linking = new EmbedBuilder()
                .setTitle("Linking your account!")
                .setDescription("__**Linking your In-Game Account**__\n" +
                        "In order to get into the Discord fully, we must link your Discord account with your In-Game account!\n" +
                        "\n" +
                        "**NOTE:** You can only link your in-game account with ONE Discord account and vice versa. " +
                        "In order to prevent abuse, the only way for your account to be unlinked from your in-game account is to contact customer support.\n" +
                        " \n" +
                        "Please only link your account when you have read through this entire channel. Linking your account will be taken as confirmation that " +
                        "you will abide by these rules and punishments will be applied accordingly.\n" +
                        " \n" +
                        "To link your in-game account with your discord account, follow these steps:\n" +
                        "**1)** Log into the network with the Minecraft account you wish to link with.\n" +
                        "**2)** Type /link. This will give you an 8 digit code. This code only lasts 60 seconds.\n" +
                        "**3)** In your DM's with this bot, type !link [8 Digit Code].\n" +
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
        message.getChannel().sendMessageEmbeds(welcome, light, medium, heavy, severe, extreme, linking, plus).setActionRow(Button.link("https://auroramc.net", "Website").withEmoji(Emoji.fromUnicode("U+1F5A5")), Button.link("https://store.auroramc.net", "Store").withEmoji(Emoji.fromUnicode("U+1F6D2")), Button.link("https://auroramc.net/terms", "Terms").withEmoji(Emoji.fromUnicode("U+1F4DD")), Button.link("https://auroramc.net/privacy", "Privacy Policy").withEmoji(Emoji.fromUnicode("U+1F512"))).queue();
        message.reply("Post made.").setEphemeral(true).queue();
    }
}
