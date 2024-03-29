/*
 * Copyright (c) 2022-2024 Ethan P-B. All Rights Reserved.
 */

package net.auroramc.discord.commands.moderation;

import net.auroramc.discord.entities.Command;
import net.auroramc.discord.managers.PunishmentManager;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Arrays;
import java.util.Map;

public class CommandEvidence extends Command {
    public CommandEvidence() {
        super("evidence", "Attach evidence to a punishment.", Arrays.asList(new OptionData(OptionType.STRING, "code", "The Punishment code attached to the Punishment.", true), new OptionData(OptionType.STRING, "evidence", "Any evidence you have to attach to the punishment", true)));
    }

    @Override
    public void execute(SlashCommandInteraction message, Member member, Map<String, String> args) {
        message.deferReply().queue();
        String code = args.get("code");
        String evidence = args.get("evidence");

        PunishmentManager.attachEvidence(message, code, evidence);
    }
}
