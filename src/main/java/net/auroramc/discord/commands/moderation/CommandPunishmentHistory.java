/*
 * Copyright (c) 2022-2023 AuroraMC Ltd. All Rights Reserved.
 *
 * PRIVATE AND CONFIDENTIAL - Distribution and usage outside the scope of your job description is explicitly forbidden except in circumstances where a company director has expressly given written permission to do so.
 */

package net.auroramc.discord.commands.moderation;

import net.auroramc.discord.entities.Command;
import net.auroramc.discord.managers.PunishmentManager;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Collections;
import java.util.Map;

public class CommandPunishmentHistory extends Command {


    public CommandPunishmentHistory() {
        super("punishmenthistory", "View the punishment history of a player.", Collections.singletonList(new OptionData(OptionType.USER, "user", "The user who's punishment history you wish to view.", true)));
    }

    @Override
    public void execute(SlashCommandInteraction message, Member member, Map<String, String> args) {
        message.deferReply().queue();
        long id;
        try {
            id = Long.parseLong(args.get("user"));
        } catch (NumberFormatException e) {
            message.reply("Invalid syntax. Correct syntax: **/ph [user ID]**").setEphemeral(true).queue();
            return;
        }
        PunishmentManager.getPunishmentHistory(member, message, id);
    }
}
