/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.discord.commands.admin;

import net.auroramc.discord.entities.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.awt.*;
import java.util.Collections;
import java.util.Map;

public class CommandIdeasPost extends Command {


    public CommandIdeasPost() {
        super("ideas", "Post the ideas announcement post.", Collections.emptyList());
    }

    @Override
    public void execute(SlashCommandInteraction message, Member member, Map<String, String> args) {
        MessageEmbed welcome = new EmbedBuilder()
                .setTitle("Idea Discussion")
                .setDescription("Welcome to the Ideas Discussions channel! If you've stumbled across this channel, it means that you've decided to opt into the @Ideas Discussions role. If you're wondering why you can't type here, it's because there's a thread created underneath each idea for discussions to take place in, as such to avoid discussions about one idea getting drowned out by another. Alongside that, we've created a public trello board with information for each idea so that you're able to see what you're discussing.\n" +
                        "\n" +
                        "Before you get started, we must stress that ideas in here are exactly that and nothing more. This means that whilst we may be discussing an idea here, it may never make its way onto the live network, or may come in a completely different form to the original idea. We hope that you can understand this and we advise that you don't take anything as confirmed unless told to by a member of the AuroraMC Leadership Team.\n" +
                        "\n" +
                        "WARNING: Please let it be known that we may tag the Ideas Discussions role when a new idea is posted to this chat. The reason for this is to ensure that it is seen and discussed by the people who wish to discuss it. If you don't wish to be tagged, we recommend you opt out\n" +
                        "\n" +
                        "Thanks again, and happy discussions!")
                .setColor(new Color(0, 170,170))
                .build();
        message.getChannel().sendMessageEmbeds(welcome).queue((msg) -> {}, Throwable::printStackTrace);
        message.reply("Post made.").setEphemeral(true).queue();
    }
}
