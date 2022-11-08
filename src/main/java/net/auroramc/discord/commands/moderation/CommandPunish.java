/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.discord.commands.moderation;

import net.auroramc.discord.DiscordBot;
import net.auroramc.discord.entities.Command;
import net.auroramc.discord.managers.PunishmentManager;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

public class CommandPunish extends Command {
    public CommandPunish() {
        super("punish", "Punish a user.", Arrays.asList(new OptionData(OptionType.NUMBER, "User ID", "The numerical snowflake ID of the user.", true, false), new OptionData(OptionType.INTEGER, "Weight", "The weight of the punishment.", true).addChoice("Light", 1).addChoice("Medium", 2).addChoice("Heavy", 3).addChoice("Severe", 4).addChoice("Extreme", 5), new OptionData(OptionType.STRING, "Reason", "THe reason for the punishment", true)));
    }

    @Override
    public void execute(SlashCommandInteraction message, Member member, Map<String, String> args) {
        message.deferReply().queue();
        long id;
        try {
            id = Long.parseLong(args.get("User ID"));
        } catch (NumberFormatException e) {
            message.reply("Invalid syntax. Correct syntax: **/punish [user ID] [weight] [reason]**").queue();
            return;
        }

        Member target = Objects.requireNonNull(DiscordBot.getJda().getGuildById(DiscordBot.getSettings().getMasterDiscord())).retrieveMemberById(id).complete();

        if (target == null) {
            message.reply("That user is not in the Main Discord.").queue();
            return;
        } else {
            if (!member.canInteract(target)) {
                message.reply("You cannot punish that user.").queue();
                return;
            }
        }

        int weight;
        try {
            weight = Integer.parseInt(args.remove("Weight"));
        } catch (NumberFormatException e) {
            message.reply("Invalid syntax. Correct syntax: **!punish [user ID] [weight 1-5] [reason]**").queue();
            return;
        }

        String reason = args.get("Reason");

        PunishmentManager.punishUser(message, id, weight, reason);
    }
}
