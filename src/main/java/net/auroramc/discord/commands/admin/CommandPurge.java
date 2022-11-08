/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.discord.commands.admin;

import net.auroramc.discord.entities.Command;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Collections;
import java.util.Map;

public class CommandPurge extends Command {
    public CommandPurge() {
        super("purge", "Purge messages from a channel.", Collections.singletonList(new OptionData(OptionType.NUMBER, "amount", "The number of messages to delete.", true)));
    }

    @Override
    public void execute(SlashCommandInteraction message, Member member, Map<String, String> args) {
        int amount;
        try {
            amount = Integer.parseInt(args.get("amount"));
        } catch (NumberFormatException e) {
            message.reply("Invalid syntax. Correct syntax: **/purge [amount]**").queue();
            return;
        }

        if (amount > 100) {
            message.reply("Amount must be less than 100.").queue();
            return;
        }

        message.getGuildChannel().getHistory().retrievePast(amount).queue((messages) -> {
            message.getGuildChannel().purgeMessages(messages);
        });
        message.reply("Purge complete.").setEphemeral(true).queue();
    }
}
