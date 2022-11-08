/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
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
        super("allowrank", "Do not use this command. This command is for use by Block2Block Only. If you fuck with the setup commands I will scream at you.", Collections.singletonList(new OptionData(OptionType.INTEGER, "Rank ID", "The ID of the rank you wsh to allow.", true)));
    }

    @Override
    public void execute(SlashCommandInteraction message, Member member, Map<String, String> args) {
        int id;
        try {
            id = Integer.parseInt(args.get("Rank ID"));
        } catch (NumberFormatException ignored) {
            message.reply("Invalid syntax. Correct syntax: **/allowrank [rank ID]**").queue();
            return;
        }
        Rank rank = Rank.getByID(id);
        if (rank != null) {
            if (!GuildManager.getAllowedRanks(member.getGuild().getIdLong()).contains(rank)) {
                GuildManager.addAllowedRank(member.getGuild().getIdLong(), rank);
                message.reply("Rank is now allowed. To allow them to join, you have to generate links for everyone you need to using /generatelink.").queue();
            } else {
                message.reply("That rank is already allowed here!").queue();
            }
        } else {
            message.reply("That is not a valid rank ID.").queue();
        }
    }
}
