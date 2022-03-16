/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.discord.managers;

import net.auroramc.discord.entities.Command;
import net.auroramc.discord.entities.Permission;
import net.auroramc.discord.entities.Rank;
import net.auroramc.discord.entities.SubRank;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import org.omg.CORBA.LongHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CommandManager {

    private static final Map<String, Command> commands;

    static {
        commands = new HashMap<>();
    }

    public static void onCommand(Message message, Member user) {
        ArrayList<String> args = new ArrayList<>(Arrays.asList(message.getContentStripped().split(" ")));
        String commandLabel = args.remove(0).substring(1);
        Command command = commands.get(commandLabel.toLowerCase());
        if (command != null) {
            if (GuildManager.getRankMappings(message.getGuild().getIdLong()) == null) {
                return;
            }
            for (Permission permission : command.getPermission()) {
                if (hasPermission(user, permission)) {
                    try {
                        command.execute(message, user, commandLabel, args);
                    } catch (Exception e) {
                        e.printStackTrace();
                        message.reply("Something went wrong when trying to execute this command, please try again!").queue();
                    }
                    return;
                }
            }
        }
    }

    private static boolean hasPermission(Member user, Permission permission) {
        Map<Rank, Long> rankMappings = GuildManager.getRankMappings(user.getGuild().getIdLong());
        Map<SubRank, Long> subrankMappings = GuildManager.getSubrankMappings(user.getGuild().getIdLong());
        for (Role role : user.getRoles()) {
            for (Map.Entry<Rank, Long> entry : rankMappings.entrySet()) {
                if (entry.getValue().equals(role.getIdLong())) {
                    if (entry.getKey().hasPermission(permission.getId())) {
                        return true;
                    }
                }
            }
            for (Map.Entry<SubRank, Long> entry : subrankMappings.entrySet()) {
                if (entry.getValue().equals(role.getIdLong())) {
                    if (entry.getKey().hasPermission(permission.getId())) {
                        return true;
                    }
                }
            }
        }

        return false;
    }


    public static void registerCommand(Command command) {
        commands.put(command.getMainCommand().toLowerCase(), command);
        for (String alias : command.getAliases()) {
            commands.put(alias.toLowerCase(), command);
        }
    }
}
