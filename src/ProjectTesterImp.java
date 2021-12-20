import java.util.List;

public class ProjectTesterImp implements ProjectTester{

    private Crawler crawler;
    private SearchQuery searchQuery;

    public ProjectTesterImp(){
        crawler = new Crawler();
        searchQuery = new SearchQuery();
    }

    public void initialize(){
        crawler.initialize();
    }

    public void crawl(String seedURL){
        crawler.crawl(seedURL);
    }

    public List<String> getOutgoingLinks(String url){
        return SearchEngine.getLinks(SearchEngine.OUTGOING_LINKS, url);
    }

    public List<String> getIncomingLinks(String url){
        return SearchEngine.getLinks(SearchEngine.INCOMING_LINKS, url);
    }

    public double getPageRank(String url){
        return PageRank.getPageRank(url);
    }

    public double getIDF(String word){
        return SearchEngine.getData(SearchEngine.IDF, word);
    }

    public double getTF(String url, String word){
        return SearchEngine.getData(SearchEngine.TF, word, url);
    }

    public double getTFIDF(String url, String word){
        return SearchEngine.getData(SearchEngine.TF_IDF, word, url);
    }

    public List<SearchResult> search(String query, boolean boost, int X){return searchQuery.search(query, boost, X);}
}
