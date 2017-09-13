package coen280.gui;

import coen280.common.DbUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;


public class Controller implements javafx.fxml.Initializable {

    @FXML
    private ChoiceBox<String> andOrChoiceBox;
    @FXML
    private ListView<CheckBox> genreListView;
    @FXML
    private DatePicker fromDatePicker;
    @FXML
    private DatePicker toDatePicker;

    @FXML
    private ListView<CheckBox> countryListView;

    @FXML
    private TextField cast1;
    @FXML
    private TextField cast2;
    @FXML
    private TextField cast3;
    @FXML
    private TextField cast4;
    @FXML
    private Button castButton1;
    @FXML
    private Button castButton2;
    @FXML
    private Button castButton3;
    @FXML
    private Button castButton4;

    @FXML
    private TextField directorText;
    @FXML
    private Button directorButton;

    @FXML
    private ListView<CheckBox> tagsListView;
    @FXML
    private ChoiceBox<String> tagWeightChoiceBox;
    @FXML
    private TextField tagWeightValue;

    @FXML
    private TextArea queryTextArea;

    @FXML
    private Button movieQueryButton;
    @FXML
    private Button userQueryButton;

    @FXML
    private ListView<CheckBox> movieResListView;
    @FXML
    private ListView<String> userResListView;



    private int fromYear;
    private int toYear;
    private List<String> selectedGenres;
    private List<String> selectedCountries;
    private List<String> selectedCasts;
    private String selectedDirector;
    private List<String> selectedTags;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        ObservableList<String> andOrChoice = FXCollections.observableArrayList("OR", "AND");
        andOrChoiceBox.setItems(andOrChoice);
        andOrChoiceBox.getSelectionModel().selectFirst();
        andOrChoiceBox.valueProperty().addListener((ov, oldValue, newValue) -> updateCountryAttribute());

        fromDatePicker.valueProperty().addListener((ov, oldValue, newValue) -> updateCountryAttribute());
        toDatePicker.valueProperty().addListener((ov, oldValue, newValue) -> updateCountryAttribute());

        ObservableList<String> tagWeightChoice = FXCollections.observableArrayList("", "<", ">", "=");
        tagWeightChoiceBox.setItems(tagWeightChoice);
        tagWeightChoiceBox.getSelectionModel().selectFirst();

        directorText.textProperty().addListener((ov, oldValue, newValue) -> updateTagAttribute());
        cast1.textProperty().addListener((ov, oldValue, newValue) -> updateTagAttribute());
        cast2.textProperty().addListener((ov, oldValue, newValue) -> updateTagAttribute());
        cast3.textProperty().addListener((ov, oldValue, newValue) -> updateTagAttribute());
        cast4.textProperty().addListener((ov, oldValue, newValue) -> updateTagAttribute());

        directorButton.setOnAction(new PopupHandler(directorText, this::getAvailableDirector));
        castButton1.setOnAction(new PopupHandler(cast1, this::getAvailableCast));
        castButton2.setOnAction(new PopupHandler(cast2, this::getAvailableCast));
        castButton3.setOnAction(new PopupHandler(cast3, this::getAvailableCast));
        castButton4.setOnAction(new PopupHandler(cast4, this::getAvailableCast));

        movieQueryButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                refreshSelection();
                String totalSqlTemplate = "SELECT q2.movie_id, mm.title, q2.genre, mm.year, mcc.country, mm.rt_audience_rating, mm.rt_audience_num_ratings \n" +
                        "FROM (SELECT listagg(mgg.genre,',') within group(order by mgg.genre) AS genre, q.movie_id \n" +
                        "\tFROM (%s) q, movie_genres mgg \n" +
                        "WHERE q.movie_id = mgg.movie_id \n" +
                        "\tGROUP BY q.movie_id) q2 \n" +
                        "JOIN movie mm ON q2.movie_id = mm.movie_id \n" +
                        "JOIN movie_countries mcc ON q2.movie_id = mcc.movie_id \n" +
                        "ORDER BY q2.movie_id";
                String totalSql = String.format(totalSqlTemplate, buildMovieQueryFinal());
                queryTextArea.setText(totalSql);
                try
                {
                    Connection conn = DbUtils.getConnection();
                    ResultSet totalResultSet = DbUtils.runQuery(conn, totalSql);
                    ObservableList<CheckBox> totalList = FXCollections.observableArrayList();
                    if (totalResultSet != null)
                    {
                        while (totalResultSet.next())
                        {
                            String template = "%s | %s | %s | %s | %s";
                            String row = String.format(template, totalResultSet.getString(1)
                                    , totalResultSet.getString(2), totalResultSet.getString(3)
                                    , totalResultSet.getString(4), totalResultSet.getString(5));
                            CheckBox checkBox = new CheckBox(row);
                            checkBox.setId(totalResultSet.getString(1));

                            totalList.add(checkBox);
                        }
                    }
                    conn.close();
                    movieResListView.setItems(totalList);

                }
                catch (Exception e)
                {
                    showError(e);
                }


            }
        });

        userQueryButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                refreshSelection();
                String sqlTemplate = "SELECT utt.user_id\n" +
                        "FROM user_taggedmovies_timestamps utt\n" +
                        "WHERE utt.tag_id in %s AND utt.movie_id in %s\n" +
                        "ORDER BY utt.user_id";
                String userResSql = String.format(sqlTemplate, buildInClause(findSelectedTags()), buildInClause(findSelectedMovies())) ;
                queryTextArea.setText(userResSql);
                try
                {
                    Connection conn = DbUtils.getConnection();
                    ResultSet userResultSet = DbUtils.runQuery(conn, userResSql);
                    ObservableList<String> userRet = FXCollections.observableArrayList();
                    if(userResultSet != null)
                    {
                        while(userResultSet.next())
                        {
                            String user = userResultSet.getString(1);
                            userRet.add("User Id " + user);

                        }
                    }
                    conn.close();
                    userResListView.setItems(userRet);
                }
                catch (Exception e)
                {
                    showError(e);
                }
            }
        });



        try (Connection connection = DbUtils.getConnection()) {
            ResultSet res = DbUtils.runQuery(connection, "select Distinct genre from movie_genres order by genre");
            ObservableList<CheckBox> checkboxList = FXCollections.observableArrayList();
            if (res != null) {
                while (res.next()) {
                    CheckBox checkBox = new CheckBox(res.getString(1));
                    checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> updateCountryAttribute());
                    checkboxList.add(checkBox);
                }
            }
            genreListView.setItems(checkboxList);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }



    private boolean isAndCondition() {
        return andOrChoiceBox.getValue().equals("AND");
    }


    private String buildInClause(List<String> list) {
        if (list.size() == 0) {
            return null;
        }
        StringBuilder ret = new StringBuilder("('" + list.get(0));
        for (int i = 1; i < list.size(); i++) {
            ret.append("','" + list.get(i));
        }
        ret.append("')");
        return ret.toString();
    }


    private List<String> findSelectedGenres() {
        ObservableList<CheckBox> list = genreListView.getItems();
        List<String> res = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).isSelected()) {
                res.add(list.get(i).getText());
            }
        }
        return res;
    }

    private List<String> findSelectedCountries() {
        ObservableList<CheckBox> countryList = countryListView.getItems();
        List<String> countryRes = new ArrayList<>();
        for (int i = 0; i < countryList.size(); i++) {
            if (countryList.get(i).isSelected()) {
                countryRes.add(countryList.get(i).getText());
            }
        }
        return countryRes;
    }

    private  List<String> findSelectedTags(){
        ObservableList<CheckBox> tagList = tagsListView.getItems();
        List<String> tagRes = new ArrayList<>();
        for(int i = 0; i < tagList.size(); i++)
        {
            if(tagList.get(i).isSelected())
            {
                tagRes.add(tagList.get(i).getId());
            }
        }
        return tagRes;
    }

    private List<String> findSelectedMovies()
    {
        ObservableList<CheckBox> movieList = movieResListView.getItems();
        List<String> movieRes = new ArrayList<>();
        for(int i = 0; i < movieList.size(); i++)
        {
            if(movieList.get(i).isSelected())
            {
                movieRes.add(movieList.get(i).getId());
            }
        }
        return movieRes;

    }

    private void refreshSelection()
    {
        LocalDate fromLocalDate = fromDatePicker.getValue();
        fromYear = Integer.MIN_VALUE;
        if (fromLocalDate != null)
        {
            fromYear = fromLocalDate.getYear();
        }
        toYear = Integer.MAX_VALUE;
        LocalDate toLocalDate = toDatePicker.getValue();
        if (toLocalDate != null)
        {
            toYear = toLocalDate.getYear();
        }

        selectedGenres = findSelectedGenres();

        selectedCountries = findSelectedCountries();

        String[] castsArray = new String[]{cast1.getText(), cast2.getText(), cast3.getText(), cast4.getText()};
        selectedCasts = Arrays.stream(castsArray)
                .filter(Objects::nonNull)
                .filter(x -> !"".equals(x))
                .map(x -> x.trim())
                .distinct()
                .collect(Collectors.toList());

        selectedDirector = directorText.getText();

        selectedTags = findSelectedTags();
    }

    private void showError(Exception e)
    {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Failed");
        alert.setHeaderText("Oops, there was an error");
        alert.setContentText(e.toString());
        alert.showAndWait();
    }

    private void updateCountryAttribute()
    {
        refreshSelection();
        try {
            String movieQuery = buildMovieQueryBeforeCountry();

            String countrySql;

            String countrySqlTemplate = "SELECT DISTINCT mc.country " +
                    "FROM (%s) mq JOIN movie_countries mc ON mq.movie_id = mc.movie_id " +
                    "ORDER BY mc.country";
            countrySql = String.format(countrySqlTemplate, movieQuery);

            Connection conn = DbUtils.getConnection();
            ResultSet countryResultSet = DbUtils.runQuery(conn, countrySql);
            ObservableList<CheckBox> checkboxList = FXCollections.observableArrayList();
            if (countryResultSet != null)
            {
                while (countryResultSet.next())
                {
                    CheckBox checkBox = new CheckBox(countryResultSet.getString(1));
                    checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> updateTagAttribute());
                    checkboxList.add(checkBox);
                }
            }
            conn.close();
            countryListView.setItems(checkboxList);

        }
        catch (Exception e)
        {
            showError(e);
        }
        updateTagAttribute();
    }


    private void updateTagAttribute()
    {
        refreshSelection();
        try
        {
            String movieQuery = buildMovieQueryBeforeTag();

            String tagSql;

            String tagSqlTemplate = "SELECT DISTINCT mt.tag_id, t.value " +
                    "FROM (%s) mq " +
                    "JOIN movie_tags mt on mt.movie_id = mq.movie_id " +
                    "JOIN tag t on t.tag_id = mt.tag_id " +
                    "ORDER BY mt.tag_id";
            tagSql = String.format(tagSqlTemplate, movieQuery);

            Connection conn = DbUtils.getConnection();
            ResultSet tagResultSet = DbUtils.runQuery(conn, tagSql);
            ObservableList<CheckBox> tagCheckboxList = FXCollections.observableArrayList();
            if (tagResultSet != null) {
                while (tagResultSet.next()) {
                    int tagId = tagResultSet.getInt(1);
                    String tagName = tagResultSet.getString(2);
                    CheckBox checkBox = new CheckBox();
                    checkBox.setText(tagId + " " + tagName);
                    checkBox.setId(Integer.toString(tagId));
                    tagCheckboxList.add(checkBox);
                }
            }
            tagsListView.setItems(tagCheckboxList);
            conn.close();
        }
        catch (Exception e)
        {
            showError(e);
        }
    }

    private List<String> getAvailableDirector()
    {
        refreshSelection();
        List<String> directorList = new ArrayList<>();
        try
        {
            String movieQuery = buildMovieQueryBeforeCast();

            String directorSql;

            String directorSqlTemplate = "SELECT DISTINCT md.director_name " +
                    "FROM (%s) mq JOIN movie_director md ON mq.movie_id = md.movie_id " +
                    "ORDER BY md.director_name";
            directorSql = String.format(directorSqlTemplate, movieQuery);

            Connection conn = DbUtils.getConnection();
            ResultSet directorResultSet = DbUtils.runQuery(conn, directorSql);
            if(directorResultSet != null)
            {
               while(directorResultSet.next())
               {
                   directorList.add(directorResultSet.getString(1));
               }
            }
        }
        catch(Exception e)
        {
            showError(e);
        }
        return directorList;
    }

    private List<String> getAvailableCast()
    {
        refreshSelection();
        List<String> castList = new ArrayList<>();
        try
        {
            String movieQuery = buildMovieQueryBeforeCast();

            String castSql;

            String castSqlTemplate = "SELECT DISTINCT ma.actor_name " +
                    "FROM (%s) mq JOIN movie_actor ma ON mq.movie_id = ma.movie_id " +
                    "ORDER BY ma.actor_name";
            castSql = String.format(castSqlTemplate, movieQuery);
            Connection conn = DbUtils.getConnection();
            ResultSet actorResultSet = DbUtils.runQuery(conn, castSql);
            if(actorResultSet != null)
            {
                while(actorResultSet.next())
                {
                    castList.add(actorResultSet.getString(1));
                }
            }
        }
        catch(Exception e)
        {
            showError(e);
        }
        return castList;
    }

    private String buildMovieQueryBeforeCountry()
    {
        String genreInClause = buildInClause(selectedGenres);
        int genreCount = selectedGenres.size();

        String movieQueryBeforeCountry;
        if (isAndCondition())
        {
            String movieQueryBeforeCountryTemplate = "SELECT DISTINCT mqag.movie_id FROM movie mqag, movie_genres mg " +
                    "WHERE mqag.movie_id = mg.movie_id AND mg.genre in %s AND mqag.year <= %d AND mqag.year >= %d " +
                    "GROUP BY mqag.movie_id HAVING count(*) = %d";
            movieQueryBeforeCountry = String.format(movieQueryBeforeCountryTemplate, genreInClause, toYear, fromYear, genreCount);
        }
        else
        {
            String movieQueryBeforeCountryTemplate = "SELECT DISTINCT mqag.movie_id FROM movie mqag, movie_genres mg " +
                    "WHERE mqag.movie_id = mg.movie_id AND mg.genre in %s AND mqag.year <= %d AND mqag.year >= %d";
            movieQueryBeforeCountry = String.format(movieQueryBeforeCountryTemplate, genreInClause, toYear, fromYear);
        }

        return movieQueryBeforeCountry;
    }

    private String buildMovieQueryBeforeCast()
    {
        String countryInClause = buildInClause(selectedCountries);
        int countryCount = selectedCountries.size();

        String movieQueryBeforeCountry = buildMovieQueryBeforeCountry();
        String movieQueryBeforeCast;
        if (countryCount == 0)
        {
            movieQueryBeforeCast = movieQueryBeforeCountry;
        }
        else if (isAndCondition())
        {
            String movieQueryBeforeCastTemplate = "SELECT DISTINCT mqac.movie_id FROM (%s) mqac, movie_countries mc " +
                    "WHERE mc.country in %s AND mqac.movie_id = mc.movie_id " +
                    "GROUP BY mqac.movie_id HAVING count(*) = %d";
            movieQueryBeforeCast = String.format(movieQueryBeforeCastTemplate, movieQueryBeforeCountry, countryInClause, countryCount);
        }
        else
        {
            String movieQueryBeforeCastTemplate = "SELECT DISTINCT mqac.movie_id " +
                    "FROM (%s) mqac, movie_countries mc " +
                    "WHERE mc.country in %s AND mqac.movie_id = mc.movie_id ";
            movieQueryBeforeCast = String.format(movieQueryBeforeCastTemplate, movieQueryBeforeCountry, countryInClause);

        }

        return movieQueryBeforeCast;
    }

    private String buildMovieQueryBeforeTag()
    {
        String castInClause = buildInClause(selectedCasts);
        int castCount = selectedCasts.size();

        String movieQueryBeforeCast = buildMovieQueryBeforeCast();
        String movieQueryBeforeTag;
        if (castCount == 0)
        {
            movieQueryBeforeTag = movieQueryBeforeCast;
        }
        else if (isAndCondition())
        {
            String movieQueryBeforeTagTemplate = "SELECT DISTINCT mqacast.movie_id " +
                    "FROM (%s) mqacast JOIN movie_actor ma ON mqacast.movie_id = ma.movie_id " +
                    "WHERE ma.actor_name in %s " +
                    "GROUP BY mqacast.movie_id HAVING count(*) = %d";
            movieQueryBeforeTag = String.format(movieQueryBeforeTagTemplate, movieQueryBeforeCast, castInClause, castCount);
        }
        else
        {
            String movieQueryBeforeTagTemplate = "SELECT DISTINCT mqacast.movie_id " +
                    "FROM (%s) mqacast JOIN movie_actor ma ON mqacast.movie_id = ma.movie_id " +
                    "WHERE ma.actor_name in %s";
            movieQueryBeforeTag = String.format(movieQueryBeforeTagTemplate, movieQueryBeforeCast, castInClause);
        }

        String movieQueryBeforeTag2;
        if (directorText.getText() == null || directorText.getText().equals(""))
        {
            movieQueryBeforeTag2 = movieQueryBeforeTag;
        }
        else
        {
            String movieQueryBeforeTag2Template = "SELECT DISTINCT mqad.movie_id " +
                    "FROM (%s) mqad JOIN movie_director md ON mqad.movie_id = md.movie_id " +
                    "WHERE md.director_name = '%s'";
            movieQueryBeforeTag2 = String.format(movieQueryBeforeTag2Template, movieQueryBeforeTag, selectedDirector);
        }

        return movieQueryBeforeTag2;
    }

    private String buildMovieQueryFinal()
    {
        String tagInClause = buildInClause(selectedTags);
        int tagCount = selectedTags.size();

        String movieQueryBeforeTag = buildMovieQueryBeforeTag();
        String movieQueryFinal;

        String tagWeightChoice = tagWeightChoiceBox.getSelectionModel().getSelectedItem();
        String tagWeightValueLuke = tagWeightValue.getText();

        String tagWeightCondition = "";
        if (!tagWeightChoice.equals("") && tagWeightValueLuke != null && !tagWeightValueLuke.trim().equals(""))
        {
            tagWeightCondition = "AND mtag.tag_weight " + tagWeightChoice + " " + tagWeightValueLuke;
        }

        if(tagCount == 0)
        {
            movieQueryFinal = movieQueryBeforeTag;
        }
        else if(isAndCondition())
        {
            String movieQueryFinalTemplate = "SELECT DISTINCT mqat.movie_id " +
                    "FROM (%s) mqat JOIN movie_tags mtag ON mtag.movie_id = mqat.movie_id " +
                    "WHERE mtag.tag_id in %s " + tagWeightCondition + " " +
                    "GROUP BY mqat.movie_id HAVING count(*) = %d";
            movieQueryFinal = String.format(movieQueryFinalTemplate, movieQueryBeforeTag, tagInClause, tagCount);
        }
        else
        {
            String movieQueryFinalTemplate = "SELECT DISTINCT mqat.movie_id " +
                    "FROM (%s) mqat JOIN movie_tags mtag ON mtag.movie_id = mqat.movie_id " +
                    "WHERE mtag.tag_id in %s " + tagWeightCondition;
            movieQueryFinal = String.format(movieQueryFinalTemplate, movieQueryBeforeTag, tagInClause, tagCount);
        }

        return movieQueryFinal;
    }

    class PopupHandler implements EventHandler<ActionEvent>
    {
        TextField textField;
        Supplier<List<String>> supplier;

        PopupHandler(TextField textField, Supplier<List<String>> supplier)
        {
            this.textField = textField;
            this.supplier = supplier;
        }
        @Override
        public void handle(ActionEvent event) {
            final Stage dialog = new Stage();
            dialog.initModality(Modality.NONE);
            Stage primaryStage = (Stage) genreListView.getScene().getWindow();
            dialog.initOwner(primaryStage);

            VBox dialogVbox = new VBox(20);
            ListView<String> listView = new ListView<>();


            listView.setOnMouseClicked(click -> {
                if(click.getClickCount() == 2)
                {
                    textField.setText(listView.getSelectionModel().getSelectedItem());
                    dialog.close();
                }
            });

            listView.setItems(FXCollections.observableArrayList(supplier.get()));

            dialogVbox.getChildren().add(listView);

            Scene dialogScene = new Scene(dialogVbox, 300, 200);
            dialog.setScene(dialogScene);
            dialog.show();
        }
    }

}




