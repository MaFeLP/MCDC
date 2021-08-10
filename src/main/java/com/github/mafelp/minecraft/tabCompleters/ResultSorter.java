package com.github.mafelp.minecraft.tabCompleters;

import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A class that sorts tab completion results.
 */
public class ResultSorter {
    /**
     * A method that sorts the results provided in the second argument alphabetically and after what has already been
     * typed (first argument).
     * @param arg The string that has already been typed.
     * @param results The results to be sorted.
     * @return The sorted results.
     */
    // Sorts possible results to provide true tab auto complete based off of what is already typed.
    public static List<String> sortedResults(String arg, List<String> results) {
        final List<String> completions = new ArrayList<>();
        StringUtil.copyPartialMatches(arg, results, completions);
        Collections.sort(completions);
        results.clear();
        results.addAll(completions);
        return results;
    }
}
