/*
 * Copyright (c) 2022-2024 Ethan P-B. All Rights Reserved.
 */

package net.auroramc.discord.commands.admin;

import net.auroramc.discord.DiscordBot;
import net.auroramc.discord.entities.Command;
import net.auroramc.discord.entities.Rank;
import net.auroramc.discord.entities.SubRank;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CommandPanel extends Command {


    public CommandPanel() {
        super("panel", "Generate a panel 2FA code.", Collections.singletonList(new OptionData(OptionType.USER, "user", "The user to generate a link for.", false, false)));
    }

    @Override
    public void execute(SlashCommandInteraction message, Member member, Map<String, String> args) {
        message.deferReply(true).queue();
        if (args.size() == 0) {
            UUID uuid = DiscordBot.getDatabaseManager().getUUID(member.getIdLong());
            if (uuid != null) {
                String code = RandomStringUtils.randomAlphanumeric(8).toUpperCase();
                message.getHook().sendMessage(code).queue();
                DiscordBot.getDatabaseManager().setPanelCode(uuid, code);
            }
        } else {
            long id;

            try {
                id = Long.parseLong(args.get("user"));
            } catch (NumberFormatException e) {
                message.getHook().sendMessage("That is not a valid ID.").queue();
                return;
            }

            UUID uuid = DiscordBot.getDatabaseManager().getUUID(id);
            if (uuid != null) {
                Rank rank = DiscordBot.getDatabaseManager().getRank(uuid);
                if (rank.getId() < 11) {
                    List<SubRank> subRanks = DiscordBot.getDatabaseManager().getSubRanks(uuid);
                    boolean panel = false;
                    for (SubRank subrank : subRanks) {
                        if (subrank.getId() == 3 || subrank.getId() == 4 || subrank.getId() == 10 || subrank.getId() == 11) {
                            panel = true;
                            break;
                        }
                    }
                    if (!panel) {
                        message.getHook().sendMessage("That user does not have permission to access the panel, so a code was not generated.").queue();
                        return;
                    }
                }

                String code = RandomStringUtils.randomAlphanumeric(8).toUpperCase();
                message.getHook().sendMessage(code).setEphemeral(true).queue();
                DiscordBot.getDatabaseManager().setPanelCode(uuid, code);
            } else {
                message.getHook().sendMessage("That user does not exist.").queue();
            }
        }
    }
}
