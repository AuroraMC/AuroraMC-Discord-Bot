/*
 * Copyright (c) 2022-2024 Ethan P-B. All Rights Reserved.
 */

package net.auroramc.discord.commands.moderation;

import net.auroramc.discord.DiscordBot;
import net.auroramc.discord.entities.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;

public class CommandUnlink extends Command {
    public CommandUnlink() {
        super("unlink", "Unlink a user's Minecraft account.", Collections.singletonList(new OptionData(OptionType.STRING, "username", "The Minecraft username of the player you wish to unlink.", true)));
    }

    @Override
    public void execute(SlashCommandInteraction message, Member member, Map<String, String> args) {
            String username = args.remove("username");
            long id = DiscordBot.getDatabaseManager().getDiscordID(username);
            if (id != -1) {
                message.replyEmbeds(
                        new EmbedBuilder()
                                .setTimestamp(Instant.now())
                                .setTitle("Are you sure?")
                                .setDescription("Are you sure you wish to unlink user " + username + "?")
                                .build()
                ).setActionRow(Button.success("unlink-confirm-" + id, "Confirm"), Button.danger("unlink-cancel-" + id, "Cancel")).queue();
            } else {
                message.reply("User not found.").setEphemeral(true).queue();
            }
    }
}
