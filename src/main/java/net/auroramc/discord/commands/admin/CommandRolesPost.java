/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
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

public class CommandRolesPost extends Command {


    public CommandRolesPost() {
        super("roles", "Post the roles announcement post.", Collections.emptyList());
    }

    @Override
    public void execute(SlashCommandInteraction message, Member member, Map<String, String> args) {
        MessageEmbed welcome = new EmbedBuilder()
                .setTitle("Notification Roles")
                .setDescription("__**Get notifications for the updates you want!**__\n" +
                        "Use the buttons below to join roles for notifications about the updates you want! Just click on the button to toggle the role!\n" +
                        "\n" +
                        "__General Announcements__\n" +
                        "Get general updates about AuroraMC that aren't specific to a platform!\n" +
                        " \n" +
                        "__Staff Updates__\n" +
                        "Get updates about our joining and departing staff members directly from Staff Management!\n" +
                        " \n" +
                        "__Discord Changelogs__\n" +
                        "Keep up to date about our latest changes and updates to the Discord, Discord bot or rules!\n" +
                        " \n" +
                        "__Server Changelogs__\n" +
                        "Get full and comprehensive changelogs about updates and fixes in-game, and updates on whats currently in the pipeline!\n" +
                        " \n" +
                        "__Event Announcements__\n" +
                        "Get notified when events are happening and join in with all of the fun!\n" +
                        " \n" +
                        "__Ideas Discussion__\n" +
                        "Ideas Discussion is a channel that will allow players to discuss and give their opinion on many different ideas, whether\n" +
                        "that be community ideas or ideas directly suggested by a member of our Leadership Team.\n" +
                        " \n" +
                        "**To get/remove any of the roles, simply click on the buttons below!**")
                .setColor(new Color(0, 170,170))
                .build();
        message.getChannel().sendMessageEmbeds(welcome).setActionRow(Button.primary("roles-956641192433578045", "General Announcements").withEmoji(Emoji.fromFormatted("<:AMCLogo:764501157405130762>")), Button.primary("roles-956641156941369345", "Staff Updates").withEmoji(Emoji.fromFormatted("<:Aurora_Logo_Staff:764501176200331274>")), Button.primary("roles-956641046345953350", "Discord Changelogs").withEmoji(Emoji.fromFormatted("<:discord:956643739474993172>")), Button.primary("roles-956640990410723438", "Server Changelogs").withEmoji(Emoji.fromUnicode("U+1F195")), Button.primary("roles-956642314158235759", "Event Announcements").withEmoji(Emoji.fromUnicode("U+1F389")), Button.primary("roles-1039546744909864980", "Idea Discussion").withEmoji(Emoji.fromUnicode("U+1F4F0"))).queue((msg) -> {}, Throwable::printStackTrace);
        message.reply("Post made.").queue();
    }
}
