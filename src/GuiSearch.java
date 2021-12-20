import java.util.List;

//MVC Model
public class GuiSearch extends SearchQuery {

    private String[] ranking;

    public GuiSearch(){
        ranking = new String[10];
    }

    public String[] getRanking(){
        return ranking;
    }

    public void guiSearch(String phrase, boolean boost) {
        List<SearchResult> results = search(phrase, boost, 10);
        for(int i=0; i < results.size(); i++){
            ranking[i] = String.format("%-5s %-8s %s", (i+1) + ".", results.get(i).getTitle(), results.get(i).getScore());
        }
    }
}
