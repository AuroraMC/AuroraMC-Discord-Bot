/*
 * Copyright (c) 2022-2024 Ethan P-B. All Rights Reserved.
 */

package net.auroramc.discord.commands.admin;

import net.auroramc.discord.entities.Command;
import net.auroramc.discord.managers.PunishmentManager;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Collections;
import java.util.Map;

public class CommandHidePunishment extends Command {

    public CommandHidePunishment() {
        super("hidepunishment", "Hide a punishment from view in the punishment history.", Collections.singletonList(new OptionData(OptionType.STRING, "code", "The Punishment code attached to the Punishment.", true)));
    }

    @Override
    public void execute(SlashCommandInteraction message, Member member, Map<String, String> args) {
        message.deferReply().queue();
        String code = args.remove("code");

        PunishmentManager.hidePunishment(message, code);
    }
}
