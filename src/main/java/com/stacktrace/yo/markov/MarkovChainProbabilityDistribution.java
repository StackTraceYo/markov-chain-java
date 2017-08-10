package com.stacktrace.yo.markov;

import com.google.common.collect.Maps;

import java.util.Map;
import java.util.Random;

/**
 * Created by Stacktraceyo on 8/10/17.
 */
public class MarkovChainProbabilityDistribution<T> {

    private final Map<T, Integer> distributionRecords;
    private final Random random = new Random(System.currentTimeMillis());

    public MarkovChainProbabilityDistribution(T initRecord) {
        distributionRecords = Maps.newHashMap();
        record(initRecord, 1);
    }

    public MarkovChainProbabilityDistribution() {
        distributionRecords = Maps.newHashMap();
    }

    public T getNext() {
        return pickNextToken(random.nextInt(getDistributionSize()));
    }

    private T pickNextToken(Integer roll) {
        Integer current = 0;
        for (T token : distributionRecords.keySet()) {
            if (current >= roll) {
                if (roll < current + distributionRecords.get(token)) {
                    return token;
                } else {
                    current += distributionRecords.get(token);
                }
            }
        }
        throw new IllegalStateException("Probabilities did not add Up Properly -> Did you generate them?");
    }

    public Map<T, Integer> record(T in) {
        return record(in, 1);
    }

    public Map<T, Integer> record(T in, Integer count) {
        distributionRecords.merge(in, count,
                (oldCount, addCount) -> (oldCount + addCount));
        return distributionRecords;
    }

    public MarkovChainProbabilityDistribution<T> merge(MarkovChainProbabilityDistribution<T> distribution) {
        distribution.distributionRecords.forEach(this::record);
        return this;
    }

    public Integer getTokenFrequency(T token) {
        return distributionRecords.getOrDefault(token, 0);
    }

    public Long getProbabilityOfToken(T token) {
        return getTokenFrequency(token) / Math.round(new Double(getDistributionSize()));
    }

    public Map<T, Integer> getDistributionRecords() {
        return distributionRecords;
    }

    public Integer getDistributionSize() {
        return distributionRecords.size();
    }


//    public static final class DistributionRecord<T> {
//
//        private final T token;
//        private Integer frequency;
//
//        public DistributionRecord(T token, Integer frequency) {
//            this.token = token;
//            this.frequency = frequency;
//        }
//
//        public T getToken() {
//            return token;
//        }
//
//        public Integer getFrequency() {
//            return frequency;
//        }
//
//        public Integer addCount() {
//            frequency++;
//            return frequency;
//        }
//    }
}
