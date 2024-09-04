package edu.usfca.cs272;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer.ALGORITHM;

/**
 * The TextProcessor class provides methods for processing text files, cleaning
 * words, and generating word counts and an inverted index.
 */
public class InvertedIndexProcessor {

	/** The log4j2 logger. */
	private static final Logger log = LogManager.getLogger(InvertedIndexProcessor.class);

	/** The Snowball stemmer. */
	private static final SnowballStemmer stemmer = new SnowballStemmer(ALGORITHM.ENGLISH);

	/**
	 * Processes a given path and updates the provided inverted index accordingly.
	 *
	 * @param inputPath The text path to process, which can be a directory or a
	 *                  file.
	 * @param index     The inverted index to update.
	 * @throws IOException If an IO error occurs.
	 */
	public static void processPath(Path inputPath, InvertedIndex index) throws IOException {
		if (Files.isDirectory(inputPath)) {
			processDirectory(inputPath, index);
		} else if (Files.isRegularFile(inputPath)) {
			processFile(inputPath, index);
		}
	}

	/**
	 * Processes a text file, generates word counts, and an inverted index, and
	 * stores them in the respective maps.
	 *
	 * @param path  The path to the text file to process.
	 * @param index The word counts and inverted index maps to update.
	 * @throws IOException If an I/O error occurs while reading the file.
	 */
	public static void processFile(Path path, InvertedIndex index) throws IOException {
		processFile(path, index, InvertedIndexProcessor.stemmer);
	}

	/**
	 * Processes a text file, generates word counts, and an inverted index, and
	 * stores them in the respective maps.
	 *
	 * @param path    The path to the text file to process.
	 * @param index   The word counts and inverted index maps to update.
	 * @param stemmer The stemmer to use for stemming words.
	 * @throws IOException If an I/O error occurs while reading the file.
	 */
	public static void processFile(Path path, InvertedIndex index, Stemmer stemmer) throws IOException {
		try (BufferedReader br = Files.newBufferedReader(path)) {
			String line;
			int wordPosition = 1;
			String location = path.toString();

			while ((line = br.readLine()) != null) {
				String[] words = FileStemmer.parse(line);
				for (String word : words) {
					String stemmedWord;
					stemmedWord = stemmer.stem(word).toString();
					// Add word position to the inverted index
					index.addIndex(stemmedWord, location, wordPosition);
					wordPosition++;
				}
			}
		} catch (IOException e) {
			log.error("Error processing file: " + path.toString(), e);
		}
	}

	/**
	 * Recursively processes all text files in a directory and its subdirectories,
	 * generating word counts and updating the wordCounts map.
	 *
	 * @param path  The path to the directory to process.
	 * @param index The word counts and inverted index maps to update.
	 * @throws IOException If an I/O error occurs while reading the directory.
	 */
	public static void processDirectory(Path path, InvertedIndex index) throws IOException {
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
			for (Path entry : stream) {
				if (isTextFile(entry)) {
					processFile(entry, index);
				} else if (Files.isDirectory(entry)) {
					log.debug("Processing subdirectory: " + entry.toString());

					// Recursively process subdirectories
					processDirectory(entry, index);
				}
			}
		}

	}

	/**
	 * Checks whether a given file is a text file.
	 *
	 * @param entry The Path object representing the file to be checked.
	 * @return true if the file is a valid text file, false otherwise.
	 */
	public static boolean isTextFile(Path entry) {
		boolean isRegularFile = Files.isRegularFile(entry);
		String lower = entry.getFileName().toString().toLowerCase();
		return isRegularFile && (lower.endsWith(".txt") || lower.endsWith(".text"));
	}

}