/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.discord.commands.admin;

import net.auroramc.discord.DiscordBot;
import net.auroramc.discord.entities.Command;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;

import java.util.Collections;
import java.util.Map;

public class CommandReloadFilter extends Command {
    public CommandReloadFilter() {
        super("reloadfilter", "Forcefully reload the filter.", Collections.emptyList());
    }

    @Override
    public void execute(SlashCommandInteraction message, Member member, Map<String, String> args) {
        message.reply("Filter updated.").queue();
        DiscordBot.updateFilter();

    }
}
