/*
 * Copyright (c) 2022-2024 Ethan P-B. All Rights Reserved.
 */

package net.auroramc.discord.commands;

import net.auroramc.discord.DiscordBot;
import net.auroramc.discord.entities.Command;
import net.auroramc.discord.util.CommunityPoll;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class CommandPoll extends Command {

    public CommandPoll() {
        super("newpoll", "Start a new poll. Answers output in order 1-4. If Answer 3 is not specified, Answer 4 is ignored.", Arrays.asList(new OptionData(OptionType.INTEGER, "length", "The length, in days, the poll should run for.", true), new OptionData(OptionType.STRING, "question", "The question you would like to ask.", true), new OptionData(OptionType.STRING, "answer-1", "The first option", true), new OptionData(OptionType.STRING, "answer-2", "The second option", true), new OptionData(OptionType.STRING, "answer-3", "The third option", false), new OptionData(OptionType.STRING, "answer-4", "The fourth option", false)));
    }

    @Override
    public void execute(SlashCommandInteraction message, Member member, Map<String, String> args) {
        message.deferReply().queue();
        if (DiscordBot.getDatabaseManager().getPoll() != null) {
            message.getHook().sendMessage("There is already a poll in progress. Please wait for it to end before starting a new one.").setEphemeral(true).queue();
            return;
        }
        long expire = System.currentTimeMillis() + (Long.parseLong(args.get("length")) * 86400000);
        String question = args.get("question");
        List<CommunityPoll.PollAnswer> answers = new ArrayList<>();
        for (int i = 1;i < 5;i++) {
            if (!args.containsKey("answer-" + i)) {
                break;
            }
            answers.add(new CommunityPoll.PollAnswer(i, args.get("answer-" + i)));
        }
        message.deferReply().queue();
        DiscordBot.getDatabaseManager().newPoll(question, answers, expire);
        message.getHook().sendMessage("New poll asking **" + question + "** has been published! Please allow up to 60 minutes for the change to be reflected in-game!").queue();
    }
}
