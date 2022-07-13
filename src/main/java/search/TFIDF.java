package search;

import model.DocumentData;

import java.util.*;

/**
 * The TFIDF class contains all the methods needed to implement to capture the search query
 * in order to match it with the most relevant documents
 *
 */
public class TFIDF {
    /**
     * Calculate the term frequency of a term in comparison to the total number of word in a documents
     *
     * @param words total number of word in a document
     * @param term  the word to be searched in the document, extracted from the search query
     * @return     a double, the termFrequency being the result from division of the number of time the term appears in the document by the total number of word the document has
     * */
    public static double calculateTF(List<String> words, String term){
        long count = 0;
        for(String word : words){
            if(term.equalsIgnoreCase(word)){
                count ++;
            }
        }

        double termFrequency = (double) count/words.size();
        return termFrequency;
    }

    /**
     * Create for a given document a map object where each term of the search query is associated to its term frequency
     *
     * @param words total number of word in a document
     * @param terms list of terms in a search query
     *
     * @return      a DocumentData, a class of map object to which a term along with its frequency are added.
     * */
    public static DocumentData createDocumentData(List<String> words, List<String> terms){
        DocumentData documentData = new DocumentData();

        for(String term : terms){
            double termFreq = calculateTF(words, term);
            documentData.putTermFrequency(term, termFreq);
        }

        return documentData;
    }

    /**
     * Calculate the inverse document frequency of a term for every single stored documents
     *
     * @param term a search query term which the IDF is calculated for
     * @param documentResults object that maps a document to a map of terms and their term frequencies
     *
     * @return      a double, the IDF of a given term
     * */
    private static double getIDF(String term, Map<String, DocumentData> documentResults){
        double nt = 0;

        for (String document : documentResults.keySet()){
            DocumentData documentData = documentResults.get(document);
            double termFreq = documentData.getFrequency(term);
            if (termFreq > 0.0){
                nt ++;
            }
        }

        return nt == 0 ? 0 : Math.log10(documentResults.size()/nt);
    }

    /**
     * Calculate the inverse document frequency for a list of terms
     *
     * @param terms list of terms
     * @param documentResults map of documents to map of term to term frequency
     *
     * @result a map, of a term to its IDF
     * */
    private static Map<String, Double> getTermToInverseDocumentFrequencyMap(List<String> terms, Map<String, DocumentData> documentResults){
        Map<String, Double> termToIDF = new HashMap<>();
        for (String term : terms){
            double idf = getIDF(term, documentResults);
            termToIDF.put(term, idf);
        }
        return termToIDF;
    }

    /**
     * Calculate the document score for a list of terms contained in a search query
     *
     * @param terms List terms of a search query
     * @param documentData Map of a terms to their frequencies
     *
     * @return a double, the document score for a specific search query
     * */
    private static double calculateDocumentScore(List<String> terms, DocumentData documentData, Map<String, Double> termToInverseDocumentFrequency){
        double score = 0;

        for(String term : terms){
            double termFrequency = documentData.getFrequency(term);
            double inverseTermFrequency = termToInverseDocumentFrequency.get(term);
            score += termFrequency * inverseTermFrequency;
        }
        return score;

    }

    /**
     * Return documents sorted by score in descending order, from the most relevant documents to the least relevant
     *
     * @param terms list of terms which TF and IDF are generated for
     * @param documentResults a map of a document to its data
     * */
    public static Map<Double, List<String>> getDocumentsSortedByScore(List<String> terms, Map<String, DocumentData> documentResults){
        TreeMap<Double, List<String>> scoreToDocuments = new TreeMap<>();
        Map<String, Double> termToInverseDocumentFrequency = getTermToInverseDocumentFrequencyMap(terms, documentResults);

        for (String document : documentResults.keySet()){
            DocumentData documentData = documentResults.get(document);
            double score = calculateDocumentScore(terms, documentData, termToInverseDocumentFrequency);

            addDocumentScoreToTreeMap(scoreToDocuments, score, document);
        }
        return scoreToDocuments.descendingMap();
    }

    /**
     * Put a document and its score into the treeMap scoreToDoc
     *
     * @param scoreToDoc the treeMap which a pair of score and document is added into
     * @param score
     * @param document
     * */
    public static void addDocumentScoreToTreeMap(TreeMap<Double, List<String>> scoreToDoc, double score, String document){
        List<String> documentsWithCurrentScore = scoreToDoc.get(score);
        if(documentsWithCurrentScore == null){
            documentsWithCurrentScore = new ArrayList<>();
        }
        documentsWithCurrentScore.add(document);
        scoreToDoc.put(score, documentsWithCurrentScore);
    }


    /**
     * Return an array of strings from a search query or a line of a document
     *
     * @param line
     * */
    public static List<String> getWordFromLine(String line){
        return Arrays.asList(line.split("(\\.)+|(,)+|( )+|(-)+|(\\?)+|(!)+|(;)+|(:)+|(/d)+|(/n)+"));
    }

    /**
     * Add every single line of a document to a list
     *
     * @param lines
     * */
    public static List<String> getWordsFromLines(List<String> lines){
        List<String> words = new ArrayList<>();
        for (String line : lines){
            words.addAll(getWordFromLine(line));
        }
        return words;
    }

    
}
