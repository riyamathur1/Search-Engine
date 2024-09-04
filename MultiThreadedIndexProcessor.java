package edu.usfca.cs272;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer.ALGORITHM;

/**
 * Processes text files or directories in a multithreaded manner.
 */
public class MultiThreadedIndexProcessor {

	/**
	 * Processes a given path and updates the provided inverted index accordingly in
	 * a multithreaded manner.
	 *
	 * @param inputPath       The text path to process, which can be a directory or
	 *                        a file.
	 * @param threadSafeIndex The thread safe inverted index to update.
	 * @param workQueue       The work queue for managing multithreaded processing.
	 * @throws IOException If an IO error occurs.
	 *
	 */
	public static void processPath(Path inputPath, ThreadSafeInvertedIndex threadSafeIndex, WorkQueue workQueue)
			throws IOException {
		if (Files.isDirectory(inputPath)) {
			processDirectoryMultiThreaded(inputPath, threadSafeIndex, workQueue);
		} else if (Files.isRegularFile(inputPath)) {
			Runnable task = new Runnable() {
				@Override
				public void run() {
					try {
						processFile(inputPath, threadSafeIndex);
					} catch (IOException e) {
						throw new UncheckedIOException(e);
					}
				}
			};
			// Add the task to the WorkQueue for execution
			workQueue.execute(task);
		}

		workQueue.finish();
	}

	/**
	 * Processes a text file in a multithreaded manner, generates word counts, and
	 * updates the thread safe inverted index.
	 *
	 * @param path            The path to the text file to process.
	 * @param threadSafeIndex The thread safe inverted index to update.
	 * @throws IOException If an I/O error occurs.
	 */
	public static void processFile(Path path, ThreadSafeInvertedIndex threadSafeIndex) throws IOException {
		processFile(path, threadSafeIndex, new SnowballStemmer(ALGORITHM.ENGLISH));
	}

	/**
	 * Processes a text file in a multithreaded manner, generates word counts, and
	 * updates the thread safe inverted index with a specified stemmer.
	 *
	 * @param path            The path to the text file to process.
	 * @param threadSafeIndex The thread safe inverted index to update.
	 * @param stemmer         The stemmer to use for stemming words.
	 * @throws IOException If an I/O error occurs.
	 */
	public static void processFile(Path path, ThreadSafeInvertedIndex threadSafeIndex, Stemmer stemmer)
			throws IOException {
		InvertedIndex local = new InvertedIndex();
		InvertedIndexProcessor.processFile(path, local, stemmer);
		threadSafeIndex.addAll(local);
	}

	/**
	 * Recursively processes all text files in a directory and its subdirectories in
	 * a multithreaded manner, generating word counts and updating the wordCounts
	 * map.
	 *
	 * @param path            The path to the directory to process.
	 * @param threadSafeIndex The word counts and inverted index maps to update.
	 * @param workQueue       The work queue for managing multithreaded processing.
	 * @throws IOException If an I/O error occurs while reading the directory.
	 */
	public static void processDirectoryMultiThreaded(Path path, ThreadSafeInvertedIndex threadSafeIndex,
			WorkQueue workQueue) throws IOException {
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
			for (Path entry : stream) {
				if (InvertedIndexProcessor.isTextFile(entry)) {
					// Create a Runnable task for processing each text file
					Runnable task = new Runnable() {
						@Override
						public void run() {
							try {
								processFile(entry, threadSafeIndex);
							} catch (IOException e) {
								System.err.println("Could not process file");
							}
						}
					};
					// Add task to the WorkQueue
					workQueue.execute(task);
				} else if (Files.isDirectory(entry)) {
					// Recursively process subdirectories using multiple threads
					processDirectoryMultiThreaded(entry, threadSafeIndex, workQueue);
				}
			}
		}
	}

}