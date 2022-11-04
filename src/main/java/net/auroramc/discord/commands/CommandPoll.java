/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.discord.commands;

import net.auroramc.discord.DiscordBot;
import net.auroramc.discord.entities.Command;
import net.auroramc.discord.util.CommunityPoll;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.*;

public class CommandPoll extends Command {

    public CommandPoll() {
        super("newpoll", "Start a new poll. Answers will be output in order 1-4. If Answer 3 is not specified, Answer 4 will be ignored.", Arrays.asList(new OptionData(OptionType.INTEGER, "Length", "The length, in days, the poll should run for.", true), new OptionData(OptionType.STRING, "Question", "The question you would like to ask.", true), new OptionData(OptionType.STRING, "Answer 1", "The first option", true), new OptionData(OptionType.STRING, "Answer 2", "The second option", true), new OptionData(OptionType.STRING, "Answer 3", "The third option", false), new OptionData(OptionType.STRING, "Answer 4", "The fourth option", false)));
    }

    @Override
    public void execute(SlashCommandInteraction message, Member member, Map<String, String> args) {
        if (DiscordBot.getDatabaseManager().getPoll() != null) {
            message.reply("There is already a poll in progress. Please wait for it to end before starting a new one.").queue();
            return;
        }
        long expire = System.currentTimeMillis() + (Long.parseLong(args.get("Length")) * 86400000);
        String question = args.get("Question");
        List<CommunityPoll.PollAnswer> answers = new ArrayList<>();
        for (int i = 1;i < 5;i++) {
            answers.add(new CommunityPoll.PollAnswer(i, args.get("Answer " + i)));
            i++;
        }
        message.deferReply().queue();
        DiscordBot.getDatabaseManager().newPoll(question, answers, expire);
        message.reply("New poll asking **" + question + "** has been published! Please allow up to 60 minutes for the change to be reflected in-game!").queue();
    }
}
