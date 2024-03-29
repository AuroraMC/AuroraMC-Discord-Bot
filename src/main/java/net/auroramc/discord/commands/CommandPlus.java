/*
 * Copyright (c) 2022-2024 Ethan P-B. All Rights Reserved.
 */

package net.auroramc.discord.commands;

import net.auroramc.discord.DiscordBot;
import net.auroramc.discord.entities.Command;
import net.auroramc.discord.managers.PlusManager;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

public class CommandPlus extends Command {


    public CommandPlus() {
        super("plus", "Check your plus status, and update if you have a new subscription active!", Collections.emptyList());
    }

    @Override
    public void execute(SlashCommandInteraction message, Member member, Map<String, String> args) {
        message.deferReply().queue();
        UUID uuid = DiscordBot.getDatabaseManager().getUUID(member.getIdLong());
        PlusManager.onCommand(member, message, uuid);
    }
}
