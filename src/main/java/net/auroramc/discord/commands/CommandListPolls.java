/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.discord.commands;

import net.auroramc.discord.DiscordBot;
import net.auroramc.discord.entities.Command;
import net.auroramc.discord.entities.Permission;
import net.auroramc.discord.util.CommunityPoll;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.md_5.bungee.api.ChatColor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CommandListPolls extends Command {

    public CommandListPolls() {
        super("listpolls", Arrays.asList("polls", "getpolls"), Arrays.asList(Permission.ADMIN, Permission.DEBUG_INFO, Permission.COMMUNITY_MANAGEMENT), Collections.singletonList(960543335821504542L));
    }

    @Override
    public void execute(Message message, Member member, String aliasUsed, List<String> args) {
        List<CommunityPoll> polls = DiscordBot.getDatabaseManager().getPolls();

        StringBuilder description = new StringBuilder("The " + polls.size() + " most recent polls are:\n \n");
        List<Button> buttons = new ArrayList<>();

        EmbedBuilder builder = new EmbedBuilder()
                .setTitle("Recent Polls")
                .setColor(ChatColor.DARK_AQUA.getColor());

        String[] emojis = {"1️⃣","2️⃣","3️⃣","4️⃣","5️⃣"};
        for (int i = 0;i < 5 && i < polls.size();i++) {
            CommunityPoll poll = polls.get(i);
            description.append(emojis[i]);
            description.append(" **");
            description.append(poll.getQuestion());
            description.append("**\n");
            buttons.add(Button.primary("poll-" + poll.getId(), "View Results").withEmoji(Emoji.fromUnicode(emojis[i])));
        }

        builder.setDescription(description);
        MessageEmbed messageEmbed = builder.build();
        message.replyEmbeds(messageEmbed).setActionRow(buttons).queue();
    }
}
