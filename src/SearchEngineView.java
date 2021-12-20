import javafx.collections.FXCollections;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;

//MVC View
public class SearchEngineView extends Pane{
    private ListView<String> searchResults;
    private TextField searchBar;
    private CheckBox pageRank;
    private Button searchButton;


    public ListView<String> getSearchResults() { return searchResults; }
    public TextField getSearchBar() { return searchBar; }
    public CheckBox getPageRank() { return pageRank; }
    public Button getSearchButton() { return searchButton; }

    public SearchEngineView() {
        Label label1 = new Label("Search Bar");
        label1.relocate(20, 10);
        Label label2 = new Label("Page Rank");
        label2.relocate(230, 10);

        searchBar = new TextField();
        searchBar.relocate(20,30);
        searchBar.setPrefSize(200,40);

        pageRank = new CheckBox();
        pageRank.relocate(250,30);
        pageRank.setPrefSize(40,40);

        searchButton = new Button("Search");
        searchButton.relocate(300,30);
        searchButton.setPrefSize(60,40);

        searchResults = new ListView<String>();
        searchResults.relocate(20, 80);
        searchResults.setPrefSize(340,260);

        // Add all the components to the Pane
        getChildren().addAll(label1, label2, searchResults, searchBar, pageRank, searchButton);

        setPrefSize(380, 360);

    }

    public void update(GuiSearch model){
        String[] result = model.getRanking();
        searchResults.setItems(FXCollections.observableArrayList(result));
    }

}
