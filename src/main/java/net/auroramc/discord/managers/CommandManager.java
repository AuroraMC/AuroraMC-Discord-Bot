/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.discord.managers;

import net.auroramc.discord.entities.Command;

import java.util.HashMap;
import java.util.Map;

public class CommandManager {

    private static Map<String, Command> commands;

    static {
        commands = new HashMap<>();
    }


    public static void registerCommand(Command command) {
        commands.put(command.getMainCommand().toLowerCase(), command);
        for (String alias : command.getAliases()) {
            commands.put(alias.toLowerCase(), command);
        }
    }
}
