/*
 * Copyright (c) 2022-2023 AuroraMC Ltd. All Rights Reserved.
 *
 * PRIVATE AND CONFIDENTIAL - Distribution and usage outside the scope of your job description is explicitly forbidden except in circumstances where a company director has expressly given written permission to do so.
 */

package net.auroramc.discord.commands.setup;

import net.auroramc.discord.entities.Command;
import net.auroramc.discord.entities.Rank;
import net.auroramc.discord.managers.GuildManager;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Collections;
import java.util.Map;

public class CommandUpdateRoles extends Command {
    public CommandUpdateRoles() {
        super("updateroles", "Do not fuck with this command. This command is for use by Block2Block Only. I will scream at you.", Collections.emptyList());
    }

    @Override
    public void execute(SlashCommandInteraction message, Member member, Map<String, String> args) {
        if (GuildManager.getSetupServers().contains(member.getGuild().getIdLong())) {
            message.deferReply(true).queue();
            GuildManager.updateRoles(message.getGuild());
            message.getHook().sendMessage("Roles for this discord have been updated.").setEphemeral(true).queue();
        } else {
            message.reply("This discord is not setup yet!").setEphemeral(true).queue();
        }
    }
}
