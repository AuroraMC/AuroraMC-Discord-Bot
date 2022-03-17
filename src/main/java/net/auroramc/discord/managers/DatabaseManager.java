/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.discord.managers;

import net.auroramc.discord.DiscordBot;
import net.auroramc.discord.entities.BotSettings;
import net.auroramc.discord.entities.Rank;
import net.auroramc.discord.entities.SubRank;
import net.auroramc.discord.util.MySQLConnectionPool;
import net.dv8tion.jda.api.entities.ISnowflake;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.*;

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
            long masterDiscord = Long.parseLong(connection.hget("bot.settings", "masterDiscord"));
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

    public Map<Long, Map<Rank, Long>> getRankMappings() {
        try (Connection connection = mysql.getConnection()) {
            Map<Long, Map<Rank, Long>> mappings = new HashMap<>();
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM dc_rank_mappings");
            ResultSet set = statement.executeQuery();
            while (set.next()) {
                if (mappings.containsKey(set.getLong(1))) {
                    mappings.get(set.getLong(1)).put(Rank.getByID(set.getInt(2)), set.getLong(3));
                } else {
                    mappings.put(set.getLong(1), new HashMap<>());
                    mappings.get(set.getLong(1)).put(Rank.getByID(set.getInt(2)), set.getLong(3));
                }
            }
            return mappings;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Map<Long, Map<SubRank, Long>> getSubRankMappings() {
        try (Connection connection = mysql.getConnection()) {
            Map<Long, Map<SubRank, Long>> mappings = new HashMap<>();
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM dc_subrank_mappings");
            ResultSet set = statement.executeQuery();
            while (set.next()) {
                if (mappings.containsKey(set.getLong(1))) {
                    mappings.get(set.getLong(1)).put(SubRank.getByID(set.getInt(2)), set.getLong(3));
                } else {
                    mappings.put(set.getLong(1), new HashMap<>());
                    mappings.get(set.getLong(1)).put(SubRank.getByID(set.getInt(2)), set.getLong(3));
                }
            }
            return mappings;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void addRankMappings(long guildId, Map<Rank, Long> rankMappings, Map<SubRank, Long> subrankMappings) {
        try (Connection connection = mysql.getConnection()) {
            for (Map.Entry<Rank, Long> entry : rankMappings.entrySet()) {
                PreparedStatement statement = connection.prepareStatement("INSERT INTO dc_rank_mappings(guild_id, rank_id, role_id) VALUES (?,?,?)");
                statement.setLong(1, guildId);
                statement.setInt(2, entry.getKey().getId());
                statement.setLong(3, entry.getValue());
                statement.execute();
            }

            for (Map.Entry<SubRank, Long> entry : subrankMappings.entrySet()) {
                PreparedStatement statement = connection.prepareStatement("INSERT INTO dc_subrank_mappings(guild_id, subrank_id, role_id) VALUES (?,?,?)");
                statement.setLong(1, guildId);
                statement.setInt(2, entry.getKey().getId());
                statement.setLong(3, entry.getValue());
                statement.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getUserFromCode(String code) {
        try (Jedis connection = jedis.getResource()) {
            if (connection.exists("discord.link." + code)) {
                return connection.get("discord.link." + code);
            }
            return null;
        }
    }

    public void addLink(UUID uuid, long discordId) {
        try (Connection connection = mysql.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO dc_links(amc_id, discord_id) VALUES ((SELECT id FROM auroramc_players WHERE uuid = ?),?)");
            statement.setString(1, uuid.toString());
            statement.setLong(2, discordId);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Rank getRank(UUID uuid) {
        try (Connection connection = mysql.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT `rank` FROM `ranks` WHERE amc_id = (SELECT id FROM auroramc_players WHERE uuid = ?)");
            statement.setString(1, uuid.toString());

            ResultSet set = statement.executeQuery();
            if (set.next()) {
                return Rank.getByID(set.getInt(1));
            } else {
                //NEW USER
                return Rank.getByID(0);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ArrayList<SubRank> getSubRanks(UUID uuid) {
        try (Connection connection = mysql.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT subranks FROM `ranks` WHERE amc_id = (SELECT id FROM auroramc_players WHERE uuid = ?)");
            statement.setString(1, uuid.toString());

            ResultSet set = statement.executeQuery();
            if (set.next()) {
                if (set.getString(1) == null) {
                    return new ArrayList<>();
                }
                String[] ranks = set.getString(1).split(",");
                Arrays.sort(ranks);
                ArrayList<SubRank> subRanks = new ArrayList<>();
                for (String rank : ranks) {
                    subRanks.add(SubRank.getByID(Integer.parseInt(rank)));
                }
                return subRanks;
            } else {
                //NEW USER
                return new ArrayList<>();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Rank> getAllowedRanks(long guildId) {
        try (Jedis connection = jedis.getResource()) {
            Set<String> rankStrings = connection.smembers("discord.ranks." + guildId);
            List<Rank> ranks = new ArrayList<>();
            for (String rankString : rankStrings) {
                ranks.add(Rank.getByID(Integer.parseInt(rankString)));
            }
            return ranks;
        }
    }

    public List<Long> getRegisteredGuilds() {
        try (Jedis connection = jedis.getResource()) {
            Set<String> rankStrings = connection.smembers("discord.guilds");
            List<Long> ranks = new ArrayList<>();
            for (String rankString : rankStrings) {
                ranks.add(Long.parseLong(rankString));
            }
            return ranks;
        }
    }

    public void addSetupServer(long guildId) {
        try (Jedis connection = jedis.getResource()) {
            connection.sadd("discord.guilds", guildId + "");
        }
    }

    public void removeSetupServer(long guildId) {
        try (Jedis connection = jedis.getResource()) {
            connection.srem("discord.guilds", guildId + "");
            connection.del("discord.ranks." + guildId);
            connection.del("discord.subranks." + guildId);
        }
    }

    public void addAllowedRank(long guildId, int rankId) {
        try (Jedis connection = jedis.getResource()) {
            connection.sadd("discord.ranks." + guildId, rankId + "");
        }
    }

    public void removeAllowedRank(long guildId, int rankId) {
        try (Jedis connection = jedis.getResource()) {
            connection.srem("discord.ranks." + guildId, rankId + "");
        }
    }

    public void addAllowedSubRank(long guildId, int subrankId) {
        try (Jedis connection = jedis.getResource()) {
            connection.sadd("discord.subranks." + guildId, subrankId + "");
        }
    }

    public void removeAllowedSubRank(long guildId, int subrankId) {
        try (Jedis connection = jedis.getResource()) {
            connection.srem("discord.subranks." + guildId, subrankId + "");
        }
    }

    public List<SubRank> getAllowedSubRanks(long guildId) {
        try (Jedis connection = jedis.getResource()) {
            Set<String> rankStrings = connection.smembers("discord.subranks." + guildId);
            List<SubRank> ranks = new ArrayList<>();
            for (String rankString : rankStrings) {
                ranks.add(SubRank.getByID(Integer.parseInt(rankString)));
            }
            return ranks;
        }
    }

    public Long getLoggingChannel(long guildId) {
        try (Jedis connection = jedis.getResource()) {
            return Long.parseLong(connection.hget("discord.loggingChannel", guildId + ""));
        }
    }

    public Long getLinkingChannel(long guildId) {
        try (Jedis connection = jedis.getResource()) {
            return Long.parseLong(connection.hget("discord.linkingChannel", guildId + ""));
        }
    }

    public void setLoggingChannel(long guildId, long channelId) {
        try (Jedis connection = jedis.getResource()) {
            connection.hset("discord.loggingChannel", guildId + "", channelId + "");
        }
    }

    public void setLinkingChannel(long guildId, long channelId) {
        try (Jedis connection = jedis.getResource()) {
            connection.hset("discord.linkingChannel", guildId + "", channelId + "");
        }
    }

    public Long getMainChannel(long guildId) {
        try (Jedis connection = jedis.getResource()) {
            return Long.parseLong(connection.hget("discord.mainChannel", guildId + ""));
        }
    }

    public void setMainChannel(long guildId, long channelId) {
        try (Jedis connection = jedis.getResource()) {
            connection.hset("discord.mainChannel", guildId + "", channelId + "");
        }
    }

    public void addInviteLink(long guildId, long userId, String code) {
        try (Jedis connection = jedis.getResource()) {
            connection.hset("discord.inviteLinks." + guildId, code, userId + "");
        }
    }

    public void removeInviteLink(long guildId, String code) {
        try (Jedis connection = jedis.getResource()) {
            connection.hdel("discord.inviteLinks." + guildId, code);
        }
    }

    public List<String> getCodes(long guildId) {
        try (Jedis connection = jedis.getResource()) {
            return new ArrayList<>(connection.hkeys("discord.inviteLinks." + guildId));
        }
    }

    public long getRecipient(long guildId, String code) {
        try (Jedis connection = jedis.getResource()) {
            return Long.parseLong(connection.hget("discord.inviteLinks." + guildId, code));
        }
    }

    public UUID getDiscord(long id) {
        try (Connection connection = mysql.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT `uuid` FROM auroramc_players WHERE id = (SELECT amc_id FROM dc_links WHERE discord_id = ?)");
            statement.setLong(1, id);

            ResultSet set = statement.executeQuery();

            if (set.next()) {
                return UUID.fromString(set.getString(1));
            }
            return null;
        } catch (SQLException ignored) {
            return null;
        }
    }

}
