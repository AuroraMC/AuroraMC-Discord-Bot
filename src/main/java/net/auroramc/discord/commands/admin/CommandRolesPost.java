/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.discord.commands.admin;

import net.auroramc.discord.entities.Command;
import net.auroramc.discord.entities.Permission;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.awt.*;
import java.util.Collections;
import java.util.List;

public class CommandRolesPost extends Command {


    public CommandRolesPost() {
        super("roles", Collections.emptyList(), Collections.singletonList(Permission.ADMIN), null);
    }

    @Override
    public void execute(Message message, Member member, String aliasUsed, List<String> args) {
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
                        "**To get/remove any of the roles, simply click on the buttons below!**")
                .setColor(new Color(0, 170,170))
                .build();
        message.getChannel().sendMessageEmbeds(welcome).setActionRow(Button.primary("roles-956641192433578045", "General Announcements").withEmoji(Emoji.fromMarkdown("<:AMCLogo:764501157405130762>")), Button.primary("roles-956641156941369345", "Staff Updates").withEmoji(Emoji.fromMarkdown("<:Aurora_Logo_Staff:764501176200331274>")), Button.primary("roles-956641046345953350", "Discord Changelogs").withEmoji(Emoji.fromMarkdown("<:discord:956643739474993172>")), Button.primary("roles-956640990410723438", "Server Changelogs").withEmoji(Emoji.fromUnicode("U+1F195")), Button.primary("roles-956642314158235759", "Event Announcements").withEmoji(Emoji.fromUnicode("U+1F389"))).queue((msg) -> {}, Throwable::printStackTrace);
        message.delete().queue();
    }
}
