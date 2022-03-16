/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.discord.managers;

import net.auroramc.discord.DiscordBot;
import net.auroramc.discord.entities.BotSettings;
import net.auroramc.discord.util.MySQLConnectionPool;
import net.dv8tion.jda.api.entities.ISnowflake;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.time.Duration;

public class DatabaseManager {

    private final MySQLConnectionPool mysql;
    private final JedisPool jedis;

    public DatabaseManager(String mysqlHost, String mysqlPort, String mysqlDb, String mysqlUsername, String mysqlPassword, String redisHost, String redisAuth) {
        DiscordBot.getLogger().fine("Initialising MySQL and Redis database connection pools...");
        //Setting up MySQL connection pool.
        MySQLConnectionPool mysql1;
        try {
            mysql1 = new MySQLConnectionPool(mysqlHost, mysqlPort, mysqlDb, mysqlUsername, mysqlPassword);
        } catch (ClassNotFoundException e) {
            mysql1 = null;
            e.printStackTrace();
        }
        mysql = mysql1;

        //Setting up Redis connection pool.
        final JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(128);
        config.setMaxIdle(128);
        config.setMinIdle(16);
        config.setTestOnBorrow(true);
        config.setTestOnReturn(true);
        config.setTestWhileIdle(true);
        config.setMinEvictableIdleTimeMillis(Duration.ofSeconds(60).toMillis());
        config.setTimeBetweenEvictionRunsMillis(Duration.ofSeconds(30).toMillis());
        config.setNumTestsPerEvictionRun(3);
        config.setBlockWhenExhausted(true);
        jedis = new JedisPool(config, redisHost, 6379, 2000, redisAuth);
        DiscordBot.getLogger().fine("Database connection pools initialised.");
    }

    public BotSettings getSettings() {
        try (Jedis connection = jedis.getResource()) {
            char commandPrefix = connection.hget("bot.settings", "commandPrefix").charAt(0);
            boolean commandsEnabled = Boolean.parseBoolean(connection.hget("bot.settings", "commandsEnabled"));
            long masterDiscord = Long.getLong(connection.hget("bot.settings", "masterDiscord"));
            return new BotSettings(commandsEnabled, commandPrefix, masterDiscord);
        }
    }

    public void setCommandPrefix(char prefix) {
        try (Jedis connection = jedis.getResource()) {
            connection.hset("bot.settings", "commandPrefix", prefix + "");
        }
    }

    public void setCommandsEnabled(boolean commandsEnabled) {
        try (Jedis connection = jedis.getResource()) {
            connection.hset("bot.settings", "commandsEnabled", commandsEnabled + "");
        }
    }

    public void setMasterDiscord(long masterDiscord) {
        try (Jedis connection = jedis.getResource()) {
            connection.hset("bot.settings", "masterDiscord", masterDiscord + "");
        }
    }



}
