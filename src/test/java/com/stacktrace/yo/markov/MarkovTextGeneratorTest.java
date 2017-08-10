package com.stacktrace.yo.markov;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.assertFalse;


public class MarkovTextGeneratorTest {

    @Test
    public void testGeneratorReturnsTokens() throws IOException {
        List<String> tokens = new MarkovTextChainGenerator(2)
                .generateChainFromStream(
                        Files.newInputStream(
                                Paths.get("src/test/resources/data/trumptweets.txt")
                        )
                ).getTokens(20);
        assertFalse(tokens.isEmpty());
        tokens.forEach(token -> System.out.print(token + " "));
    }
}
