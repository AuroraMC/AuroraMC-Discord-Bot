/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.discord.commands.moderation;

import net.auroramc.discord.DiscordBot;
import net.auroramc.discord.entities.Command;
import net.auroramc.discord.entities.Permission;
import net.auroramc.discord.managers.PunishmentManager;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class CommandSlowMode extends Command {
    public CommandSlowMode() {
        super("slowmode", Collections.singletonList("slow"), Collections.singletonList(Permission.MODERATION));
    }

    @Override
    public void execute(Message message, Member member, String aliasUsed, List<String> args) {
        if (args.size() == 1) {
            int id;
            try {
                id = Integer.parseInt(args.get(0));
            } catch (NumberFormatException e) {
                message.reply("Invalid syntax. Correct syntax: **!slowmode [time in seconds]**").queue();
                return;
            }

            if (id > TextChannel.MAX_SLOWMODE) {
                message.reply("Slowmode must be less than 21600.").queue();
                return;
            }

            message.getTextChannel().getManager().setSlowmode(id).queue();
        } else {
            message.reply("Invalid syntax. Correct syntax: **!slowmode [time in seconds]**").queue();
        }
    }
}
