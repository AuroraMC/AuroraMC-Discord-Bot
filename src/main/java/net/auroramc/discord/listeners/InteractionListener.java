/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.discord.listeners;

import net.auroramc.discord.managers.PunishmentManager;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class InteractionListener extends ListenerAdapter {

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        if (event.getButton().getId() != null) {
            String[] ids = event.getButton().getId().split("-");
            if (ids.length == 4 && ids[0].equals("ph")) {
                int page = Integer.parseInt(ids[1]);
                long id = Long.parseLong(ids[2]);
                long recipient = Long.parseLong(ids[3]);
                PunishmentManager.onPageChange(event.getMessage(), page, id, recipient);
            }
        }
    }
}
