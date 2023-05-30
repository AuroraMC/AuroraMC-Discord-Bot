/*
 * Copyright (c) 2022-2023 AuroraMC Ltd. All Rights Reserved.
 *
 * PRIVATE AND CONFIDENTIAL - Distribution and usage outside the scope of your job description is explicitly forbidden except in circumstances where a company director has expressly given written permission to do so.
 */

package net.auroramc.discord.listeners;

import net.auroramc.discord.DiscordBot;
import net.auroramc.discord.commands.CommandLink;
import net.auroramc.discord.managers.CommandManager;
import net.auroramc.discord.managers.GuildManager;
import net.auroramc.discord.managers.MessageCache;
import net.auroramc.discord.managers.SpamManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
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
            //Spam manager
            String unfiltered = e.getMessage().getContentStripped();
            String filtered = DiscordBot.getFilter().filter(unfiltered);
            if (unfiltered.equals(filtered)) {
                if (!SpamManager.onMessage(e.getMessage())) {
                    MessageCache.onMessage(e.getMessage());
                }
            } else {
                e.getMessage().delete().queue();
                Objects.requireNonNull(e.getGuild().getTextChannelById(GuildManager.getServerLogId(e.getGuild().getIdLong()))).sendMessageEmbeds(
                        new EmbedBuilder()
                                .setTitle("Message Deleted")
                                .setDescription("Message sent <t:" + e.getMessage().getTimeCreated().toEpochSecond() + ":R> by " + e.getMessage().getAuthor().getAsMention() + " was deleted in " + e.getMessage().getChannel().getAsMention() + " due to a filtered word.\n" +
                                        "**Message: `" + e.getMessage().getContentStripped() + "`**")
                                .setColor(new Color(0, 170, 170))
                                .build()
                ).queue();
            }
            if (e.getChannelType() == ChannelType.NEWS) {
                e.getMessage().crosspost().queue();
            }

        }
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        CommandManager.onCommand(event, event.getUser());
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
                String unfiltered = event.getMessage().getContentStripped();
                String filtered = DiscordBot.getFilter().filter(unfiltered);
                if (!filtered.equals(unfiltered)) {
                    event.getMessage().delete().queue();
                    Objects.requireNonNull(event.getGuild().getTextChannelById(GuildManager.getServerLogId(event.getGuild().getIdLong()))).sendMessageEmbeds(
                            new EmbedBuilder()
                                    .setTitle("Message Deleted")
                                    .setDescription("Message sent <t:" + event.getMessage().getTimeCreated().toEpochSecond() + ":R> by " + event.getMessage().getAuthor().getAsMention() + " was deleted in " + event.getMessage().getChannel().getAsMention() + " due to a filtered word.\n" +
                                            "**Message:** `" + event.getMessage().getContentStripped() + "`**")
                                    .setColor(new Color(0, 170, 170))
                                    .build()
                    ).queue();
                }

                Objects.requireNonNull(event.getGuild().getTextChannelById(GuildManager.getServerLogId(event.getGuild().getIdLong()))).sendMessageEmbeds(
                        new EmbedBuilder()
                                .setTitle("Message Edited")
                                .setDescription("Message sent <t:" + message.getTimeCreated().toEpochSecond() + ":R> by " + message.getAuthor().getAsMention() + " was edited in " + message.getChannel().getAsMention() + "\n" +
                                        "**Old Message:** `" + message.getContentStripped() + "`\n" +
                                        "**New Message:** `" + event.getMessage().getContentStripped() + "`")
                                .setColor(new Color(0, 170, 170))
                                .build()
                ).queue();
            }
        }
    }
}
