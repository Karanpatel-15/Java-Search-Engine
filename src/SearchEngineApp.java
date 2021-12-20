import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;


public class SearchEngineApp extends Application{

    GuiSearch model;

    public SearchEngineApp(){model = new GuiSearch();}

    public void start(Stage primaryStage) {
        Pane  aPane = new Pane();

        SearchEngineView view = new SearchEngineView();
        aPane.getChildren().add(view);

        primaryStage.setTitle("Search Engine");
        primaryStage.setResizable(false);
        primaryStage.setScene(new Scene(aPane));
        primaryStage.show();

        view.getSearchButton().setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent actionEvent) {
                String query = view.getSearchBar().getText();
                boolean boost = view.getPageRank().isSelected();
                model.guiSearch(query, boost);
                view.update(model);
            }

        });

    }

    public static void main(String[] args) {
        launch(args);
    }
}
