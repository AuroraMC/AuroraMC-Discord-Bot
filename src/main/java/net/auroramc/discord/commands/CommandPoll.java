/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.discord.commands;

import net.auroramc.discord.DiscordBot;
import net.auroramc.discord.entities.Command;
import net.auroramc.discord.entities.Permission;
import net.auroramc.discord.managers.DatabaseManager;
import net.auroramc.discord.managers.PunishmentManager;
import net.auroramc.discord.util.CommunityPoll;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CommandPoll extends Command {

    public CommandPoll() {
        super("newpoll", Arrays.asList("poll", "addpoll"), Arrays.asList(Permission.ADMIN, Permission.DEBUG_INFO, Permission.COMMUNITY_MANAGEMENT), null);
    }

    @Override
    public void execute(Message message, Member member, String aliasUsed, List<String> args) {
        String arg = String.join(" ", args);
        args = new ArrayList<>(Arrays.asList(arg.split(";")));
        if (args.size() >= 4 && args.size() < 6) {
            if (DiscordBot.getDatabaseManager().getPoll() != null) {
                message.reply("There is already a poll in progress. Please wait for it to end before starting a new one.").queue();
                return;
            }
            long expire = System.currentTimeMillis() + (Long.parseLong(args.remove(0)) * 86400000);
            String question = args.remove(0);
            List<CommunityPoll.PollAnswer> answers = new ArrayList<>();
            int i = 1;
            for (String answer : args) {
                answers.add(new CommunityPoll.PollAnswer(i, answer));
                i++;
            }

            DiscordBot.getDatabaseManager().newPoll(question, answers, expire);
            message.reply("New poll asking **" + question + "** has been published! Please allow up to 60 minutes for the change to be reflected in-game!").queue();
            return;
        } else {
            message.reply("Invalid syntax. Correct syntax: **!newpoll [length in days];[question];[answer 1];[answer 2];...**\n" +
                    "You may provide up to 4 answers. They will be displayed in the order you specify them.").queue();
        }
    }
}
