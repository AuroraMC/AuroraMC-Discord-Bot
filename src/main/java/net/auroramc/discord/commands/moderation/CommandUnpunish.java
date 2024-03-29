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

public class CommandUnpunish extends Command {
    public CommandUnpunish() {
        super("unpunish", "Remove an active ban/timeout from a user.", Arrays.asList(new OptionData(OptionType.STRING, "code", "The Punishment code attached to the Punishment.", true), new OptionData(OptionType.STRING, "reason", "The reason you are removing the punishment.", true)));
    }

    @Override
    public void execute(SlashCommandInteraction message, Member member, Map<String, String> args) {
        message.deferReply().queue();
            String code = args.get("code");
            String reason = args.get("reason");

            PunishmentManager.removePunishment(message, code, reason);
    }
}
