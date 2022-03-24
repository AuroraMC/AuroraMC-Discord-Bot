/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.discord.commands.admin;

import net.auroramc.discord.entities.Command;
import net.auroramc.discord.entities.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.Collections;
import java.util.List;

public class CommandPurge extends Command {
    public CommandPurge() {
        super("purge", Collections.emptyList(), Collections.singletonList(Permission.ADMIN), null);
    }

    @Override
    public void execute(Message message, Member member, String aliasUsed, List<String> args) {
        if (args.size() == 1) {
            int amount;
            try {
                amount = Integer.parseInt(args.get(0));
            } catch (NumberFormatException e) {
                message.reply("Invalid syntax. Correct syntax: **!purge [amount]**").queue();
                return;
            }

            if (amount > 100) {
                message.reply("Amount must be less than 100.").queue();
                return;
            }

            message.getTextChannel().getHistory().retrievePast(amount).queue((messages) -> {
                message.getTextChannel().purgeMessages(messages);
            });
        } else {
            message.reply("Invalid syntax. Correct syntax: **!purge [amount]**").queue();
        }
    }
}
