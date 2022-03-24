/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.discord.listeners;

import net.auroramc.discord.managers.PunishmentManager;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class InteractionListener extends ListenerAdapter {

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        if (event.getButton().getId() != null) {
            String[] ids = event.getButton().getId().split("-");
            if (ids.length == 4 && ids[0].equals("ph")) {
                int page = Integer.parseInt(ids[1]);
                long id = Long.parseLong(ids[2]);
                long recipient = Long.parseLong(ids[3]);
                PunishmentManager.onPageChange(event, event.getMessage(), page, id, recipient);
            } else if (ids.length == 2 && ids[0].equals("roles")) {
                Role role = Objects.requireNonNull(event.getGuild()).getRoleById(Long.parseLong(ids[1]));
                if (role != null) {
                    if (Objects.requireNonNull(event.getMember()).getRoles().contains(role)) {
                        event.getGuild().removeRoleFromMember(event.getMember(), role).queue();
                        event.reply("The notification role `" + role.getName() + "` has been removed from you!").setEphemeral(true).queue();
                    } else {
                        event.getGuild().addRoleToMember(event.getMember(), role).queue();
                        event.reply("The notification role `" + role.getName() + "` has been added from you!").setEphemeral(true).queue();
                    }
                } else {
                    event.reply("I couldn't find that role, please let an admin know!").setEphemeral(true).queue();
                }
            }
        }
    }
}
