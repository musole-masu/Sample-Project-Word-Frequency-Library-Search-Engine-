package model;

import java.util.HashMap;
import java.util.Map;

/**
 * The DocumentData helper class that has a map of a term to its term frequency for a given document
 * */
public class DocumentData {
    private Map<String, Double> termToFrequency = new HashMap<>();

    /**
     * Add a term and its term frequency for a given document
     *
     * @param term
     * @param frequency
     * */
    public void putTermFrequency(String term, double frequency){
        termToFrequency.put(term, frequency);
    }

    /**
     * Return the term frequency of a given term in a document
     *
     * @param term
     *
     * @return  a double as term frequency
     * */
    public double getFrequency(String term){
        return termToFrequency.get(term);
    }
}
