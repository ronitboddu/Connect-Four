package connectfour.gui;

import connectfour.model.ConnectFourBoard;
import connectfour.model.Observer;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static connectfour.model.ConnectFourBoard.COLS;
import static connectfour.model.ConnectFourBoard.ROWS;
import static connectfour.model.ConnectFourBoard.Player;

/**
 * Implements GUI for connect four game
 * @author RIT CS
 * @author Shivani Singh, ss5243@rit.edu
 * @author Ronit Boddu, rb1209@rit.edu
 */

public class ConnectFourGUI extends Application implements Observer<ConnectFourBoard> {
    private final ConnectFourBoard board;
    private Label moves;
    private Label currentPlayerText;
    private Label status;
    private final Image empty;
    private final List<Button> buttonList;
    private final Map<ConnectFourBoard.Player,Image> playerImage;

    /**
     * Constructor to initialize connect four board and its parameters.
     */
    public ConnectFourGUI() {
        this.board = new ConnectFourBoard();
        initializeView();
        playerImage = new HashMap<>();
        empty = new Image(getClass().getResourceAsStream("empty.png"));
        Image p1Black = new Image(getClass().getResourceAsStream("p1black.png"));
        Image p2Red = new Image(getClass().getResourceAsStream("p2red.png"));
        playerImage.put(ConnectFourBoard.Player.P1, p1Black);
        playerImage.put(ConnectFourBoard.Player.P2, p2Red);
        buttonList = new ArrayList<>();

    }

    /*
     ******************* THE VIEW SECTION ***************************************
     */

    /**
     * The View initialization adds board as an observer to the Model.
     */
    private void initializeView() {
        this.board.addObserver(this);
    }

    /**
     * The update for the GUI prints the board and some state.
     * @param board
     */
    @Override
    public void update(ConnectFourBoard board) {
        this.moves.setText(String.valueOf(this.board.getMovesMade()) + " moves made");
        this.currentPlayerText.setText("Current Player: "+this.board.getCurrentPlayer());
        this.status.setText("Status: "+this.board.getGameStatus());
    }

    /**
     * DiscButton class to represent clickable image.
     */
    private static class DiscButton extends Button{
        private Player player;
        public DiscButton(Image image, Player player){
            this.setGraphic(new ImageView(image));
            this.player = player;
        }

    }

    /**
     * start function to initialize the GUI Components
     * @param stage
     * @throws Exception
     */
    @Override
    public void start(Stage stage) throws Exception {
        BorderPane borderPane = new BorderPane();
        BorderPane innerBorderPane = new BorderPane();
        FlowPane flowPane = new FlowPane();

        moves = new Label(this.board.getMovesMade()+" moves made");
        innerBorderPane.setLeft(moves);
        BorderPane.setAlignment(moves, Pos.BOTTOM_CENTER);

        currentPlayerText = new Label("Current player: "+this.board.getCurrentPlayer());
        innerBorderPane.setCenter(currentPlayerText);
        BorderPane.setAlignment(currentPlayerText, Pos.BOTTOM_CENTER);

        status = new Label("Status: "+this.board.getGameStatus());
        innerBorderPane.setRight(status);
        BorderPane.setAlignment(status, Pos.BOTTOM_CENTER);

        GridPane gridPane = makeGridPane();
        borderPane.setCenter(gridPane);
        borderPane.setBottom(innerBorderPane);
        innerBorderPane.setPrefHeight(20);

        Scene scene = new Scene(borderPane);
        stage.setTitle("ConnectFourGUI");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    /*
     ******************* THE CONTROLLER SECTION *********************************
     */

    /**
     * The helper function to create gridPane
     * @return GridPane
     */
    private GridPane makeGridPane(){
        GridPane gridPane = new GridPane();
        for(int row=0;row<ROWS;++row){
            for(int col=0;col<COLS;++col){
                DiscButton button = new DiscButton(empty, ConnectFourBoard.Player.NONE);
                gridPane.add(button,col,row);
                buttonList.add(button);
                ActionOnClick(button,gridPane,col);
            }
        }
        return gridPane;
    }

    /**
     * The function to implement action on clicking on any button.
     * @param button - user clicked button.
     * @param gridPane - the gridPane
     * @param col - the column of the clicked button.
     */
    public void ActionOnClick(DiscButton button, GridPane gridPane,int col){
        button.setOnAction(event-> {
            update(this.board);
            if(getRow(col,button)>=0) {
                DiscButton new_button = new DiscButton(playerImage.get(board.getCurrentPlayer()),
                        board.getCurrentPlayer());
                gridPane.add(new_button,col,getRow(col,button));
                buttonList.add(new_button);
                ActionOnClick(new_button,gridPane,col);
                this.board.makeMove(col);
            }
            if(this.board.getGameStatus() != ConnectFourBoard.Status.NOT_OVER){
                for(Button b:buttonList){
                    b.setDisable(true);
                }
            }
        });
    }

    /**
     * The function to get the bottom most available row in the column of the clicked button.
     * @param col - the column of the clicked button.
     * @param button - user clicked button.
     * @return the bottom most row of the column at which there is no user disc
     */
    public int getRow(int col,DiscButton button){
        int getRowGrid =-1;
        for(int row=ROWS-1; row >= 0; --row){
            if (this.board.getContents(row,col) == ConnectFourBoard.Player.NONE) {
                button.player = board.getCurrentPlayer();
                getRowGrid = row;
                break;
            }
        }
        return getRowGrid;
    }
}
