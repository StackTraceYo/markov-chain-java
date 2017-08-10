package com.stacktrace.yo;

import com.stacktrace.yo.markov.MarkovTextChainGenerator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class TrumpTweetTextGenerator {


    public static void main(String[] args) throws IOException {
        List<String> tokens = new MarkovTextChainGenerator(2)
                .generateChainFromStream(
                        Files.newInputStream(
                                Paths.get("src/main/resources/data/trumptweets.txt")
                        )
                ).getTokens(20);
        tokens.forEach(token -> System.out.print(token + " "));
    }
}
