package edu.usfca.cs272;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * The InvertedIndex class represents an inverted index data structure for word
 * counts. It stores word counts associated with file paths and line numbers.
 */
public class InvertedIndex {
	/**
	 * Represents the location of the search result.
	 */
	public class SearchResult implements Comparable<SearchResult> {
		/**
		 * Represents the location of the search result.
		 */
		private final String location;
		/**
		 * Represents the number of matches in the search result.
		 */
		private int numMatches;
		/**
		 * Represents the score of the search result.
		 */
		private double score;

		/**
		 * Initializes a new SearchResult object.
		 *
		 * @param location The location of the search result.
		 */
		public SearchResult(String location) {
			this.location = location;
			this.numMatches = 0;
			this.score = 0;
		}

		/**
		 * Updates the count of matches by adding the incrementing number of matches to
		 * the current count.
		 *
		 * @param numMatches The number of matches to add to the current count.
		 */
		private void update(int numMatches) {
			this.numMatches += numMatches;
			this.score = (double) this.numMatches / counts.get(this.location);
		}

		/**
		 * Gets the location of the search result.
		 *
		 * @return The location as a string.
		 */
		public String getLocation() {
			return location;
		}

		/**
		 * Gets the number of matches in the search result.
		 *
		 * @return The number of matches.
		 */
		public int getNumMatches() {
			return numMatches;
		}

		/**
		 * Gets the score of the search result.
		 *
		 * @return The score of the search result.
		 */
		public double getScore() {
			return score;
		}

		/**
		 * Compares this SearchResult with another SearchResult.
		 *
		 * @param other The SearchResult to compare against.
		 * @return A negative integer, zero, or a positive integer as the SearchResult
		 *         is less than, equal to, or greater than the other SearchResult.
		 */
		@Override
		public int compareTo(SearchResult other) {
			// First compare by score
			int scoreComparison = Double.compare(other.getScore(), this.getScore());
			if (scoreComparison != 0) {
				return scoreComparison;
			}

			// If scores are equal, compare by count
			int countComparison = Integer.compare(other.numMatches, this.numMatches);
			if (countComparison != 0) {
				return countComparison;
			}

			// If scores and counts are equal, compare by location
			return this.location.compareTo(other.location);
		}

		/**
		 * Returns a string representation of the SearchResult.
		 *
		 * @return A string representation of the SearchResult.
		 */
		@Override
		public String toString() {
			return "Location: " + location + ", Total Word Count: " + counts.get(this.location) + ", Matches: "
					+ numMatches;
		}
	}

	/**
	 * The inverted index, which maps words to their occurrences in various
	 * locations.
	 */
	private final TreeMap<String, TreeMap<String, TreeSet<Integer>>> inverted;

	/**
	 * The word counts associated with different locations.
	 */
	private final TreeMap<String, Integer> counts;

	/**
	 * Initializes a new instance of the InvertedIndex class. Creates an empty
	 * inverted index.
	 */
	public InvertedIndex() {
		this.inverted = new TreeMap<>();
		this.counts = new TreeMap<>();

	}

	/**
	 * Adds an entry to the inverted index for a given word, location, and position.
	 *
	 * @param word     The word to be added to the index.
	 * @param location The location where the word appears.
	 * @param position The position of the word within the file.
	 */
	public void addIndex(String word, String location, int position) {
		TreeMap<String, TreeSet<Integer>> filePositions = inverted.computeIfAbsent(word, k -> new TreeMap<>());
		TreeSet<Integer> positionsList = filePositions.computeIfAbsent(location, k -> new TreeSet<>());

		positionsList.add(position);

		counts.merge(location, position, Math::max);
	}

	/**
	 * Adds all the contents of another InvertedIndex to this InvertedIndex.
	 *
	 * @param otherIndex The InvertedIndex to add to this InvertedIndex.
	 */
	public void addAll(InvertedIndex otherIndex) {
		for (var entry : otherIndex.inverted.entrySet()) {
			String word = entry.getKey();
			TreeMap<String, TreeSet<Integer>> otherLocations = entry.getValue();
			TreeMap<String, TreeSet<Integer>> locations = this.inverted.get(word);

			if (locations == null) {
				// If the word doesn't exist, add all its locations and positions
				this.inverted.put(word, otherLocations);
			} else {
				// If the word already exists, merge its locations and positions
				for (var locationEntry : otherLocations.entrySet()) {

					String location = locationEntry.getKey();
					var otherPositions = locationEntry.getValue();
					var positions = locations.get(location);

					if (positions == null) {
						// If the location doesn't exist, add all positions
						locations.put(location, otherPositions);

					} else {
						// If the location exists, merge positions
						positions.addAll(otherPositions);
					}
				}
			}
		}
		// Merge word counts for each location
		for (var entryLocation : otherIndex.getCounts().entrySet()) {
			String location = entryLocation.getKey();
			int count = entryLocation.getValue();
			counts.merge(location, count, Integer::max);
		}
	}

	/**
	 * Checks if a word count exists for a certain location.
	 *
	 * @param location the location to check for word count
	 * @return {@code true} if a word count exists for the specified location,
	 *         {@code false} otherwise
	 *
	 */
	public boolean hasCount(String location) {
		return counts.containsKey(location);
	}

	/**
	 * Checks if a certain word exists in the inverted index
	 *
	 * @param word the word to check for existence within the inverted index
	 * @return {@code true} if the word exists in the inverted index, {@code false}
	 *         otherwise
	 */
	public boolean hasWord(String word) {
		return inverted.containsKey(word);
	}

	/**
	 * Checks if a specific word exists within a given location in the Inverted
	 * Index.
	 *
	 * @param word     the word to check for existence within the specified location
	 * @param location the file location to check for the presence of the word
	 * @return {@code true} if the word exists in the specified location,
	 *         {@code false} otherwise
	 *
	 */
	public boolean hasLocation(String word, String location) {
		return hasWord(word) && inverted.get(word).containsKey(location);
	}

	/**
	 * Checks if a specific position exists for a given word and location in the
	 * Inverted Index.
	 *
	 * @param word     the word to check for the position
	 * @param location the file location to check for the position
	 * @param position the position to check for existence within the specified
	 *                 location
	 * @return {@code true} if the position exists for the word in the specified
	 *         location, {@code false} otherwise
	 */
	public boolean hasPosition(String word, String location, Integer position) {
		return hasLocation(word, location) && inverted.get(word).get(location).contains(position);
	}

	/**
	 * Calls the JsonWriter function that writes an inverted index to a JSON file
	 *
	 * @param path the JSON file to write to.
	 * @throws IOException if an IO error occurs.
	 */
	public void writeJson(Path path) throws IOException {
		JsonWriter.writeInvertedIndex(inverted, path);
	}

	/**
	 * Gets the word counts.
	 *
	 * @return A TreeMap where each key is the location and corresponding value is
	 *         the word count.
	 */
	public Map<String, Integer> getCounts() {
		return Collections.unmodifiableMap(counts);
	}

	/**
	 * Gets all the words in the inverted index.
	 *
	 * @return A set of all words in the inverted index.
	 */
	public Set<String> getWords() {
		return Collections.unmodifiableSet(inverted.keySet());
	}

	/**
	 * Gets all the locations where a specific word exists in the inverted index.
	 *
	 * @param word The word to check for existence in locations.
	 * @return A set of locations where the word exists, or an empty set if the word
	 *         is not found.
	 */
	public Set<String> getLocations(String word) {
		if (hasWord(word)) {
			return Collections.unmodifiableSet(inverted.get(word).keySet());
		} else {
			return Collections.emptySet();
		}
	}

	/**
	 * Gets all the positions of a specific word within a given location in the
	 * inverted index.
	 *
	 * @param word     The word to check for positions.
	 * @param location The location to check for positions.
	 * @return A set of positions where the word exists in the specified location,
	 *         or an empty set if not found.
	 */
	public Set<Integer> getPositions(String word, String location) {
		if (hasLocation(word, location)) {
			return Collections.unmodifiableSet(inverted.get(word).get(location));
		} else {
			return Collections.emptySet();
		}
	}

	/**
	 * Returns the number of positions of a specific word within a given location in
	 * the inverted index.
	 *
	 * @param word     the word to count occurrences for
	 * @param location the location to count occurrences within
	 * @return the number of occurrences of the word in the specified location, or 0
	 *         if not found
	 */
	public int numPositions(String word, String location) {
		return getPositions(word, location).size();
	}

	/**
	 * Returns the total number of word counts associated with different file
	 * locations.
	 *
	 * @return The total number of word counts in the inverted index.
	 */
	public int numCounts() {
		return counts.size();
	}

	/**
	 * Returns the total number of unique words in the inverted index.
	 *
	 * @return The total number of unique words.
	 */
	public int numWords() {
		return getWords().size();
	}

	/**
	 * Returns the number of locations where a specific word exists in the inverted
	 * index.
	 *
	 * @param word The word to check for existence in locations.
	 * @return The number of locations where the word exists, or 0 if the word is
	 *         not found.
	 */
	public int numLocations(String word) {
		return getLocations(word).size();
	}

	/**
	 * Gets the word count associated with a specific location.
	 *
	 * @param location The location for which you want to get the word count.
	 * @return The word count associated with the specified location, or 0 if not
	 *         found.
	 */
	public int getNumWords(String location) {
		return counts.getOrDefault(location, 0);
	}

	/**
	 * Returns string that includes a list of words and their corresponding
	 * positions in locations and a list of word counts in each location.
	 *
	 * @return a string representation of the Inverted Index and Word Counts
	 */
	@Override
	public String toString() {
		return inverted.toString() + "\n" + counts.toString();
	}

	/**
	 * Searches for query stems in the inverted index and returns a list of search
	 * results.
	 *
	 * @param queryStems A Set of query stems to search for.
	 * @param partial    A boolean flag indicating whether partial search should be
	 *                   performed (true) or exact search (false).
	 * @return A list of SearchResult objects representing the search results.
	 */
	public List<SearchResult> search(Set<String> queryStems, boolean partial) {
		if (partial) {
			return partialSearch(queryStems);
		} else {
			return exactSearch(queryStems);
		}
	}

	/**
	 * Helper method to process an inner map and update search results.
	 *
	 * @param innerMap      The inner map to process.
	 * @param map           The map containing search results.
	 * @param searchResults The list of search results to update.
	 */
	private void processInnerMap(Map<String, TreeSet<Integer>> innerMap, Map<String, SearchResult> map,
			List<SearchResult> searchResults) {
		for (var entry : innerMap.entrySet()) {
			String location = entry.getKey();
			int increment = entry.getValue().size();
			SearchResult result = map.get(location);

			if (result == null) {
				result = new SearchResult(location);
				map.put(location, result);

				searchResults.add(result);
			}
			result.update(increment);
		}
		searchResults.sort(InvertedIndex.SearchResult::compareTo);

	}

	/**
	 * Performs a partial search for query stems in the inverted index and returns a
	 * list of search results.
	 *
	 * @param queryStems A Set of query stems to search for partially.
	 * @return A list of SearchResult objects representing the partial search
	 *         results.
	 */
	public List<SearchResult> partialSearch(Set<String> queryStems) {
		List<SearchResult> searchResults = new ArrayList<>();
		Map<String, SearchResult> map = new HashMap<>();

		for (String queryStem : queryStems) {
			for (Map.Entry<String, TreeMap<String, TreeSet<Integer>>> entry : inverted.tailMap(queryStem).entrySet()) {
				String word = entry.getKey();
				boolean isMatch = word.startsWith(queryStem);

				if (isMatch) {
					processInnerMap(entry.getValue(), map, searchResults);
				} else {
					break;
				}
			}
		}
		return searchResults;
	}

	/**
	 * Performs an exact search for query stems in the inverted index and returns a
	 * list of search results.
	 *
	 * @param queryStems A Set of query stems to search for exactly.
	 * @return A list of SearchResult objects representing the exact search results.
	 */
	public List<SearchResult> exactSearch(Set<String> queryStems) {
		List<SearchResult> searchResults = new ArrayList<>();
		Map<String, SearchResult> map = new HashMap<>();

		for (String queryStem : queryStems) {
			var innerMap = inverted.get(queryStem);
			if (innerMap != null) {
				processInnerMap(innerMap, map, searchResults);
			}
		}
		return searchResults;

	}
}