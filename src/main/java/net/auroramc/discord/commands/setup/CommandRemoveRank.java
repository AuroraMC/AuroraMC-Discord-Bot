/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.discord.commands.setup;

import net.auroramc.discord.entities.Command;
import net.auroramc.discord.entities.Rank;
import net.auroramc.discord.managers.GuildManager;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

import java.util.Collections;
import java.util.List;

public class CommandRemoveRank extends Command {
    public CommandRemoveRank() {
        super("removerank", Collections.emptyList(), null);
    }

    @Override
    public void execute(Message message, Member member, String aliasUsed, List<String> args) {
        if (args.size() == 1) {
            int id;
            try {
                id = Integer.parseInt(args.get(0));
            } catch (NumberFormatException ignored) {
                message.reply("Invalid syntax. Correct syntax: **!removerank [rank ID]**").queue();
                return;
            }
            Rank rank = Rank.getByID(id);
            if (rank != null) {
                if (GuildManager.getAllowedRanks(member.getGuild().getIdLong()).contains(rank)) {
                    GuildManager.removeAllowedRank(member.getGuild().getIdLong(), rank);
                    message.reply("Rank is no longer allowed.").queue();
                } else {
                    message.reply("That rank is already disallowed here!").queue();
                }
            } else {
                message.reply("That is not a valid rank ID.").queue();
            }
        } else {
            message.reply("Invalid syntax. Correct syntax: **!removerank [rank ID]**").queue();
        }
    }
}
