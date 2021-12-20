import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SearchEngine{

    protected static final String DATA_DIR = "." + File.separator + "data";
    protected static final String FILE_EXTENSION = ".txt";

    protected static final String TITLES = DATA_DIR + File.separator + "titles";
    protected static final String PAGE_RANK = DATA_DIR + File.separator + "page_rank";

    protected static final String MAPPING_FILE = DATA_DIR + File.separator + "mapping" + FILE_EXTENSION;
    protected static final String TOTAL_PAGES_FILE = DATA_DIR + File.separator + "total_pages" + FILE_EXTENSION;

    //required by public (similar to pi in Math class)
    public static final String IDF = DATA_DIR + File.separator + "idf";
    public static final String TF = DATA_DIR + File.separator + "tf";
    public static final String TF_IDF = DATA_DIR + File.separator + "tf_idf";
    public static final String INCOMING_LINKS = DATA_DIR + File.separator + "incoming_links";
    public static final String OUTGOING_LINKS = DATA_DIR + File.separator + "outgoing_links";

    protected static String readDataValue(String filePath){
        String dataValue = null;

        try {
            BufferedReader file = new BufferedReader(new FileReader(filePath));
            dataValue = file.readLine().strip();
            file.close();
        } catch (FileNotFoundException e) {
            return "0";
        }
        catch (Exception e) {
            System.out.println("Exception in readDataValue: " + filePath);
            e.printStackTrace();
        }
        return dataValue;
    }

    public static double getData(String dataType, String word){
        return getData(dataType, word, null);
    }

    public static double getData(String dataType, String word, String URL){
        String filePath = dataType + File.separator + word;
        if (dataType.equals(TF) || dataType.equals(TF_IDF))
            filePath +=  "_" + getMappingIndex(URL);

        filePath += FILE_EXTENSION;

        return Double.parseDouble(readDataValue(filePath));
    }


    protected static List<String> readLinks(String filePath){
        List<String> listOfLinks = new ArrayList<>();
        try {
            BufferedReader file = new BufferedReader(new FileReader(filePath));
            String line = file.readLine();

            while (line != null) {
                listOfLinks.add(line.substring(line.indexOf(",")+1).strip());
                line = file.readLine();
            }
            file.close();
        } catch (FileNotFoundException e) {
            return null;
        } catch (Exception e) {
            System.out.println("Exception in readLinks: " + filePath);
            e.printStackTrace();
        }
        return listOfLinks;
    }

    public static List<String> getLinks(String linkType, String URL){
        String filePath = linkType + File.separator + getMappingIndex(URL) + FILE_EXTENSION;
        return readLinks(filePath);
    }

    protected static void writeDataInFile(String filePath, String content){
        try{
            BufferedWriter file = new BufferedWriter(new FileWriter(filePath, true));
            file.write(content);
            file.close();
        }catch(Exception e){
            System.out.println("Exception in writeDataInFile: " + filePath);
            e.printStackTrace();
        }
    }

    protected static String getMappingIndex(String URL){
        String index = null;
        try {
            BufferedReader file = new BufferedReader(new FileReader(MAPPING_FILE));
            String line = file.readLine();

            while (line != null) {
                if (URL.equals(line.substring(line.indexOf(",")+1))){
                    index = line.substring(0, line.indexOf(","));
                    break;
                }
                line = file.readLine();
            }
            file.close();
        } catch (Exception e) {
            System.out.println("Exception in getMappingIndex: " + URL);
            e.printStackTrace();
        }
        return index;
    }
}
