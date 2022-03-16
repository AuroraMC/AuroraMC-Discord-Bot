/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.discord.listeners.message;

import net.auroramc.discord.DiscordBot;
import net.auroramc.discord.commands.CommandLink;
import net.auroramc.discord.managers.CommandManager;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MessageListener extends ListenerAdapter {

    private static final CommandLink link;

    static {
        link = new CommandLink();
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent e) {
        if (e.isFromGuild()) {
            if (e.getMessage().getContentStripped().startsWith(DiscordBot.getSettings().getCommandPrefix() + "")) {
                CommandManager.onCommand(e.getMessage(), e.getMember());
            }
        } else if (e.getMessage().getContentStripped().startsWith("!link")) {
            List<String> args = new ArrayList<>(Arrays.asList(e.getMessage().getContentStripped().split(" ")));
            args.remove(0);
            try {
                link.execute(e.getMessage(), e.getAuthor(), "link", args);
            } catch (Exception ex) {
                ex.printStackTrace();
                e.getMessage().reply("Something went wrong when trying to execute this command, please try again!").queue();
            }
        }
    }
}
