/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.discord.commands.admin;

import net.auroramc.discord.DiscordBot;
import net.auroramc.discord.entities.*;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class CommandPanel extends Command {


    public CommandPanel() {
        super("panel", Collections.emptyList(), Collections.singletonList(Permission.PANEL), null);
    }

    @Override
    public void execute(Message message, Member member, String aliasUsed, List<String> args) {
        if (args.size() == 0) {
            UUID uuid = DiscordBot.getDatabaseManager().getDiscord(member.getIdLong());
            if (uuid != null) {
                String code = RandomStringUtils.randomAlphanumeric(8).toUpperCase();
                message.reply(code).queue();
                DiscordBot.getDatabaseManager().setPanelCode(uuid, code);
            }
        } else {
            long id;

            try {
                id = Long.parseLong(args.get(0));
            } catch (NumberFormatException e) {
                message.reply("That is not a valid ID.").queue();
                return;
            }

            UUID uuid = DiscordBot.getDatabaseManager().getDiscord(id);
            if (uuid != null) {
                Rank rank = DiscordBot.getDatabaseManager().getRank(uuid);
                if (!rank.hasPermission("panel")) {
                    List<SubRank> subRanks = DiscordBot.getDatabaseManager().getSubRanks(uuid);
                    boolean panel = false;
                    for (SubRank subrank : subRanks) {
                        if (subrank.hasPermission("panel")) {
                            panel = true;
                            break;
                        }
                    }
                    if (!panel) {
                        message.reply("That user does not have permission to access the panel, so a code was not generated.").queue();
                        return;
                    }
                }

                String code = RandomStringUtils.randomAlphanumeric(8).toUpperCase();
                message.reply(code).queue();
                DiscordBot.getDatabaseManager().setPanelCode(uuid, code);
            } else {
                message.reply("That user does not exist.").queue();
            }
        }
    }
}
