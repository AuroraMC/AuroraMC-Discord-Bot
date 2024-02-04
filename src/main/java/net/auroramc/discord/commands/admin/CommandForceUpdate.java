/*
 * Copyright (c) 2022-2024 Ethan P-B. All Rights Reserved.
 */

package net.auroramc.discord.commands.admin;

import net.auroramc.discord.DiscordBot;
import net.auroramc.discord.entities.Command;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;

import java.util.Collections;
import java.util.Map;

public class CommandForceUpdate extends Command {
    public CommandForceUpdate() {
        super("forceupdate", "Force a Discord rank update.", Collections.emptyList());
    }

    @Override
    public void execute(SlashCommandInteraction message, Member member, Map<String, String> args) {
        message.reply("Forced a rank update.").queue();
        DiscordBot.forceRankUpdate();
    }
}
