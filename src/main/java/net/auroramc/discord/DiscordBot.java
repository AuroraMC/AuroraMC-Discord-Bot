/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.discord;

import jline.console.ConsoleReader;
import net.auroramc.discord.commands.CommandLink;
import net.auroramc.discord.commands.admin.CommandGenerateLink;
import net.auroramc.discord.commands.admin.CommandPanel;
import net.auroramc.discord.commands.admin.CommandReadMePost;
import net.auroramc.discord.commands.setup.*;
import net.auroramc.discord.entities.BotSettings;
import net.auroramc.discord.entities.Command;
import net.auroramc.discord.listeners.ReadyEventListener;
import net.auroramc.discord.listeners.member.JoinListener;
import net.auroramc.discord.listeners.message.MessageListener;
import net.auroramc.discord.managers.CommandManager;
import net.auroramc.discord.managers.DatabaseManager;
import net.auroramc.discord.managers.GuildManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.md_5.bungee.log.DiscordBotLogger;
import net.md_5.bungee.log.LoggingOutputStream;
import org.fusesource.jansi.AnsiConsole;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

public class DiscordBot {

    private static Logger logger;
    private static DatabaseManager databaseManager;
    private static BotSettings settings;
    private static JDA jda;
    private static boolean shutdown;
    private static final Object object;

    static {
        shutdown = false;
        object = new Object();
    }


    public static void main(String[] args) throws LoginException {
        System.setProperty( "library.jansi.version", "BungeeCord" );

        AnsiConsole.systemInstall();
        ConsoleReader consoleReader;
        try {
            consoleReader = new ConsoleReader();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        consoleReader.setExpandEvents( false );

        logger = new DiscordBotLogger( "Discord Bot", "discordbot.log", consoleReader);
        System.setErr( new PrintStream( new LoggingOutputStream( logger, Level.SEVERE ), true ) );
        System.setOut( new PrintStream( new LoggingOutputStream( logger, Level.INFO ), true ) );
        Thread.currentThread().setName("Main Thread");
        logger.info("Starting AuroraMC Discord Bot...");


        Preferences prefs = Preferences.userNodeForPackage(DiscordBot.class);
        String mysqlHost = prefs.get("mysqlHost", null);
        String mysqlPort = prefs.get("mysqlPort", null);
        String mysqlDb = prefs.get("mysqlDb", null);
        String mysqlUsername = prefs.get("mysqlUsername", null);
        String mysqlPassword = prefs.get("mysqlPassword", null);
        String redisHost = prefs.get("redisHost", null);
        String redisAuth = prefs.get("redisAuth", null);
        String botToken = prefs.get("botToken", null);

        logger.info("Loading Database...");

        databaseManager = new DatabaseManager(mysqlHost, mysqlPort, mysqlDb, mysqlUsername, mysqlPassword, redisHost, redisAuth);
        logger.info("Loading Bot Settings...");
        settings = databaseManager.getSettings();

        logger.info("Loading Commands...");
        //Register commands
        CommandManager.registerCommand(new CommandSetup());
        CommandManager.registerCommand(new CommandGenerateLink());
        CommandManager.registerCommand(new CommandAllowRank());
        CommandManager.registerCommand(new CommandRemoveRank());
        CommandManager.registerCommand(new CommandAllowSubRank());
        CommandManager.registerCommand(new CommandRemoveSubRank());
        CommandManager.registerCommand(new CommandPanel());
        CommandManager.registerCommand(new CommandReadMePost());


        logger.info("Logging in...");
        jda = JDABuilder.create(botToken, GatewayIntent.GUILD_MEMBERS,
                        GatewayIntent.GUILD_BANS,
                        GatewayIntent.GUILD_EMOJIS,
                        GatewayIntent.GUILD_WEBHOOKS,
                        GatewayIntent.GUILD_INVITES,
                        GatewayIntent.GUILD_VOICE_STATES,
                        GatewayIntent.GUILD_PRESENCES,
                        GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.GUILD_MESSAGE_REACTIONS,
                        GatewayIntent.GUILD_MESSAGE_TYPING,
                        GatewayIntent.DIRECT_MESSAGES,
                        GatewayIntent.DIRECT_MESSAGE_REACTIONS,
                        GatewayIntent.DIRECT_MESSAGE_TYPING)
                .setActivity(Activity.playing("auroramc.net"))
                .setStatus(OnlineStatus.ONLINE)
                .addEventListeners(new ReadyEventListener())
                .build();

        jda.addEventListener(new MessageListener());
        jda.addEventListener(new JoinListener());
        GuildManager.load();
        done();
    }

    private static void done() {
        logger.info("Loading complete.");
        String command;
        try {
            while (!shutdown) {
                synchronized (object) {
                    object.wait();
                }
            }
        } catch (InterruptedException e) {
            logger.log(Level.INFO, "Interrupt received, shutting down.", e);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "An error has occurred while trying to process commands. Shutting down. Stack trace: ", e);
        }
        logger.info("Shutting down...");
        shutdown();
    }

    public static void interrupt() {
        shutdown = true;
        object.notifyAll();
    }

    private static void shutdown() {
        jda.shutdownNow();
    }

    public static Logger getLogger() {
        return logger;
    }

    public static DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public static BotSettings getSettings() {
        return settings;
    }
}
