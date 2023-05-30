/*
 * Copyright (c) 2022-2023 AuroraMC Ltd. All Rights Reserved.
 *
 * PRIVATE AND CONFIDENTIAL - Distribution and usage outside the scope of your job description is explicitly forbidden except in circumstances where a company director has expressly given written permission to do so.
 */

package net.auroramc.discord.commands.setup;

import net.auroramc.discord.entities.Command;
import net.auroramc.discord.entities.Rank;
import net.auroramc.discord.managers.GuildManager;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Collections;
import java.util.Map;

public class CommandAllowRank extends Command {
    public CommandAllowRank() {
        super("allowrank", "Do not fuck with this command. This command is for use by Block2Block Only. I will scream at you.", Collections.singletonList(new OptionData(OptionType.INTEGER, "rank-id", "The ID of the rank you wsh to allow.", true)));
    }

    @Override
    public void execute(SlashCommandInteraction message, Member member, Map<String, String> args) {
        int id;
        try {
            id = Integer.parseInt(args.get("rank-id"));
        } catch (NumberFormatException ignored) {
            message.reply("Invalid syntax. Correct syntax: **/allowrank [rank ID]**").queue();
            return;
        }
        Rank rank = Rank.getByID(id);
        if (rank != null) {
            if (!GuildManager.getAllowedRanks(member.getGuild().getIdLong()).contains(rank)) {
                GuildManager.addAllowedRank(member.getGuild().getIdLong(), rank);
                message.reply("Rank is now allowed. To allow them to join, you have to generate links for everyone you need to using /generatelink.").setEphemeral(true).queue();
            } else {
                message.reply("That rank is already allowed here!").setEphemeral(true).queue();
            }
        } else {
            message.reply("That is not a valid rank ID.").setEphemeral(true).queue();
        }
    }
}
