package edu.usfca.cs272;

import java.io.IOException;
import java.nio.file.Path;

/**
 * This class is responsible for processing text files and generating word
 * counts as well as an inverted index. It takes command-line arguments for
 * input and output file paths.
 */
public class Driver {
	/**
	 * Main method to process text files, generate word counts, and create an
	 * inverted index.
	 *
	 * @param args Command-line arguments.
	 */
	public static void main(String[] args) {

		if (args.length == 0) {
			System.err.println("Error: No arguments given.");
			return;
		}

		ArgumentParser parser = new ArgumentParser(args);

		InvertedIndex index = null;
		ThreadSafeInvertedIndex threadSafeIndex = null;
		WorkQueue workQueue = null;
		QueryProcessorInterface queryProcessor = null;

		if (parser.hasFlag("-threads")) {
			try {
				int numThreads = parser.getInteger("-threads");
				if (numThreads < 1) {
					numThreads = 5;
				}
				threadSafeIndex = new ThreadSafeInvertedIndex();
				workQueue = new WorkQueue(numThreads);

				index = threadSafeIndex;
				queryProcessor = new MultiThreadedQueryProcessor(threadSafeIndex, parser.hasFlag("-partial"),
						workQueue);
			} catch (NumberFormatException e) {
				System.err.println("Invalid number of threads.");
				return;
			}
		} else {
			index = new InvertedIndex();
			queryProcessor = new QueryProcessor(index, parser.hasFlag("-partial"));
		}

		if (parser.hasFlag("-text")) {
			Path textPath = parser.getPath("-text");
			if (textPath != null) {
				try {
					if (parser.hasFlag("-threads")) {
						MultiThreadedIndexProcessor.processPath(textPath, threadSafeIndex, workQueue);
					} else {
						InvertedIndexProcessor.processPath(textPath, index);
					}
				} catch (IOException e) {
					System.err.println("Could not process text file from: " + textPath);
				}
			}
		}

		if (parser.hasFlag("-query")) {
			Path queryPath = parser.getPath("-query");
			if (queryPath != null) {
				try {
					queryProcessor.processPath(queryPath);
				} catch (IOException e) {
					System.err.println("Unable to process query path");
				}
			}
		}

		if (workQueue != null) {
			workQueue.shutdown();
		}

		if (parser.hasFlag("-index")) {
			Path indexPath = parser.getPath("-index", Path.of("index.json"));
			try {
				index.writeJson(indexPath);
			} catch (IOException e) {
				System.err.println("Could not write index to JSON file: " + indexPath);
			}
		}

		if (parser.hasFlag("-counts")) {
			Path countsPath = parser.getPath("-counts", Path.of("counts.json"));
			try {
				JsonWriter.writeObject(index.getCounts(), countsPath);
			} catch (IOException e) {
				System.err.println("Could not write counts to JSON file: " + countsPath);
			}
		}

		// Process queries and get results
		if (parser.hasFlag("-results")) {
			// Output search results to JSON file
			Path resultsPath = parser.getPath("-results", Path.of("results.json"));
			try {
				queryProcessor.writeJson(resultsPath);
			} catch (IOException e) {
				System.err.println("Could not write results to JSON file: " + resultsPath);
			}
		}

	}
}