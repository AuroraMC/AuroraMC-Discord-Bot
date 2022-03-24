/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.discord.managers;

import net.auroramc.discord.DiscordBot;
import net.auroramc.discord.entities.*;
import net.auroramc.discord.util.MySQLConnectionPool;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.User;
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

    public void setPanelCode(UUID uuid, String code) {
        try (Jedis connection = jedis.getResource()) {
            connection.set("panel.code." + uuid.toString(), code);
            connection.expire("panel.code." + uuid, 60);
        }
    }

    public List<RankUpdate> getRankUpdates() {
        try (Connection connection = mysql.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM rank_changes");
            ResultSet set = statement.executeQuery();

            List<RankUpdate> updates = new ArrayList<>();

            while (set.next()) {
                long id = set.getLong(1);
                Rank oldRank = Rank.getByID(set.getInt(2));
                Rank newRank = null;
                if (set.wasNull()) {
                    oldRank = null;
                } else {
                    newRank = Rank.getByID(set.getInt(3));
                }
                SubRank addedSubrank = null;
                SubRank removedSubrank = null;
                String subrank = set.getString(4);
                if (subrank != null) {
                    if (subrank.startsWith("+")) {
                        addedSubrank = SubRank.getByID(Integer.parseInt(subrank.substring(1)));
                    } else {
                        removedSubrank = SubRank.getByID(Integer.parseInt(subrank.substring(1)));
                    }
                }
                updates.add(new RankUpdate(id, oldRank, newRank, addedSubrank, removedSubrank));
            }

            statement = connection.prepareStatement("DELETE FROM rank_changes");
            statement.execute();
            return updates;
        } catch (SQLException e) {
            return new ArrayList<>();
        }
    }

    public long getExpire(UUID player) {
        try (Jedis connection = jedis.getResource()) {
            if (connection.hexists(String.format("plus.%s", player), "expire")) {
                return Long.parseLong(connection.hget(String.format("plus.%s", player), "expire"));
            } else {
                return -1;
            }
        }
    }

    public int getDaysSubscribed(UUID player) {
        try (Jedis connection = jedis.getResource()) {
            if (connection.hexists(String.format("plus.%s", player), "daysSubscribed")) {
                return Integer.parseInt(connection.hget(String.format("plus.%s", player), "daysSubscribed"));
            } else {
                return -1;
            }
        }
    }

    public int getStreak(UUID player) {
        try (Jedis connection = jedis.getResource()) {
            if (connection.hexists(String.format("plus.%s", player), "streak")) {
                return Integer.parseInt(connection.hget(String.format("plus.%s", player), "streak"));
            } else {
                return -1;
            }
        }
    }

    public long getStreakStartTimestamp(UUID player) {
        try (Jedis connection = jedis.getResource()) {
            if (connection.hexists(String.format("plus.%s", player), "streakStart")) {
                return Long.parseLong(connection.hget(String.format("plus.%s", player), "streakStart"));
            } else {
                return -1;
            }
        }
    }

    public int getTotalValidPunishments(long id, int weight) {
        try (Connection connection = mysql.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM dc_punishments WHERE punished = ? AND remover IS NULL AND removal_reason IS NULL AND visible = true AND weight = ?");
            statement.setLong(1, id);
            statement.setInt(2, weight);
            ResultSet set = statement.executeQuery();

            return set.getInt(1);
        } catch (SQLException ignored) {
            return 0;
        }
    }

    public List<Punishment> getPunishmentsVisible(long id) {
        try (Connection connection = mysql.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM dc_punishments WHERE punished = ? AND remover IS NULL AND removal_reason IS NULL AND visible = true");
            statement.setLong(1, id);
            ResultSet set = statement.executeQuery();
            List<Punishment> punishments = new ArrayList<>();
            while (set.next()) {
                punishments.add(new Punishment(set.getString(1), set.getLong(2), set.getBoolean(3), set.getString(4), set.getInt(5), set.getLong(6), set.getLong(7), set.getLong(8), set.getString(9), set.getString(10), set.getLong(11), set.getBoolean(12)));
            }
            return punishments;
        } catch (SQLException ignored) {
            return null;
        }
    }

    public List<Punishment> getAllPunishments(long id) {
        try (Connection connection = mysql.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM dc_punishments WHERE punished = ? AND remover IS NULL AND removal_reason IS NULL");
            statement.setLong(1, id);
            ResultSet set = statement.executeQuery();
            List<Punishment> punishments = new ArrayList<>();
            while (set.next()) {
                punishments.add(new Punishment(set.getString(1), set.getLong(2), set.getBoolean(3), set.getString(4), set.getInt(5), set.getLong(6), set.getLong(7), set.getLong(8), set.getString(9), set.getString(10), set.getLong(11), set.getBoolean(12)));
            }
            return punishments;
        } catch (SQLException ignored) {
            return null;
        }
    }

    public void punishUser(String code, long punished, boolean ban, long issued, long expire, String reason, int weight, long punisher) {
        try (Connection connection = mysql.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO dc_punishments(punishment_code, punished, is_ban, issued, expire, reason, weight, punisher) VALUES (?,?,?,?,?,?,?,?)");
            statement.setString(1, code);
            statement.setLong(2, punished);
            statement.setBoolean(3, ban);
            statement.setLong(4, issued);
            statement.setLong(5, expire);
            statement.setString(6, reason);
            statement.setInt(7, weight);
            statement.setLong(8, punisher);
            statement.execute();
        } catch (SQLException ignored) {
        }
    }

    public ChatFilter loadFilter() {
        try (Jedis connection = jedis.getResource()) {
            List<String> coreWords = new ArrayList<>(connection.smembers("filter.core"));
            List<String> whitelist = new ArrayList<>(connection.smembers("filter.whitelist"));
            List<String> blacklist = new ArrayList<>(connection.smembers("filter.blacklist"));
            List<String> phrases = new ArrayList<>(connection.smembers("filter.phrases"));
            return new ChatFilter(coreWords, blacklist, whitelist, phrases);
        }
    }

}
