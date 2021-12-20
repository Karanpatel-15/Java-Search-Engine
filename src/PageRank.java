import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class PageRank extends SearchEngine{
    private static final double ALPHA = 0.1;

    public static void pageRankCalculator(){
        int totalPages = Integer.parseInt(readDataValue(TOTAL_PAGES_FILE));

        Double[][] adjacencyMatrix = new Double[totalPages][totalPages];

        for (int i=0; i < adjacencyMatrix.length; i++)
            Arrays.fill(adjacencyMatrix[i], 0.0);

        File outDir = new File(SearchEngine.OUTGOING_LINKS);

        for (File file : Objects.requireNonNull(outDir.listFiles())) {

            int row = Integer.parseInt(file.getName().substring(0,file.getName().indexOf(".")));

            List<String> outLinks = readLinks(file.toPath().toString());
            for(String link: Objects.requireNonNull(outLinks)){
                int column = Integer.parseInt(getMappingIndex(link));
                adjacencyMatrix[row][column] = 1.0;
            }
        }

        for(int row = 0; row < totalPages; row++){
            int onesInRow = 0;
            for(int i=0; i<adjacencyMatrix[row].length; i++) {
                if (adjacencyMatrix[row][i] == 1.0)
                    onesInRow++;
            }
            for(int column = 0; column < adjacencyMatrix[row].length; column++)
                adjacencyMatrix[row][column] = (adjacencyMatrix[row][column]/onesInRow)*(1-ALPHA)+(ALPHA/totalPages);
        }

        Double[][] previousIterations = new Double[1][totalPages];
        for(int i=0; i<totalPages; i++)
            previousIterations[0][i] = (1/(double)totalPages);

        Double[][] currentIterations = Matmult.mult_matrix(previousIterations, adjacencyMatrix);

        while (Matmult.euclidean_dist(previousIterations, currentIterations) >= 0.0001){
            previousIterations = currentIterations;
            currentIterations = Matmult.mult_matrix(previousIterations, adjacencyMatrix);
        }

        for(int i=0; i<currentIterations[0].length; i++)
            writeDataInFile(SearchEngine.PAGE_RANK + File.separator + i + SearchEngine.FILE_EXTENSION, String.valueOf(currentIterations[0][i]));

    }

    public static double getPageRank(String URL){
        String index = SearchEngine.getMappingIndex(URL);
        if (index == null)
            return -1.0;

        return getPageRankByIndex(index);
    }

    protected static double getPageRankByIndex(String index){
        String filePath = SearchEngine.PAGE_RANK + File.separator + index + SearchEngine.FILE_EXTENSION;
        return Double.parseDouble(SearchEngine.readDataValue(filePath));
    }

}
