/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.discord.entities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChatFilter {

    private final List<String> coreFilteredWords;
    private final List<String> wordWhitelist;
    private final List<String> wordBlacklist;
    private final List<String> bannedPhrases;

    public ChatFilter(List<String> coreFilteredWords, List<String> wordBlacklist, List<String> wordWhitelist, List<String> bannedPhrases) {
        this.coreFilteredWords = coreFilteredWords;
        this.wordBlacklist = wordBlacklist;
        this.wordWhitelist = wordWhitelist;
        this.bannedPhrases = bannedPhrases;
    }

    public String filter(String message) {
        List<String> splitMessage = new ArrayList<>(Arrays.asList(message.split(" ")));
        List<String> finalMessage = new ArrayList<>();

        if (splitMessage.size() == 1) {
            return filterWord(message);
        }

        pairs:
        for (String word : splitMessage) {
            finalMessage.add(filterWord(word));
        }

        String finalJoinedMessage = String.join(" ", finalMessage);

        for (String phrase : bannedPhrases) {
            if (finalJoinedMessage.toLowerCase().contains(phrase)) {
                finalJoinedMessage = finalJoinedMessage.replace(phrase, "HONK!");
            }
        }

        return finalJoinedMessage;
    }

    private String filterWord(String word) {
        filteredWords:
        for (String filteredWord : coreFilteredWords) {
            if (word.toLowerCase().equalsIgnoreCase(filteredWord)) {
                for (String whitelistedWord : wordWhitelist) {
                    if (word.toLowerCase().equalsIgnoreCase(whitelistedWord)) {
                        break filteredWords;
                    }
                }
                //This is not in the word whitelist, filter it;
                //So as to skip the second part of the pair, add 1.
                return "HONK!";
            }
        }

        for (String blacklistedWord : wordBlacklist) {
            if (word.toLowerCase().equalsIgnoreCase(blacklistedWord)) {
                //So as to skip the second part of the pair, add 1.
                return "HONK!";
            }
        }

        return word;
    }

    public List<String> getCoreFilteredWords() {
        return new ArrayList<>(coreFilteredWords);
    }

    public List<String> getWordWhitelist() {
        return new ArrayList<>(wordWhitelist);
    }

    public List<String> getWordBlacklist() {
        return new ArrayList<>(wordBlacklist);
    }

    public List<String> getBannedPhrases() {
        return new ArrayList<>(bannedPhrases);
    }

    public void addWhitelistedWord(String word) {
        wordWhitelist.add(word);
    }

    public void addBlacklistedWord(String word) {
        wordBlacklist.add(word);
    }

    public void addCoreFilteredWord(String word) {
        coreFilteredWords.add(word);
    }

    public void addBannedPhrase(String phrase) {
        bannedPhrases.add(phrase);
    }
}
