/*
 * Copyright (c) 2022-2023 AuroraMC Ltd. All Rights Reserved.
 *
 * PRIVATE AND CONFIDENTIAL - Distribution and usage outside the scope of your job description is explicitly forbidden except in circumstances where a company director has expressly given written permission to do so.
 */

package net.auroramc.discord.commands;

import net.auroramc.discord.DiscordBot;
import net.auroramc.discord.entities.Command;
import net.auroramc.discord.util.CommunityPoll;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.md_5.bungee.api.ChatColor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class CommandListPolls extends Command {

    public CommandListPolls() {
        super("listpolls", "List the 5 most recent polls ran.", Collections.emptyList());
    }

    @Override
    public void execute(SlashCommandInteraction message, Member member, Map<String, String> args) {
        message.deferReply().queue();
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
        if (polls.size() > 0) {
            message.getHook().sendMessageEmbeds(messageEmbed).setActionRow(buttons).queue(message2 -> {
                message2.delete().queueAfter(5, TimeUnit.MINUTES);
            });
        } else {
            message.getHook().sendMessage("There are no recent polls.").queue();
        }

    }
}
