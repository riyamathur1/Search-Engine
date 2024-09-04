package edu.usfca.cs272;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * ThreadSafeInvertedIndex extends the InvertedIndex class to provide thread
 * safe access to the inverted index.
 */
public class ThreadSafeInvertedIndex extends InvertedIndex {
	/**
	 * A MultiReaderLock used to provide thread safe read and write access to the
	 * inverted index.
	 */
	private final MultiReaderLock lock;

	/**
	 * Constructor initializing a new ThreadSafeInvertedIndex
	 */
	public ThreadSafeInvertedIndex() {
		this.lock = new MultiReaderLock();
	}

	/**
	 * Adds an entry to the inverted index for a given word, location, and position.
	 *
	 * @param word     The word to be added to the index.
	 * @param location The location where the word appears.
	 * @param position The position of the word within the file.
	 */
	@Override
	public void addIndex(String word, String location, int position) {
		lock.writeLock().lock();
		try {
			super.addIndex(word, location, position);
		} finally {
			lock.writeLock().unlock();
		}
	}

	@Override
	public void addAll(InvertedIndex other) {
		lock.writeLock().lock();
		try {
			super.addAll(other);
		} finally {
			lock.writeLock().unlock();
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
	@Override
	public boolean hasCount(String location) {
		lock.readLock().lock();
		try {
			return super.hasCount(location);
		} finally {
			lock.readLock().unlock();
		}
	}

	/**
	 * Checks if a certain word exists in the inverted index
	 *
	 * @param word the word to check for existence within the inverted index
	 * @return {@code true} if the word exists in the inverted index, {@code false}
	 *         otherwise
	 */
	@Override
	public boolean hasWord(String word) {
		lock.readLock().lock();
		try {
			return super.hasWord(word);
		} finally {
			lock.readLock().unlock();
		}
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
	@Override
	public boolean hasPosition(String word, String location, Integer position) {
		lock.readLock().lock();
		try {
			return super.hasPosition(word, location, position);
		} finally {
			lock.readLock().unlock();
		}

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
	@Override
	public boolean hasLocation(String word, String location) {
		lock.readLock().lock();
		try {
			return super.hasLocation(word, location);
		} finally {
			lock.readLock().unlock();
		}
	}

	/**
	 * Calls the JsonWriter function that writes an inverted index to a JSON file
	 *
	 * @param path the JSON file to write to.
	 * @throws IOException if an IO error occurs.
	 */
	@Override
	public void writeJson(Path path) throws IOException {
		lock.readLock().lock();
		try {
			super.writeJson(path);
		} finally {
			lock.readLock().unlock();
		}
	}

	/**
	 * Gets the word counts.
	 *
	 * @return A TreeMap where each key is the location and corresponding value is
	 *         the word count.
	 */
	@Override
	public Map<String, Integer> getCounts() {
		lock.readLock().lock();
		try {
			return super.getCounts();
		} finally {
			lock.readLock().unlock();
		}
	}

	/**
	 * Gets all the words in the inverted index.
	 *
	 * @return A set of all words in the inverted index.
	 */
	@Override
	public Set<String> getWords() {
		lock.readLock().lock();
		try {
			return super.getWords();
		} finally {
			lock.readLock().unlock();
		}
	}

	/**
	 * Gets all the locations where a specific word exists in the inverted index.
	 *
	 * @param word The word to check for existence in locations.
	 * @return A set of locations where the word exists, or an empty set if the word
	 *         is not found.
	 */
	@Override
	public Set<String> getLocations(String word) {
		lock.readLock().lock();
		try {
			return super.getLocations(word);
		} finally {
			lock.readLock().unlock();
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
	@Override
	public Set<Integer> getPositions(String word, String location) {
		lock.readLock().lock();
		try {
			return super.getPositions(word, location);
		} finally {
			lock.readLock().unlock();
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
	@Override
	public int numPositions(String word, String location) {
		lock.readLock().lock();
		try {
			return super.numPositions(word, location);
		} finally {
			lock.readLock().unlock();
		}
	}

	/**
	 * Returns the total number of word counts associated with different file
	 * locations.
	 *
	 * @return The total number of word counts in the inverted index.
	 */
	@Override
	public int numCounts() {
		lock.readLock().lock();
		try {
			return super.numCounts();
		} finally {
			lock.readLock().unlock();
		}

	}

	/**
	 * Returns the total number of unique words in the inverted index.
	 *
	 * @return The total number of unique words.
	 */
	@Override
	public int numWords() {
		lock.readLock().lock();
		try {
			return super.numWords();
		} finally {
			lock.readLock().unlock();
		}

	}

	/**
	 * Returns the number of locations where a specific word exists in the inverted
	 * index.
	 *
	 * @param word The word to check for existence in locations.
	 * @return The number of locations where the word exists, or 0 if the word is
	 *         not found.
	 */
	@Override
	public int numLocations(String word) {
		lock.readLock().lock();
		try {
			return super.numLocations(word);
		} finally {
			lock.readLock().unlock();
		}

	}

	/**
	 * Gets the word count associated with a specific location.
	 *
	 * @param location The location for which you want to get the word count.
	 * @return The word count associated with the specified location, or 0 if not
	 *         found.
	 */
	@Override
	public int getNumWords(String location) {
		lock.readLock().lock();
		try {
			return super.getNumWords(location);
		} finally {
			lock.readLock().unlock();
		}

	}

	/**
	 * Returns string that includes a list of words and their corresponding
	 * positions in locations and a list of word counts in each location.
	 *
	 * @return a string representation of the Inverted Index and Word Counts
	 */
	@Override
	public String toString() {
		lock.readLock().lock();
		try {
			return super.toString();
		} finally {
			lock.readLock().unlock();
		}

	}

	/**
	 * Performs a partial search for query stems in the inverted index and returns a
	 * list of search results.
	 *
	 * @param queryStems A Set of query stems to search for partially.
	 * @return A list of SearchResult objects representing the partial search
	 *         results.
	 */
	@Override
	public List<SearchResult> partialSearch(Set<String> queryStems) {
		lock.readLock().lock();
		try {
			return super.partialSearch(queryStems);
		} finally {
			lock.readLock().unlock();
		}
	}

	/**
	 * Performs an exact search for query stems in the inverted index and returns a
	 * list of search results.
	 *
	 * @param queryStems A Set of query stems to search for exactly.
	 * @return A list of SearchResult objects representing the exact search results.
	 */
	@Override
	public List<SearchResult> exactSearch(Set<String> queryStems) {
		lock.readLock().lock();
		try {
			return super.exactSearch(queryStems);
		} finally {
			lock.readLock().unlock();
		}
	}

}