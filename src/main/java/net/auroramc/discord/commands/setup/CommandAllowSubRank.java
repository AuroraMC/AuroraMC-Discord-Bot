/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.discord.commands.setup;

import net.auroramc.discord.entities.Command;
import net.auroramc.discord.entities.SubRank;
import net.auroramc.discord.managers.GuildManager;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Collections;
import java.util.Map;

public class CommandAllowSubRank extends Command {
    public CommandAllowSubRank() {
        super("allowsubrank", "Do not fuck with this command. This command is for use by Block2Block Only. I will scream at you.", Collections.singletonList(new OptionData(OptionType.INTEGER, "subrank-id", "The ID of the Subrank you wsh to allow.", true)));
    }

    @Override
    public void execute(SlashCommandInteraction message, Member member, Map<String, String> args) {
            int id;
            try {
                id = Integer.parseInt(args.get("subrank-id"));
            } catch (NumberFormatException ignored) {
                message.reply("Invalid syntax. Correct syntax: **/allowsubrank [rank ID]**").queue();
                return;
            }
            SubRank rank = SubRank.getByID(id);
            if (rank != null) {
                if (!GuildManager.getAllowedSubRanks(member.getGuild().getIdLong()).contains(rank)) {
                    GuildManager.addAllowedSubRank(member.getGuild().getIdLong(), rank);
                    message.reply("SubRank is now allowed. To allow them to join, you have to generate links for everyone you need to using /generatelink.").queue();
                } else {
                    message.reply("That subrank is already allowed here!").queue();
                }
            } else {
                message.reply("That is not a valid subrank ID.").queue();
            }
    }
}
