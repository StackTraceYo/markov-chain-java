package com.stacktrace.yo.markov;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by Stacktraceyo on 8/10/17.
 */
public class MarkovChain<T> {

    private final Random random = new Random(System.currentTimeMillis());
    private final Map<T, MarkovChainProbabilityDistribution<T>> distribution;
    private final List<T> distributionKeySet;

    public MarkovChain(Map<T, MarkovChainProbabilityDistribution<T>> distribution) {
        this.distribution = distribution;
        distributionKeySet = Lists.newArrayList(distribution.keySet());
    }

    //
    public List<T> generate(Integer maxTokens) {
        List<T> tokens = Lists.newArrayList();

        //start
        DistributionRecord<T> currentRecord = getRandomRecord();
        MarkovChainProbabilityDistribution<T> currentProbability = currentRecord.record;
        T currentToken = currentRecord.tokenUsed;

        boolean continueChain = true;
        tokens.add(currentToken);

        while (continueChain) {
            if (currentProbability != null) {
                T nextToken = currentProbability.getNext();
                currentRecord = new DistributionRecord<T>(nextToken, currentProbability);
                tokens.add(nextToken);
            } else {
                //end
                continueChain = false;
            }
        }
        return tokens;
    }

    private DistributionRecord<T> getRandomRecord() {
        T token = distributionKeySet.get(getRandomIndex());
        MarkovChainProbabilityDistribution<T> x = distribution.get(token);
        return new DistributionRecord<>(token, x);
    }

    private int getRandomIndex() {
        return random.nextInt(distributionKeySet.size());
    }

    static final class DistributionRecord<T> {

        private final T tokenUsed;
        private final MarkovChainProbabilityDistribution<T> record;

        DistributionRecord(T tokenUsed, MarkovChainProbabilityDistribution<T> record) {
            this.tokenUsed = tokenUsed;
            this.record = record;
        }
    }

}
