/*
 * Copyright (c) 2022-2023 AuroraMC Ltd. All Rights Reserved.
 *
 * PRIVATE AND CONFIDENTIAL - Distribution and usage outside the scope of your job description is explicitly forbidden except in circumstances where a company director has expressly given written permission to do so.
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

public class CommandName extends Command {


    public CommandName() {
        super("name", "Update your Discord nickname to your in-game name!", Collections.emptyList());
    }

    @Override
    public void execute(SlashCommandInteraction message, Member member, Map<String, String> args) {
        message.deferReply(true).queue();
        String name = DiscordBot.getDatabaseManager().getName(member.getIdLong());
        member.modifyNickname(name).queue();
        message.getHook().sendMessage("Your username has been updated!").setEphemeral(true).queue();
    }
}
