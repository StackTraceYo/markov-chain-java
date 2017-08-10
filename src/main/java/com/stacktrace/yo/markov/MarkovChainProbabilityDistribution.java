package com.stacktrace.yo.markov;

import com.google.common.collect.Maps;

import java.util.Map;
import java.util.Random;

/**
 * Created by Stacktraceyo on 8/10/17.
 *
 * @param <T> the type parameter
 */
public class MarkovChainProbabilityDistribution<T> {

    private final Map<T, Integer> distributionRecords;
    private final Random random = new Random(System.currentTimeMillis());

    /**
     * Instantiates a new Markov chain probability distribution.
     *
     * @param initRecord the init record
     */
    public MarkovChainProbabilityDistribution(T initRecord) {
        distributionRecords = Maps.newHashMap();
        record(initRecord, 1);
    }

    /**
     * Instantiates a new Markov chain probability distribution.
     */
    public MarkovChainProbabilityDistribution() {
        distributionRecords = Maps.newHashMap();
    }

    /**
     * Gets next.
     *
     * @return the next
     */
    public T getNext() {
        return pickNextToken(random.nextInt(getDistributionSize()));
    }

    private T pickNextToken(Integer roll) {
        Integer current = 0;
        for (T token : distributionRecords.keySet()) {
            if (roll >= current) {
                if (roll < current + distributionRecords.get(token)) {
                    return token;
                } else {
                    current += distributionRecords.get(token);
                }
            }
        }
        throw new IllegalStateException("Probabilities did not add Up Properly -> Did you generate them?");
    }

    /**
     * Record map.
     *
     * @param in the in
     * @return the map
     */
    public Map<T, Integer> record(T in) {
        return record(in, 1);
    }

    /**
     * Record map.
     *
     * @param in    the in
     * @param count the count
     * @return the map
     */
    public Map<T, Integer> record(T in, Integer count) {
        distributionRecords.merge(in, count,
                (oldCount, addCount) -> (oldCount + addCount));
        return distributionRecords;
    }

    /**
     * Merge markov chain probability distribution.
     *
     * @param distribution the distribution
     * @return the markov chain probability distribution
     */
    public MarkovChainProbabilityDistribution<T> merge(MarkovChainProbabilityDistribution<T> distribution) {
        distribution.distributionRecords.forEach(this::record);
        return this;
    }

    /**
     * Gets token frequency.
     *
     * @param token the token
     * @return the token frequency
     */
    public Integer getTokenFrequency(T token) {
        return distributionRecords.getOrDefault(token, 0);
    }

    /**
     * Gets probability of token.
     *
     * @param token the token
     * @return the probability of token
     */
    public Long getProbabilityOfToken(T token) {
        return getTokenFrequency(token) / Math.round(new Double(getDistributionSize()));
    }

    /**
     * Gets distribution records.
     *
     * @return the distribution records
     */
    public Map<T, Integer> getDistributionRecords() {
        return distributionRecords;
    }

    /**
     * Gets distribution size.
     *
     * @return the distribution size
     */
    public Integer getDistributionSize() {
        return distributionRecords.size();
    }
}
