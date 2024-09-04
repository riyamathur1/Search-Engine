package edu.usfca.cs272;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import edu.usfca.cs272.InvertedIndex.SearchResult;

/**
 * Interface for a query processor that performs searches on an inverted index.
 */
public interface QueryProcessorInterface {

	/**
	 * Processes queries from a file and collects search results.
	 *
	 * @param queryPath The path to the query file.
	 * @throws IOException If there is an error reading the query file.
	 */
	public default void processPath(Path queryPath) throws IOException {
		try (BufferedReader br = Files.newBufferedReader(queryPath, UTF_8)) {
			String line;
			while ((line = br.readLine()) != null) {
				processQueries(line);
			}
		}
	}

	/**
	 * Processes a single query line and collects search results.
	 *
	 * @param line The query line to process.
	 */
	void processQueries(String line);

	/**
	 * Retrieves a set of query stems.
	 *
	 * @return An unmodifiable set of query stems.
	 */
	Set<String> getQueryStems();

	/**
	 * Retrieves the search results associated with a specific query stem.
	 *
	 * @param queryStem The query stem for which search results are requested.
	 * @return An unmodifiable list of search results for the specified query stem,
	 *         or an empty list if there are no results for the given query stem.
	 */
	List<SearchResult> getSearchResults(String queryStem);

	/**
	 * Writes search results to a JSON file.
	 *
	 * @param path The path to write the JSON file.
	 * @throws IOException If an I/O error occurs when writing the JSON files.
	 */
	void writeJson(Path path) throws IOException;

}
