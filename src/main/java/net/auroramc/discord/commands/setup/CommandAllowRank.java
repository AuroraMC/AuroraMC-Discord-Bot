/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.discord.commands.setup;

import net.auroramc.discord.entities.Command;
import net.auroramc.discord.entities.Permission;
import net.auroramc.discord.entities.Rank;
import net.auroramc.discord.managers.GuildManager;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

import java.util.Collections;
import java.util.List;

public class CommandAllowRank extends Command {
    public CommandAllowRank() {
        super("allowrank", Collections.emptyList(), null, null);
    }

    @Override
    public void execute(Message message, Member member, String aliasUsed, List<String> args) {
        if (args.size() == 1) {
            int id;
            try {
                id = Integer.parseInt(args.get(0));
            } catch (NumberFormatException ignored) {
                message.reply("Invalid syntax. Correct syntax: **!allowrank [rank ID]**").queue();
                return;
            }
            Rank rank = Rank.getByID(id);
            if (rank != null) {
                if (!GuildManager.getAllowedRanks(member.getGuild().getIdLong()).contains(rank)) {
                    GuildManager.addAllowedRank(member.getGuild().getIdLong(), rank);
                    message.reply("Rank is now allowed. To allow them to join, you have to generate links for everyone you need to using !generatelink.").queue();
                } else {
                    message.reply("That rank is already allowed here!").queue();
                }
            } else {
                message.reply("That is not a valid rank ID.").queue();
            }
        } else {
            message.reply("Invalid syntax. Correct syntax: **!allowrank [rank ID]**").queue();
        }
    }
}
