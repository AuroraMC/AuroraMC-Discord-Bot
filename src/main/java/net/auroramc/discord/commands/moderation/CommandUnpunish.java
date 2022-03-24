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

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class CommandUnpunish extends Command {
    public CommandUnpunish() {
        super("unpunish", Collections.singletonList("up"), Collections.singletonList(Permission.MODERATION), Collections.singletonList(956630044225187851L));
    }

    @Override
    public void execute(Message message, Member member, String aliasUsed, List<String> args) {
        if (args.size() >= 2) {
            String code = args.remove(0);
            String reason = String.join(" ", args);

            PunishmentManager.removePunishment(message, code, reason);
        } else {
            message.reply("Invalid syntax. Correct syntax: **!unpunish [code] [reason]**").queue();
        }
    }
}
