/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.discord.listeners;

import net.auroramc.discord.DiscordBot;
import net.auroramc.discord.commands.CommandLink;
import net.auroramc.discord.managers.CommandManager;
import net.auroramc.discord.managers.GuildManager;
import net.auroramc.discord.managers.SpamManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class MessageListener extends ListenerAdapter {

    private static final CommandLink link;

    static {
        link = new CommandLink();
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent e) {
        if (e.isFromGuild()) {
            if (e.getMessage().getContentStripped().startsWith(DiscordBot.getSettings().getCommandPrefix() + "")) {
                CommandManager.onCommand(e.getMessage(), e.getMember());
            } else {
                //Spam manager
                SpamManager.onMessage(e.getMessage());
            }
        } else if (e.getMessage().getContentStripped().startsWith("!link")) {
            List<String> args = new ArrayList<>(Arrays.asList(e.getMessage().getContentStripped().split(" ")));
            args.remove(0);
            try {
                link.execute(e.getMessage(), e.getAuthor(), args);
            } catch (Exception ex) {
                ex.printStackTrace();
                e.getMessage().reply("Something went wrong when trying to execute this command, please try again!").queue();
            }
        }
    }

    @Override
    public void onMessageDelete(@NotNull MessageDeleteEvent event) {
        if (event.isFromGuild() && event.isFromType(ChannelType.TEXT)) {
            Message message = event.getTextChannel().getHistory().getMessageById(event.getMessageId());
            if (message != null) {
                Objects.requireNonNull(event.getGuild().getTextChannelById(GuildManager.getServerLogId(event.getGuild().getIdLong()))).sendMessageEmbeds(
                    new EmbedBuilder()

                            .build()
                ).queue();
            }

        }
    }

    @Override
    public void onMessageUpdate(@NotNull MessageUpdateEvent event) {
        if (event.isFromGuild()) {

        }
    }
}
