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
 * Represents a Query Processor that performs searches on an inverted index.
 */
public class QueryProcessor implements QueryProcessorInterface {
	/**
	 * A mapping of query stems to their corresponding search results.
	 */
	private final Map<String, List<SearchResult>> resultsByStem;

	/**
	 * The inverted index used for searching.
	 */
	private final InvertedIndex index;

	/**
	 * Flag indicating whether partial search is allowed (true) or exact search is
	 * allowed (false).
	 */
	private final boolean partialSearch;
	/**
	 * The stemmer used for query parsing.
	 */
	private final Stemmer stemmer;

	/**
	 * Initializes a new QueryProcessor
	 *
	 * @param index         The inverted index to use for searching.
	 * @param partialSearch Flag indicating whether partial search is allowed (true)
	 *                      or exact search is allowed (false)f.
	 */
	public QueryProcessor(InvertedIndex index, boolean partialSearch) {
		this.resultsByStem = new HashMap<>();
		this.index = index;
		this.partialSearch = partialSearch;
		this.stemmer = new SnowballStemmer(ALGORITHM.ENGLISH);
	}

	/**
	 * Processes a single query line and collects search results.
	 *
	 * @param line The query line to process.
	 */
	@Override
	public void processQueries(String line) {
		TreeSet<String> queryStems = FileStemmer.uniqueStems(line, stemmer);
		if (!queryStems.isEmpty()) {
			String queryLine = String.join(" ", queryStems);
			if (!resultsByStem.containsKey(queryLine)) {
				List<SearchResult> searchResults = index.search(queryStems, partialSearch);
				resultsByStem.put(queryLine, searchResults); // Query stem as the key
			}
		}
	}

	/**
	 * Retrieves a set of query stems.
	 *
	 * @return An unmodifiable set of query stems.
	 */
	@Override
	public Set<String> getQueryStems() {
		return Collections.unmodifiableSet(resultsByStem.keySet());
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
		TreeSet<String> queryStems = FileStemmer.uniqueStems(queryStem, stemmer);
		String stemmedQuery = String.join(" ", queryStems);

		List<SearchResult> results = resultsByStem.get(stemmedQuery);
		return results != null ? Collections.unmodifiableList(results) : Collections.emptyList();
	}

	/**
	 * Writes search results to a JSON file.
	 *
	 * @param path The path to write the JSON file.
	 * @throws IOException If an I/O error occurs when writing the JSON files.
	 */
	@Override
	public void writeJson(Path path) throws IOException {
		JsonWriter.writeSearchResults(resultsByStem, path);
	}

	/**
	 * Returns a string representation of the search results organized by query
	 * stem.
	 *
	 * @return A string containing the search results organized by query stem.
	 */
	@Override
	public String toString() {
		return resultsByStem.toString();
	}
}