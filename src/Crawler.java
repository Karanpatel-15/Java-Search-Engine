import java.io.*;
import java.net.MalformedURLException;
import java.util.*;

public class Crawler extends SearchEngine{

    private List<ArrayList<String>> queue;
    private List<ArrayList<String>> completedQueue;
    private HashMap<String, Integer> uniqueWords;
    private HashMap<String, Integer> pagesWithUniqueWords;
    private int totalPages;

    public Crawler(){
        queue = new LinkedList<>();
        completedQueue = new LinkedList<>();
        uniqueWords = new HashMap<>();
        pagesWithUniqueWords = new HashMap<>();
        totalPages = 0;
    }

    public void initialize(){
        File dataDir = new File(SearchEngine.DATA_DIR);
        if (dataDir.exists()) {
            deleteDir(dataDir);
        }

        dataDir.mkdir();
        new File(SearchEngine.TITLES).mkdir();
        new File(SearchEngine.INCOMING_LINKS).mkdir();
        new File(SearchEngine.OUTGOING_LINKS).mkdir();
        new File(SearchEngine.TF).mkdir();
        new File(SearchEngine.IDF).mkdir();
        new File(SearchEngine.TF_IDF).mkdir();
        new File(SearchEngine.PAGE_RANK).mkdir();

    }

    private ArrayList<String> createSublist(String URL, String index){
        ArrayList<String> queueSublist = new ArrayList<>();
        queueSublist.add(URL);
        queueSublist.add(index);
        return queueSublist;
    }

    private void deleteDir(File dir){
        for(File file: Objects.requireNonNull(dir.listFiles())){
            if (file.isFile())
                file.delete();
            else
                deleteDir(file);
        }
        dir.delete();
    }


    public int crawl(String seedURL){

        queue.add(createSublist(seedURL, "0"));

        while(queue.size() != 0){

            boolean success = false;
            String currentUrl = null;
            String index = null;
            WebPage page = null;
            while (!success) {
                try {
                    currentUrl = queue.get(0).get(0);
                    index = queue.get(0).get(1);
                    page = new WebPage(currentUrl, index);
                    page.parse();
                    success = true;
                } catch (MalformedURLException e) {
                    queue.remove(0);
                    success = false;
                }
                catch (IOException e) {
                    queue.add(queue.remove(0));
                    success = false;
                }
            }
            String content = index + "," + currentUrl + "\n";
            writeDataInFile(SearchEngine.MAPPING_FILE, content);

            String filePath = SearchEngine.TITLES + File.separator + index + SearchEngine.FILE_EXTENSION;
            writeDataInFile(filePath, page.getTitle());

            saveTf(page);

            saveOutgoingAndIncomingLink(page);

            completedQueue.add(queue.remove(0));

        }

        totalPages += 1;
        saveIdfAndTfIdf();

        writeDataInFile(SearchEngine.TOTAL_PAGES_FILE, Integer.toString(totalPages));

        PageRank.pageRankCalculator();


        return totalPages;

    }

    private void saveTf(WebPage page){

        String[] textList = page.getWords();

        int textLength = textList.length;

        HashMap<String, Integer> differentWordCount = new HashMap<>();

        for (String word : textList) {

            if (differentWordCount.containsKey(word)) {
                differentWordCount.put(word, differentWordCount.get(word) + 1);
                uniqueWords.put(word, uniqueWords.get(word) + 1);
            } else {
                differentWordCount.put(word, 1);

                if (!uniqueWords.containsKey(word)) {
                    uniqueWords.put(word, 1);
                    pagesWithUniqueWords.put(word, 1);
                } else {
                    uniqueWords.put(word, uniqueWords.get(word) + 1);
                    pagesWithUniqueWords.put(word, pagesWithUniqueWords.get(word) + 1);
                }

            }
        }

        for(String word : differentWordCount.keySet()){
            String filePath = SearchEngine.TF + File.separator + word + "_" + page.getIndex() + SearchEngine.FILE_EXTENSION;
            String content = Double.toString((double)differentWordCount.get(word)/textLength);
            writeDataInFile(filePath, content);
        }
    }

    private void saveOutgoingAndIncomingLink(WebPage page){
        String[] outgoingLinkList = page.getOutgoingLinks();

        try{
            StringBuilder outLinkText = new StringBuilder();
            for(int i = 1; i < outgoingLinkList.length; i++){

                String outLink = outgoingLinkList[i];

                if(!existInQueue(queue,outLink) && !existInQueue(completedQueue,outLink)){
                    totalPages++;
                    queue.add(createSublist(outLink, Integer.toString(totalPages)));
                }

                outLinkText.append(outLink).append("\n");

                writeDataInFile(SearchEngine.INCOMING_LINKS + File.separator + findIndexInQueue(outLink) + SearchEngine.FILE_EXTENSION, page.getURL() + "\n");
            }

            writeDataInFile(SearchEngine.OUTGOING_LINKS + File.separator + page.getIndex() + SearchEngine.FILE_EXTENSION, outLinkText.toString());

        }catch(Exception e){
            e.printStackTrace();
            System.out.println("Exception in writeDataInFile");
        }

    }

    private boolean existInQueue(List<ArrayList<String>> queue, String URL){
        for (ArrayList<String> strings : queue) {
            if (URL.equals(strings.get(0)))
                return true;
        }
        return false;
    }

    private String findIndexInQueue(String URL){
        for (ArrayList<String> strings : queue) {
            if (URL.equals(strings.get(0)))
                return strings.get(1);
        }
        for (ArrayList<String> strings : completedQueue) {
            if (URL.equals(strings.get(0)))
                return strings.get(1);
        }
        return null;
    }

    private void saveIdfAndTfIdf(){
        for(String word : uniqueWords.keySet()){
            String filePath = SearchEngine.IDF + File.separator + word + SearchEngine.FILE_EXTENSION;
            double idfValue = Math.log((double)totalPages / (1+pagesWithUniqueWords.get(word))) / Math.log(2);
            writeDataInFile(filePath, Double.toString(idfValue));

            for(int i=0; i < totalPages; i++){
                filePath = SearchEngine.TF + File.separator + word + "_" + i + SearchEngine.FILE_EXTENSION;
                File tfFileName = new File (filePath);

                if(tfFileName.exists()){
                    double tfValue = Double.parseDouble(readDataValue(filePath));
                    double tfIdfValue = (Math.log(1+tfValue) / Math.log(2)) * idfValue;
                    filePath = SearchEngine.TF_IDF + File.separator + word + "_" + i + SearchEngine.FILE_EXTENSION;

                    writeDataInFile(filePath, Double.toString(tfIdfValue));

                }
            }

        }
    }

}
