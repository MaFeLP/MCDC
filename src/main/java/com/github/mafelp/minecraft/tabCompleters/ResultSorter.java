package com.github.mafelp.minecraft.tabCompleters;

import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ResultSorter {
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
