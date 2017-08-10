package com.stacktrace.yo.markov;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by Stacktraceyo on 8/10/17.
 *
 * @param <T> the type parameter.
 *            <p>
 *            This class represents the final "chain" or probability state machine.
 */
public class MarkovChain<T> {

    private final Random random = new Random(System.currentTimeMillis());
    private final Map<T, MarkovChainProbabilityDistribution<T>> distribution;
    private final List<T> distributionKeySet;

    /**
     * Instantiates a new Markov chain.
     *
     * @param distribution the distribution
     */
    public MarkovChain(Map<T, MarkovChainProbabilityDistribution<T>> distribution) {
        this.distribution = distribution;
        distributionKeySet = Lists.newArrayList(distribution.keySet());
    }

    /**
     * Generate list. This returns the list of generated tokens based on the probability distribution
     * used to construct this class.
     *
     * @param maxTokens the max tokens, the chain will continue adding tokens untill there is no where to chain after or the max token count is reached.
     * @return the list of tokens generated
     */
    public List<T> generateTokens(Integer maxTokens) {
        return generate(maxTokens);
    }

    /**
     * Generate list. This returns the list of generated tokens based on the probability distribution
     * used to construct this class. Continues until no chain link is found
     *
     * @return the list of tokens generated
     */
    public List<T> generateTokens() {
        return generate(Integer.MAX_VALUE);
    }

    private List<T> generate(Integer maxTokens) {
        List<T> tokens = Lists.newArrayList();

        //start
        DistributionRecord<T> currentRecord = getRandomRecord(); //begin with random record
        MarkovChainProbabilityDistribution<T> currentProbability = currentRecord.record;
        T currentToken = currentRecord.tokenUsed;

        boolean continueChain = true;
        tokens.add(currentToken); // add starting token

        while (continueChain && tokens.size() < maxTokens) {
            if (currentProbability != null) { //if the current token has a distribution
                T nextToken = currentProbability.getNext(); //get a random next token
                currentRecord = getNextRecord(nextToken); //find this record in master distribution
                tokens.add(nextToken); // add new token
                currentProbability = currentRecord.record; //set current probability distribution to next link in chain
            } else {
                //end
                continueChain = false; //this token was never followed by anything
            }
        }
        return tokens;
    }

    /**
     * Gets a random record by getting a random token
     */
    private DistributionRecord<T> getRandomRecord() {
        T token = distributionKeySet.get(getRandomIndex());
        MarkovChainProbabilityDistribution<T> x = distribution.get(token);
        return new DistributionRecord<>(token, x);
    }

    /**
     * Gets the next record based on based in token.
     */
    private DistributionRecord<T> getNextRecord(T token) {
        MarkovChainProbabilityDistribution<T> x = distribution.get(token);
        return new DistributionRecord<>(token, x);
    }

    private int getRandomIndex() {
        return random.nextInt(distributionKeySet.size());
    }

    /**
     * The type Distribution record.
     *
     * @param <T> the type parameter
     */
    static final class DistributionRecord<T> {

        private final T tokenUsed;
        private final MarkovChainProbabilityDistribution<T> record;

        /**
         * Instantiates a new Distribution record.
         *
         * @param tokenUsed the token used to retrieve the accompanied MarkovChainProbabilityDistribution
         * @param record    the record
         */
        DistributionRecord(T tokenUsed, MarkovChainProbabilityDistribution<T> record) {
            this.tokenUsed = tokenUsed;
            this.record = record;
        }
    }

}
