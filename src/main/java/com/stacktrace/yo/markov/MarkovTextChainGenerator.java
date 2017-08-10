package com.stacktrace.yo.markov;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by Stacktraceyo on 8/10/17.
 */
public class MarkovTextChainGenerator {

    private final Integer order;

    public MarkovTextChainGenerator(Integer order) {
        this.order = order;
    }

    public MarkovTextChainGenerator() {
        order = 2;
    }

    public void generateChainFromStream(InputStream stream) {

        Optional<Map<String, MarkovChainProbabilityDistribution<String>>> distribution = new BufferedReader(new InputStreamReader(stream))
                .lines()
                .map(this::generateFrequency)
                .reduce(this::mergeDistributions);
        if (distribution.isPresent()) {
            MarkovChain chain = new MarkovChain<String>(distribution.get());
            System.out.println(Joiner.on(" ").join(chain.generate(20)));
        }
    }

    private Map<String, MarkovChainProbabilityDistribution<String>> mergeDistributions(Map<String, MarkovChainProbabilityDistribution<String>> left, Map<String, MarkovChainProbabilityDistribution<String>> right) {
        right.forEach((token, rightDist) -> {
            MarkovChainProbabilityDistribution<String> leftDist = left.get(token);
            if (leftDist != null) {
                leftDist.merge(rightDist);
            } else {
                left.put(token, rightDist);
            }
        });
        return left;
    }


    private Map<String, MarkovChainProbabilityDistribution<String>> generateFrequency(String line) {
        Map<String, MarkovChainProbabilityDistribution<String>> frequencyMap = Maps.newConcurrentMap();
        List<String> lineTokens = Splitter.on(" ")
                .splitToList(line)
                .stream()
                .map(token -> token.trim().toLowerCase())
                .filter(token -> !token.isEmpty())
                .collect(Collectors.toList());
        while (!lineTokens.isEmpty()) {
            Iterator<String> remainderQueueIterator = Queues.newArrayDeque(lineTokens).iterator();
            if (remainderQueueIterator.hasNext()) {
                List<String> tokensToJoin = Lists.newArrayList();
                for (int i = 1; i < order; i++) {
                    if (remainderQueueIterator.hasNext()) {
                        tokensToJoin.add(remainderQueueIterator.next());
                    }
                }
                String tokenString = Joiner.on(" ").join(tokensToJoin);
                if (remainderQueueIterator.hasNext()) {
                    String follow = remainderQueueIterator.next();
                    MarkovChainProbabilityDistribution<String> probabilityDistributionRecord = frequencyMap.get(tokenString);
                    if (probabilityDistributionRecord != null) {
                        probabilityDistributionRecord.record(follow);
                    } else {
                        frequencyMap.put(tokenString, new MarkovChainProbabilityDistribution<>(follow));
                    }
                }
            }
            lineTokens.remove(0);
        }
        return frequencyMap;
    }

    public static void main(String[] args) throws IOException {
        new MarkovTextChainGenerator(2)
                .generateChainFromStream(Files.newInputStream(Paths.get("src/main/resources/data/trumptweets.txt")));
//        Map<String, MarkovChainProbabilityDistribution<String>> y = x.generateFrequency("hello whats up lord up");
//        Map<String, MarkovChainProbabilityDistribution<String>> z = x.generateFrequency("hello whats up");
//        Map<String, MarkovChainProbabilityDistribution<String>> z2 = x.mergeDistributions(y, z);
//        Map<String, MarkovChainProbabilityDistribution<String>> z21 = z2;
    }
}
