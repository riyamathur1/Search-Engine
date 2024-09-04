package edu.usfca.cs272;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Outputs several simple data structures in "pretty" JSON format where newlines
 * are used to separate elements and nested elements are indented using spaces.
 *
 * Warning: This class is not thread-safe. If multiple threads access this class
 * concurrently, access must be synchronized externally.
 *
 * @author CS 272 Software Development (University of San Francisco)
 * @version Fall 2023
 */
public class JsonWriter {
	/**
	 * Indents the writer by the specified number of times. Does nothing if the
	 * indentation level is 0 or less.
	 *
	 * @param writer the writer to use
	 * @param indent the number of times to indent
	 * @throws IOException if an IO error occurs
	 */
	public static void writeIndent(Writer writer, int indent) throws IOException {
		while (indent-- > 0) {
			writer.write("  ");
		}
	}

	/**
	 * Indents and then writes the String element.
	 *
	 * @param element the element to write
	 * @param writer  the writer to use
	 * @param indent  the number of times to indent
	 * @throws IOException if an IO error occurs
	 */
	public static void writeIndent(String element, Writer writer, int indent) throws IOException {
		writeIndent(writer, indent);
		writer.write(element);
	}

	/**
	 * Indents and then writes the text element surrounded by {@code " "} quotation
	 * marks.
	 *
	 * @param element the element to write
	 * @param writer  the writer to use
	 * @param indent  the number of times to indent
	 * @throws IOException if an IO error occurs
	 */
	public static void writeQuote(String element, Writer writer, int indent) throws IOException {
		writeIndent(writer, indent);
		writer.write('"');
		writer.write(element);
		writer.write('"');
	}

	/**
	 * Writes the elements as a pretty JSON array.
	 *
	 * @param elements the elements to write
	 * @param writer   the writer to use
	 * @param indent   the initial indent level
	 * @throws IOException if an IO error occurs
	 *
	 * @see Writer#write(String)
	 * @see #writeIndent(Writer, int)
	 * @see #writeIndent(String, Writer, int)
	 */
	public static void writeArray(Collection<? extends Number> elements, Writer writer, int indent) throws IOException {
		writer.write("[");

		// Using an iterator for the collection's elements
		var iterator = elements.iterator();

		// Writing first element
		if (iterator.hasNext()) {
			writer.write("\n");
			writeIndent(iterator.next().toString(), writer, indent + 1);
		}
		// Iterating through elements
		while (iterator.hasNext()) {
			writer.write(",\n");
			writeIndent(iterator.next().toString(), writer, indent + 1);
		}
		writer.write("\n");
		writeIndent("]", writer, indent);
	}

	/**
	 * Writes the elements as a pretty JSON array to file.
	 *
	 * @param elements the elements to write
	 * @param path     the file path to use
	 * @throws IOException if an IO error occurs
	 *
	 * @see Files#newBufferedReader(Path, java.nio.charset.Charset)
	 * @see StandardCharsets#UTF_8
	 * @see #writeArray(Collection, Writer, int)
	 */
	public static void writeArray(Collection<? extends Number> elements, Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, UTF_8)) {
			writeArray(elements, writer, 0);
		}
	}

	/**
	 * Returns the elements as a pretty JSON array.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 *
	 * @see StringWriter
	 * @see #writeArray(Collection, Writer, int)
	 */
	public static String writeArray(Collection<? extends Number> elements) {
		try {
			StringWriter writer = new StringWriter();
			writeArray(elements, writer, 0);
			return writer.toString();
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Writes the elements as a pretty JSON object.
	 *
	 * @param elements the elements to write
	 * @param writer   the writer to use
	 * @param indent   the initial indent level
	 * @throws IOException if an IO error occurs
	 *
	 * @see Writer#write(String)
	 * @see #writeIndent(Writer, int)
	 * @see #writeIndent(String, Writer, int)
	 */
	public static void writeObject(Map<String, ? extends Number> elements, Writer writer, int indent)
			throws IOException {

		// Using an iterator for the collection's elements
		var iterator = elements.entrySet().iterator();

		writer.write("{");

		// Writing first element
		if (iterator.hasNext()) {
			writer.write("\n");
			var entry = iterator.next();
			writeIndent("\"" + entry.getKey() + "\": " + entry.getValue().toString(), writer, indent + 1);
		}

		// Iterating through elements
		while (iterator.hasNext()) {
			writer.write(",\n");
			var entry = iterator.next();
			writeIndent("\"" + entry.getKey() + "\": " + entry.getValue().toString(), writer, indent + 1);
		}

		writer.write("\n");
		writeIndent("}", writer, indent);
	}

	/**
	 * Writes the elements as a pretty JSON object to file.
	 *
	 * @param elements the elements to write
	 * @param path     the location path to use
	 * @throws IOException if an IO error occurs
	 *
	 * @see Files#newBufferedReader(Path, java.nio.charset.Charset)
	 * @see StandardCharsets#UTF_8
	 * @see #writeObject(Map, Writer, int)
	 */
	public static void writeObject(Map<String, ? extends Number> elements, Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, UTF_8)) {
			writeObject(elements, writer, 0);
		}
	}

	/**
	 * Returns the elements as a pretty JSON object.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 *
	 * @see StringWriter
	 * @see #writeObject(Map, Writer, int)
	 */
	public static String writeObject(Map<String, ? extends Number> elements) {
		try {
			StringWriter writer = new StringWriter();
			writeObject(elements, writer, 0);
			return writer.toString();
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Writes the provided inverted index to a JSON file
	 *
	 * @param index  the inverted index to write
	 * @param writer the writer to use
	 * @param indent the initial indent level
	 * @throws IOException if an IO error occurs
	 */
	public static void writeInvertedIndex(
			Map<String, ? extends Map<String, ? extends Collection<? extends Number>>> index, Writer writer, int indent)
			throws IOException {
		writer.write("{");

		// Using an iterator for the collection's elements
		var iterator = index.entrySet().iterator();
		if (iterator.hasNext()) {
			writer.write("\n");
			var firstEntry = iterator.next();
			writeIndent("\"" + firstEntry.getKey() + "\": ", writer, indent + 1);
			writeObjectArrays((Map<String, ? extends Collection<? extends Number>>) firstEntry.getValue(), writer,
					indent + 1);
			// Process the rest of the entries
			while (iterator.hasNext()) {
				var entry = iterator.next();
				writer.write(",\n");
				writeIndent("\"" + entry.getKey() + "\": ", writer, indent + 1);
				writeObjectArrays(entry.getValue(), writer, indent + 1);
			}
		}
		writer.write("\n}");
	}

	/**
	 * Writes the provided inverted index to a JSON file.
	 *
	 * @param index the inverted index to write
	 * @param path  the location path where the inverted index should be written to
	 * @throws IOException if an IO error occurs
	 */
	public static void writeInvertedIndex(
			Map<String, ? extends Map<String, ? extends Collection<? extends Number>>> index, Path path)
			throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			writeInvertedIndex(index, writer, 0);
		}
	}

	/**
	 * Returns the provided inverted index as a pretty JSON string.
	 *
	 * @param index the inverted index to use
	 * @return a {@link String} containing the inverted index in pretty JSON format
	 */
	public static String writeInvertedIndex(
			Map<String, ? extends Map<String, ? extends Collection<? extends Number>>> index) {
		try {
			StringWriter writer = new StringWriter();
			writeInvertedIndex(index, writer, 0);
			return writer.toString();
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Writes the elements as a pretty JSON object with nested arrays. The generic
	 * notation used allows this method to be used for any type of map with any type
	 * of nested collection of number objects.
	 *
	 * @param elements the elements to write
	 * @param writer   the writer to use
	 * @param indent   the initial indent level
	 * @throws IOException if an IO error occurs
	 *
	 * @see Writer#write(String)
	 * @see #writeIndent(Writer, int)
	 * @see #writeIndent(String, Writer, int)
	 * @see #writeArray(Collection)
	 */
	public static void writeObjectArrays(Map<String, ? extends Collection<? extends Number>> elements, Writer writer,
			int indent) throws IOException {
		writer.write("{");
		// Using an iterator for the collection's elements
		var iterator = elements.entrySet().iterator();
		if (iterator.hasNext()) {
			writer.write("\n");
			var entry = iterator.next();
			writeIndent("\"" + entry.getKey() + "\": ", writer, indent + 1);
			writeArray(entry.getValue(), writer, indent + 1);
		}

		// Iterating through elements
		while (iterator.hasNext()) {
			writer.write(",\n");
			var entry = iterator.next();
			writeIndent("\"" + entry.getKey() + "\": ", writer, indent + 1);
			writeArray(entry.getValue(), writer, indent + 1);
		}

		writer.write("\n");
		writeIndent("}", writer, indent);
	}

	/**
	 * Writes the elements as a pretty JSON object with nested arrays to file.
	 *
	 * @param elements the elements to write
	 * @param path     the file path to use
	 * @throws IOException if an IO error occurs
	 *
	 * @see Files#newBufferedReader(Path, java.nio.charset.Charset)
	 * @see StandardCharsets#UTF_8
	 * @see #writeObjectArrays(Map, Writer, int)
	 */
	public static void writeObjectArrays(Map<String, ? extends Collection<? extends Number>> elements, Path path)
			throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, UTF_8)) {
			writeObjectArrays(elements, writer, 0);
		}
	}

	/**
	 * Returns the elements as a pretty JSON object with nested arrays.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 *
	 * @see StringWriter
	 * @see #writeObjectArrays(Map, Writer, int)
	 */
	public static String writeObjectArrays(Map<String, ? extends Collection<? extends Number>> elements) {
		try {
			StringWriter writer = new StringWriter();
			writeObjectArrays(elements, writer, 0);
			return writer.toString();
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Writes the elements as a pretty JSON array with nested objects. The generic
	 * notation used allows this method to be used for any type of collection with
	 * any type of nested map of String keys to number objects.
	 *
	 * @param elements the elements to write
	 * @param writer   the writer to use
	 * @param indent   the initial indent level
	 * @throws IOException if an IO error occurs
	 *
	 * @see Writer#write(String)
	 * @see #writeIndent(Writer, int)
	 * @see #writeIndent(String, Writer, int)
	 * @see #writeObject(Map)
	 */
	public static void writeArrayObjects(Collection<? extends Map<String, ? extends Number>> elements, Writer writer,
			int indent) throws IOException {
		writer.write("[");

		// Using an iterator for the collection's elements
		var iterator = elements.iterator();
		if (iterator.hasNext()) {
			writer.write("\n");
			var nestedMap = iterator.next();
			writeIndent("", writer, indent + 1);
			writeObject(nestedMap, writer, indent + 1);
		}

		// Iterating through elements
		while (iterator.hasNext()) {
			writer.write(",\n");
			var nestedMap = iterator.next();
			writeIndent("", writer, indent + 1);
			writeObject(nestedMap, writer, indent + 1);
		}

		writer.write("\n");
		writeIndent("]", writer, indent);
	}

	/**
	 * Writes the elements as a pretty JSON array with nested objects to file.
	 *
	 * @param elements the elements to write
	 * @param path     the file path to use
	 * @throws IOException if an IO error occurs
	 *
	 * @see Files#newBufferedReader(Path, java.nio.charset.Charset)
	 * @see StandardCharsets#UTF_8
	 * @see #writeArrayObjects(Collection)
	 */
	public static void writeArrayObjects(Collection<? extends Map<String, ? extends Number>> elements, Path path)
			throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, UTF_8)) {
			writeArrayObjects(elements, writer, 0);
		}
	}

	/**
	 * Returns the elements as a pretty JSON array with nested objects.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 *
	 * @see StringWriter
	 * @see #writeArrayObjects(Collection)
	 */
	public static String writeArrayObjects(Collection<? extends Map<String, ? extends Number>> elements) {
		try {
			StringWriter writer = new StringWriter();
			writeArrayObjects(elements, writer, 0);
			return writer.toString();
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Writes a single search result in JSON format.
	 * 
	 * @param <T>    The type of collection containing search results.
	 * @param result The search result to write.
	 * @param writer The writer to use.
	 * @param indent The initial indent level.
	 * @throws IOException If an I/O error occurs.
	 */
	public static <T extends Collection<? extends InvertedIndex.SearchResult>> void writeSearchResult(
			InvertedIndex.SearchResult result, Writer writer, int indent) throws IOException {
		writeIndent("{\n", writer, indent);
		writeIndent("\"count\": " + result.getNumMatches() + ",\n", writer, indent + 1);

		DecimalFormat decimalFormat = new DecimalFormat("0.00000000");
		writeIndent("\"score\": " + decimalFormat.format(result.getScore()) + ",\n", writer, indent + 1);

		writeIndent("\"where\": \"" + result.getLocation() + "\"\n", writer, indent + 1);
		writeIndent("}", writer, indent);
	}

	/**
	 * Writes a collection of search results in JSON format.
	 * 
	 * @param <T>     The type of collection containing search results.
	 * @param results The collection of search results to write.
	 * @param writer  The writer to use.
	 * @param indent  The initial indent level.
	 * @throws IOException If an I/O error occurs.
	 */
	public static <T extends Collection<? extends InvertedIndex.SearchResult>> void writeSearchResults(T results,
			Writer writer, int indent) throws IOException {
		writer.write("[\n");

		Iterator<? extends InvertedIndex.SearchResult> iterator = results.iterator();

		if (iterator.hasNext()) {
			while (iterator.hasNext()) {
				InvertedIndex.SearchResult result = iterator.next();
				writeSearchResult(result, writer, indent + 1);

				// Add a comma if it's not the last result
				if (iterator.hasNext()) {
					writer.write(",");
				}
				writer.write("\n");
			}
		}

		writeIndent("]", writer, indent);
	}

	/**
	 * Writes a map of search results by stem in JSON format.
	 * 
	 * @param <T>           The type of collection containing search results.
	 * @param resultsByStem The map of search results by stem to write.
	 * @param writer        The writer to use.
	 * @param indent        The initial indent level.
	 * @throws IOException If an I/O error occurs.
	 */
	public static <T extends Collection<? extends InvertedIndex.SearchResult>> void writeSearchResults(
			Map<String, T> resultsByStem, Writer writer, int indent) throws IOException {
		writer.write("{\n");

		// Get the sorted entries by query stem name
		List<Map.Entry<String, T>> sortedEntries = new ArrayList<>(resultsByStem.entrySet());
		sortedEntries.sort(Comparator.comparing(Map.Entry::getKey));

		Iterator<Map.Entry<String, T>> iterator = sortedEntries.iterator();
		if (iterator.hasNext()) {
			while (iterator.hasNext()) {
				Map.Entry<String, T> entry = iterator.next();
				String queryStem = entry.getKey();
				T results = entry.getValue();

				List<InvertedIndex.SearchResult> sortedResults = new ArrayList<>(results);

				// Write the query stem
				writeIndent("\"" + queryStem + "\": ", writer, indent + 1);
				writeSearchResults(sortedResults, writer, indent + 1);

				// Add a comma if it's not the last query stem
				if (iterator.hasNext()) {
					writer.write(",");
				}
				writer.write("\n");

			}
		}

		writeIndent("}", writer, indent);
	}

	/**
	 * Writes a map of search results by stem to a specified file path in JSON
	 * format.
	 * 
	 * @param <T>           The type of collection containing search results.
	 * @param resultsByStem The map of search results by stem to write.
	 * @param path          The file path where the output should be written.
	 * @throws IOException If an I/O error occurs.
	 */
	public static <T extends Collection<? extends InvertedIndex.SearchResult>> void writeSearchResults(
			Map<String, T> resultsByStem, Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			writeSearchResults(resultsByStem, writer, 0);
		}
	}

}