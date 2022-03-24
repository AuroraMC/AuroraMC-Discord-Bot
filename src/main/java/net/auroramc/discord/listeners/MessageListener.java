/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.discord.listeners;

import net.auroramc.discord.DiscordBot;
import net.auroramc.discord.commands.CommandLink;
import net.auroramc.discord.managers.CommandManager;
import net.auroramc.discord.managers.GuildManager;
import net.auroramc.discord.managers.MessageCache;
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
        if (e.getAuthor().isBot()) {
            return;
        }
        if (e.isFromGuild()) {
            if (e.getMessage().getContentStripped().startsWith(DiscordBot.getSettings().getCommandPrefix() + "")) {
                CommandManager.onCommand(e.getMessage(), e.getMember());
            } else {
                //Spam manager
                String unfiltered = e.getMessage().getContentStripped();
                String filtered = DiscordBot.getFilter().filter(unfiltered);
                if (unfiltered.equals(filtered)) {
                    if (!SpamManager.onMessage(e.getMessage())) {
                        MessageCache.onMessage(e.getMessage());
                    }
                } else {
                    e.getMessage().delete().queue();
                }
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
            Message message = MessageCache.getMessage(event.getMessageIdLong());
            if (message != null) {
                MessageCache.removeMessage(event.getMessageIdLong());
                if (message.getAuthor().isBot()) {
                    return;
                }
                Objects.requireNonNull(event.getGuild().getTextChannelById(GuildManager.getServerLogId(event.getGuild().getIdLong()))).sendMessageEmbeds(
                    new EmbedBuilder()
                            .setTitle("Message Deleted")
                            .setDescription("Message sent <t:" + message.getTimeCreated().toEpochSecond() + ":R> by " + message.getAuthor().getAsMention() + " was deleted in " + message.getChannel().getAsMention() + "\n" +
                                    "**Message: `" + message.getContentStripped() + "`**")
                            .setColor(new Color(0, 170, 170))
                            .build()
                ).queue();
            }
        }
    }

    @Override
    public void onMessageUpdate(@NotNull MessageUpdateEvent event) {
        if (event.isFromGuild() && event.isFromType(ChannelType.TEXT)) {
            if (event.getAuthor().isBot()) {
                return;
            }
            Message message = MessageCache.getMessage(event.getMessageIdLong());
            if (message != null) {
                MessageCache.onMessage(event.getMessage());

                Objects.requireNonNull(event.getGuild().getTextChannelById(GuildManager.getServerLogId(event.getGuild().getIdLong()))).sendMessageEmbeds(
                        new EmbedBuilder()
                                .setTitle("Message Edited")
                                .setDescription("Message sent <t:" + message.getTimeCreated().toEpochSecond() + ":R> by " + message.getAuthor().getAsMention() + " was edited in " + message.getChannel().getAsMention() + "\n" +
                                        "**Old Message: `" + message.getContentStripped() + "`**\n" +
                                        "**New Message: `" + event.getMessage().getContentStripped() + "`")
                                .setColor(new Color(0, 170, 170))
                                .build()
                ).queue();
            }
        }
    }
}
