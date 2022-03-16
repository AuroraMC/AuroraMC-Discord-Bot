/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.discord.commands;

import net.auroramc.discord.entities.Command;
import net.auroramc.discord.entities.Permission;
import net.dv8tion.jda.api.entities.Member;

import java.util.ArrayList;
import java.util.List;

public class CommandLink extends Command {

    public CommandLink() {
        super("link", new ArrayList<>(), null);
    }

    @Override
    public void execute(Member member, String aliasUsed, List<String> args) {

    }
}
