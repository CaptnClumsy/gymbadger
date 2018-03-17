package com.clumsy.gymbader.imaging;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.text.similarity.LevenshteinDistance;

import com.clumsy.gymbadger.data.SimpleGymDao;

public class GymMatcher {

	public static List<SimpleGymDao> getBestMatches(final String scannedText, final List<SimpleGymDao> gymList) {
		List<SimpleGymDao> matches = new ArrayList<SimpleGymDao>();
		// Try to find an exact match
		for (SimpleGymDao gym : gymList) {
			if (gym.getShortName().equalsIgnoreCase(scannedText)) {
				matches.add(gym);
			}
		}
		// If we found ANY exact matches return them now
		if (matches.size()>0) {
			return matches;
		}
		// No exact match so start guessing...
		LevenshteinDistance d = new LevenshteinDistance();
		SimpleGymDao bestMatch = null;
		double lowestDifference = 50.0;
		for (SimpleGymDao gym : gymList) {
			int longest = Math.max(scannedText.length(), gym.getShortName().length());
			int distance = d.apply(scannedText, gym.getShortName());
			double percentdiff = ((double)distance/(double)longest)*100;
			if (percentdiff <= lowestDifference) {
				lowestDifference = percentdiff;
				bestMatch = gym;
				matches.add(bestMatch);
			}
		}
		if (bestMatch!=null && bestMatch.getName().length() != 0) {
			// Ensure the best match comes first
			matches.remove(bestMatch);
			matches.add(0, bestMatch);
		}
		return matches;
	}
}
