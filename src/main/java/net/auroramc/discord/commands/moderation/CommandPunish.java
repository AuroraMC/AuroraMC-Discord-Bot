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

public class CommandPunish extends Command {
    public CommandPunish() {
        super("punish", Collections.singletonList("p"), Collections.singletonList(Permission.MODERATION), Collections.singletonList(956630044225187851L));
    }

    @Override
    public void execute(Message message, Member member, String aliasUsed, List<String> args) {
        if (args.size() >= 3) {
            long id;
            try {
                id = Long.parseLong(args.remove(0));
            } catch (NumberFormatException e) {
                message.reply("Invalid syntax. Correct syntax: **!punish [user ID] [weight 1-5] [reason]**").queue();
                return;
            }

            Member target = Objects.requireNonNull(DiscordBot.getJda().getGuildById(DiscordBot.getSettings().getMasterDiscord())).retrieveMemberById(id).complete();

            if (target == null) {
                message.reply("That user is not in the Main Discord.").queue();
            } else {
                if (!member.canInteract(target)) {
                    message.reply("You cannot punish that user.").queue();
                }
            }

            int weight;
            try {
                weight = Integer.parseInt(args.remove(0));
            } catch (NumberFormatException e) {
                message.reply("Invalid syntax. Correct syntax: **!punish [user ID] [weight 1-5] [reason]**").queue();
                return;
            }

            String reason = String.join(" ", args);

            PunishmentManager.punishUser(message, id, weight, reason);
        } else {
            message.reply("Invalid syntax. Correct syntax: **!punish [user ID] [weight 1-5] [reason]**").queue();
        }
    }
}
