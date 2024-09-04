package edu.usfca.cs272;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import edu.usfca.cs272.InvertedIndex.SearchResult;
import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer.ALGORITHM;

/**
 * Processes queries in a multithreaded manner.
 */
public class MultiThreadedQueryProcessor implements QueryProcessorInterface {
	/**
	 * A mapping of query stems to their corresponding search results.
	 */
	private final Map<String, List<SearchResult>> resultsByStem;

	/**
	 * Flag indicating whether partial search is allowed (true) or exact search is
	 * allowed (false).
	 */
	private final boolean partialSearch;

	/**
	 * The index to use for searching.
	 */
	private final ThreadSafeInvertedIndex threadSafeIndex;

	/**
	 * The inverted index used for searching.
	 */
	private final WorkQueue workQueue;

	/**
	 * Initializes a new QueryProcessor
	 *
	 * @param threadSafeIndex The inverted index to use for searching.
	 * @param partialSearch   Flag indicating whether partial search is allowed
	 *                        (true) or exact search is allowed (false).
	 * @param workQueue       The work queue for managing multithreaded processing.
	 */
	public MultiThreadedQueryProcessor(ThreadSafeInvertedIndex threadSafeIndex, boolean partialSearch,
			WorkQueue workQueue) {
		this.resultsByStem = new HashMap<>();
		this.threadSafeIndex = threadSafeIndex;
		this.partialSearch = partialSearch;
		this.workQueue = workQueue;
	}

	/**
	 * Processes queries from a file and collects search results in a multithreaded
	 * manner.
	 *
	 * @param queryPath The path to the query file.
	 * @throws IOException If there is an error reading the query file.
	 */
	@Override
	public void processPath(Path queryPath) throws IOException {
		QueryProcessorInterface.super.processPath(queryPath);
		workQueue.finish();
	}

	/**
	 * Processes a single query line and collects search results in a multithreaded
	 * manner.
	 *
	 * @param line The query line to process.
	 */
	@Override
	public void processQueries(String line) {
		Task task = new Task(line);
		workQueue.execute(task);
	}

	/**
	 * Retrieves a set of query stems.
	 *
	 * @return An unmodifiable set of query stems.
	 */
	@Override
	public Set<String> getQueryStems() {
		synchronized (resultsByStem) {
			return Collections.unmodifiableSet(resultsByStem.keySet());
		}
	}

	/**
	 * Retrieves the search results associated with a specific query stem.
	 *
	 * @param queryStem The query stem for which search results are requested.
	 * @return An unmodifiable list of search results for the specified query stem,
	 *         or an empty list if there are no results for the given query stem.
	 */
	@Override
	public List<SearchResult> getSearchResults(String queryStem) {
		// Stem and join the query line
		TreeSet<String> queryStems = FileStemmer.uniqueStems(queryStem, new SnowballStemmer(ALGORITHM.ENGLISH));
		String stemmedQuery = String.join(" ", queryStems);

		synchronized (resultsByStem) {
			List<SearchResult> results = resultsByStem.get(stemmedQuery);
			return results != null ? Collections.unmodifiableList(results) : Collections.emptyList();
		}
	}

	/**
	 * Writes search results to a JSON file.
	 *
	 * @param path The path to write the JSON file.
	 * @throws IOException If an I/O error occurs when writing the JSON files.
	 */
	@Override
	public void writeJson(Path path) throws IOException {
		synchronized (resultsByStem) {
			JsonWriter.writeSearchResults(resultsByStem, path);
		}
	}

	/**
	 * Returns a string representation of the search results organized by query
	 * stem.
	 *
	 * @return A string containing the search results organized by query stem.
	 */
	@Override
	public String toString() {
		synchronized (resultsByStem) {
			return resultsByStem.toString();
		}
	}

	/**
	 * Represents a task to process each query line in a multithreaded manner.
	 */
	private class Task implements Runnable {
		/**
		 * The query line to process.
		 */
		private final String line;

		/**
		 * The thread safe inverted index to search.
		 */
		private final Stemmer stemmer;

		/**
		 * Initializes a new task.
		 *
		 * @param line The query line to process.
		 */
		public Task(String line) {
			this.line = line;
			this.stemmer = new SnowballStemmer(ALGORITHM.ENGLISH);

		}

		/**
		 * Runs the task and processes the query line. Updates search results in
		 * resultsByStem.
		 */
		@Override
		public void run() {
			TreeSet<String> queryStems = FileStemmer.uniqueStems(line, stemmer);
			String queryLine = String.join(" ", queryStems);

			synchronized (resultsByStem) {
				if (queryStems.isEmpty() || resultsByStem.containsKey(queryLine)) {
					return;
				}
			}

			List<SearchResult> searchResults = threadSafeIndex.search(queryStems, partialSearch);

			synchronized (resultsByStem) {
				resultsByStem.put(queryLine, searchResults); // Query stem as the key
			}
		}
	}
}