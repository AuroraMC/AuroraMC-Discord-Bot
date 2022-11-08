/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.discord.managers;

import net.auroramc.discord.DiscordBot;
import net.auroramc.discord.commands.CommandLink;
import net.auroramc.discord.entities.Command;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.*;

public class CommandManager {

    private static final Map<String, Command> commands;
    private static final CommandLink link = new CommandLink();

    static {
        commands = new HashMap<>();
    }

    public static void onCommand(SlashCommandInteraction message, User user) {
        DiscordBot.getLogger().fine("Command \"" + message.getName() + "\" was executed.");
        String commandLabel = message.getName().split(" ")[0].toLowerCase().substring(1);
        if (commandLabel.equals("link")) {
            Map<String, String> args = new HashMap<>();
            for (OptionMapping mapping : message.getOptions()) {
                args.put(mapping.getName(), mapping.getAsString());
            }
            link.execute(message, user, args);
            return;
        }
        Command command = commands.get(commandLabel);
        if (command != null && GuildManager.getRankMappings(message.getGuild().getIdLong()) != null) {
            Map<String, String> args = new HashMap<>();
            for (OptionMapping mapping : message.getOptions()) {
                if (mapping.getType() == OptionType.USER) {
                    args.put(mapping.getName(), mapping.getAsUser().getIdLong() + "");
                    continue;
                }
                args.put(mapping.getName(), mapping.getAsString());
            }
            command.execute(message, message.getGuild().getMember(user), args);
        }
    }


    public static void registerCommand(Command command) {
        commands.put(command.getMainCommand().toLowerCase(), command);
    }

    public static void loadCommands(JDA jda) {
        List<SlashCommandData> data = new ArrayList<>();
        for (Command command : commands.values()) {
            data.add(command.getAsSlashCommandData());
        }
        //This is a global command, so init first.
        jda.updateCommands().addCommands(Commands.slash("link", "Link your Minecraft Account to your Discord account").setDefaultPermissions(DefaultMemberPermissions.ENABLED).addOptions(new OptionData(OptionType.STRING, "code", "The 8-digit confirmation code produced by /link in-game.", true))).queue();
        for (long id : GuildManager.getSetupServers()) {
            Objects.requireNonNull(jda.getGuildById(id)).updateCommands().addCommands(data).queue();
        }

        Objects.requireNonNull(jda.getGuildById(DiscordBot.getSettings().getMasterDiscord())).updateCommands().addCommands(data).queue();

    }

    public static void loadCommands(Guild guild) {
        List<SlashCommandData> data = new ArrayList<>();
        for (Command command : commands.values()) {
            data.add(command.getAsSlashCommandData());
        }
        guild.updateCommands().addCommands(data).queue();

    }
}
