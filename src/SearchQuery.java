import java.io.File;
import java.util.*;

public class SearchQuery extends SearchEngine{

    public SearchQuery(){}

    public List<SearchResult> search(String phrase, boolean boost, int X) {

        List<String> phraseList = List.of(phrase.split(" "));
        int phraseWords = phraseList.size();

        List<String> uniquePhraseWordsVector = new ArrayList<>();
        List<Double> queryVector = new ArrayList<>();

        int uniqueWords = 0;

        for (String word : phraseList) {
            if (uniquePhraseWordsVector.contains(word))
                continue;

            uniquePhraseWordsVector.add(word);
            uniqueWords++;

            String filePath = SearchEngine.IDF + File.separator + word + SearchEngine.FILE_EXTENSION;
            double idf = Double.parseDouble(SearchEngine.readDataValue(filePath));

            queryVector.add( (Math.log10(1 + (  (double) Collections.frequency(phraseList, word) / phraseWords)) / Math.log10(2)) * idf);

        }

        //read total pages;
        int totalPages = Integer.parseInt(SearchEngine.readDataValue(SearchEngine.TOTAL_PAGES_FILE));
        Double[][] pageVector = new Double[totalPages][uniqueWords];
        for (int i = 0; i < totalPages; i++) {
            for (int j = 0; j < uniqueWords; j++) {
                String tfIdfPath = SearchEngine.TF_IDF + File.separator + uniquePhraseWordsVector.get(j) + "_" + i + SearchEngine.FILE_EXTENSION;
                pageVector[i][j] = Double.parseDouble(SearchEngine.readDataValue(tfIdfPath));
            }
        }

        Double[] cosineSimilarity = new Double[totalPages];
        for (int i = 0; i < totalPages; i++) {
            double numerator = 0;
            double leftDenominator = 0;
            double rightDenominator = 0;

            for (int j = 0; j < queryVector.size(); j++) {
                numerator += queryVector.get(j) * pageVector[i][j];
                leftDenominator += Math.pow(queryVector.get(j), 2);
                rightDenominator += Math.pow(pageVector[i][j], 2);
            }

            if (leftDenominator * rightDenominator == 0)
                cosineSimilarity[i] = 0.0;
            else if (boost)
                cosineSimilarity[i] = (numerator/(Math.sqrt(leftDenominator)*Math.sqrt(rightDenominator)))*PageRank.getPageRankByIndex(String.valueOf(i));
            else
                cosineSimilarity[i] = (numerator / (Math.sqrt(leftDenominator) * Math.sqrt(rightDenominator)));
        }

        List<SearchResult> rankedList = new ArrayList<>();
        for (int i = 0; i < X; i++) {

            int index = 0;
            for (int j = 0; j < cosineSimilarity.length - 1; j++) {

                double currentScore = Double.parseDouble(String.format("%.3f", cosineSimilarity[j + 1]));
                double largestScore = Double.parseDouble(String.format("%.3f", cosineSimilarity[index]));

                if (currentScore > largestScore) {
                    index = j + 1;
                } else if (currentScore == largestScore) {

                    String currentScoreTitle = SearchEngine.readDataValue(SearchEngine.TITLES + File.separator + (j+1) + SearchEngine.FILE_EXTENSION);
                    String largestScoreTitle = SearchEngine.readDataValue(SearchEngine.TITLES + File.separator + index + SearchEngine.FILE_EXTENSION);
                    if ((currentScoreTitle).compareTo(largestScoreTitle) < 0) {
                        index = j + 1;
                    }
                }

            }
            String title = SearchEngine.readDataValue(SearchEngine.TITLES + File.separator + index + SearchEngine.FILE_EXTENSION);
            rankedList.add( new SearchResultImp(title, cosineSimilarity[index]));
            cosineSimilarity[index] = -1.0;
        }
        return rankedList;
    }

}
