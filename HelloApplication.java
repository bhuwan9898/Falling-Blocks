package com.example.demo4;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Random;

public class HelloApplication extends Application {
    // Text displayed at the end of the game
    Text gameEndMessage = new Text("You Lost!\nTry Again!");

    // Timeline animation interval (milliseconds)
    int difficultyTimeSpeed = 500;

    // Radio buttons for game speed control
    RadioButton radioEasy;
    RadioButton radioDifficult;
    ToggleGroup toggleGroupLevels;

    // Button labels for states of the game
    // Start - Beginning of the game
    // Reset - After game is lost
    // Continue - If game is paused
    // Pause - If game is currently running
    final static String BUTTON_LABEL_GAMESTATE_START = "Start";
    final static String BUTTON_LABEL_GAMESTATE_RESET = "Reset";
    final static String BUTTON_LABEL_GAMESTATE_CONTINUE = "Continue";
    final static String BUTTON_LABEL_GAMESTATE_PAUSE = "Pause";

    // Game control button
    Button gameButton;

    // Timeline animation instance for keyframe control
    // Time duration is the difficultTimeSpeed
    // Interval event handler is GamePlayTimelineHandler
    Timeline gamePlayTimeline = new Timeline(
            new KeyFrame(Duration.millis(difficultyTimeSpeed),
                    new GamePlayTimelineHandler()));

    // Array of Circles for "cannons"
    Circle[] cannons = new Circle[5];

    // Array of Rectangles for "falling blocks"
    Rectangle[] blocks = new Rectangle[5];

    // Count of cannons left at game run-time
    int canonsLeft;

    // Minimum number of cannons required to continue game
    // End of game if cannons left is less than min cannons required.
    int minCannonsReq = 3;

    // Pane for the game activity area
    GamePane gamePane;

    // Pane for button and speed controls
    ControlPane controlPane = new ControlPane();

    // Main pane for the "Falling Bricks" window
    BorderPane borderPaneGame = new BorderPane();

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Initialize game state parameters and initial falling brick alignment
        initiateGame();

        // Set game animation timeline to run indefinitely
        gamePlayTimeline.setCycleCount(Timeline.INDEFINITE);

        // Set the game control panel on the left of the border pane
        borderPaneGame.setLeft(controlPane);

        // Set the main pain for the game window for the scene
        Scene scene = new Scene(borderPaneGame);
        // Set the title for the primary stage as the name of the game
        primaryStage.setTitle("Game: Falling Blocks");
        // Set the scene on the stage
        primaryStage.setScene(scene);
        // Display the stage
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    // Method Purpose: Initialize game state parameters
    void initiateGame() {
        // Reset available active cannons
        canonsLeft = 5;
        // Instantiate game activity area pane
        gamePane = new GamePane();
        // Set game activity area pane at the center of the main border pane
        borderPaneGame.setCenter(gamePane);
    }


    // Inner Class inside Falling Blocks
    // Purpose: Design and features for game activity area.
    // Design is based on 5cx6r GridPane. Top row for 5 initial
    // state of bricks. Bottom row for 5 available cannons.
    class GamePane extends GridPane {
        // Constructor: Create and initialize all objects within the game activity area
        public GamePane() {
            // Set game activity area width=250 and height=300
            setWidth(250);
            setHeight(300);

            // Set game activity area background color Black
            setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));

            // Manually add 6 rows for the gridpane layout for game activity area
            for (int rowIndex = 0; rowIndex < 6; rowIndex++) {
                // create row constraint with height of each row as 50
                RowConstraints row = new RowConstraints(50);
                // add row constraint for a newly created row
                getRowConstraints().add(row);
            }

            // Generate items for 5 columns in the game activity area
            for (int colIndex = 0; colIndex < 5; colIndex++) {
                // obtain block and save it in blocks array index=i
                // TO DO
                Rectangle rectangle = getBlock();
                blocks[colIndex] = rectangle;
                // add block to first row for the present column
                // TO DO
                this.add(rectangle,colIndex,0);

                // obtain cannon and save it in cannons
                // TO DO
                Circle circle = getCannon();
                cannons[colIndex] = circle;

                // add cannon to last row (#5) for the present column
                // TO DO
                this.add(circle,colIndex,5);

                // set horizontal alignment for cannon's cell to center
                setHalignment(cannons[colIndex], HPos.CENTER);

                // Lamdba expressions to be used for cannon in the current column
                // to handle events triggered using the mouse
                // a. when mouse pressed on cannon, invoke cannon fire trigger method
                circle.setOnMousePressed(event -> {cannonFireTrigger(event);});
                // b. when mouse released on cannon, invoke cannon fire trigger release method
                circle.setOnMouseReleased(event ->{cannonFireTriggerRelease(event);});
                // c. when mouse entered over cannon, invoke cannon activate effect method
                circle.setOnMouseEntered(event ->{cannonActivateEffect(event);});
                // d. when mouse exited from cannon, invoke cannon deactivate effect method
                circle.setOnMouseExited(event ->{cannonDeactivateEffect(event);});
                // TO DO
            }
        }

        // Method Purpose: Design default inactive cannon
        // Cannon (circle) design:
        // radius = 20, stroke = black, fill = red, opacity = 0.5
        Circle getCannon() {
            Circle cannon = new Circle(20);
            cannon.setFill(Color.RED);
            cannon.setStroke(Color.BLACK);
            cannon.setOpacity(0.5);
            return cannon;
            // TO DO
        }

        // Method Purpose: Design default block
        // Block (rectangle) design:
        // height/width = 50, stroke = black, fill = green
        Rectangle getBlock() {
            Rectangle block = new Rectangle(0,0,50,50);
            block.setFill(Color.GREEN);
            block.setStroke(Color.BLACK);
            return block;
            // TO DO
        }

        // Method Purpose: Design default cross for "destroyed" cannon places
        // Cross (line composition) design using StackPane:
        // cross is placed in 50x50 cell in the game activity panel.
        // line1 (top-L to bottom-R) and line2 (top-R to bottom-L) of a 50x50 stackpane.
        // color = red for both line1 and line2
        StackPane getCross() {
            StackPane cross = new StackPane();
            Line line1 = new Line(0,0,50,50);
            Line line2 = new Line(50,0,0,50);
            line1.setStroke(Color.RED);
            line2.setStroke(Color.RED);
            cross.getChildren().addAll(line1,line2);
            return cross;
            // TO DO
        }
        Line getLine(){
            Line line1 = new Line(25,0,25,50);
            line1.setStroke(Color.RED);
            line1.setFill(Color.RED);
            return line1;

        }

        // Method Purpose: Invoked by cannon's lamdba expression event handler
        // to process cannon firing game activity
        void cannonFireTrigger(Event e) {
            // Extract source for event "e" and cast to "Circle"
            Circle eventSource = (Circle) e.getSource();
            // Change cannon radius = 25 (visualize fire-trigger effect)
            // TO DO
            eventSource.setRadius(25);

            // Obtain column index for the game activity area gridpane
            // TO DO
            int cIndex = getColumnIndex(eventSource);
            //int rowIndex = getRowIndex(eventSource);
            Line line = getLine();
            this.getChildren().add(line);
            setHalignment(line, HPos.CENTER);
            //getLine(cIndex,rowIndex);
            // Remove the block from the game activity area gridpane
            // using the column index and the blocks array
            // TO DO
            this.getChildren().remove(blocks[cIndex]);
            this.getChildren().remove(line);
            // add the same block from the blocks array again
            // in the same column index within the gridpane in top row
            // TO DO
            this.add(blocks[cIndex],cIndex,0);
        }

        // Method Purpose: Invoked by cannon's lambda expression event handler
        // to process cannon end of triggered fire game activity
        void cannonFireTriggerRelease(MouseEvent e) {
            // Extract source for event "e" and cast to "Circle"
            Circle eventSource = (Circle) e.getSource();
            // Change cannon radius = 20 (visualize fire-trigger release effect)
            eventSource.setRadius(20);
            // TO DO
        }

        // Method Purpose: Invoked by cannon's lambda expression event handler
        // to process cannon activation game activity when mouse comes on top of cannon
        void cannonActivateEffect(MouseEvent e) {
            // Extract source for event "e" and cast to "Circle"
            Circle eventSource = (Circle) e.getSource();
            // Change cannon opacity = 1.0 (visualize activated cannon effect)
            eventSource.setOpacity(1.0);
            // TO DO
        }

        // Method Purpose: Invoked by cannon's lambda expression event handler
        // to process cannon de-activation game activity when mouse move away from cannon
        void cannonDeactivateEffect(MouseEvent e) {
            // Extract source for event "e" and cast to "Circle"
            Circle eventSource = (Circle) e.getSource();
            // Change cannon opacity = 0.5 (visualize de-activated cannon effect)
            eventSource.setOpacity(0.5);
            // TO DO
        }

        // Method Purpose: Return randomly selected column index.
        // Random column index must be where a cannon is still
        // available, and number of cannons left is higher than
        // the minimum number of required cannons for game.
        // May return -1 if no such index available
        int getRandomColumnIndex() {
            // TO DO
            Random random = new Random();
            ArrayList<Integer> cannonsStill = new ArrayList<>();
            if (canonsLeft>=minCannonsReq){
                for (int i = 0;i<5;i++){
                    if (cannons[i] != null){
                        cannonsStill.add(i);
                    }}
                int randomIndex = random.nextInt(cannonsStill.size());
                return cannonsStill.get(randomIndex);
            }
            else return -1;
        }

        // Method Purpose: Game play progress for falling block in each interval
        boolean dropRandomBlock() {
            // If game should proceed (condition: available cannons still not below min required)
            if (canonsLeft>=minCannonsReq) {
                // Obtain randomly selected block from array of blocks using random column index
                // TO DO
                Rectangle randomBlock = blocks[getRandomColumnIndex()];
                // Get gridpane's (game activity area) current row index
                // and current column index for the randomly selected block
                // TO DO
                int colIndex = this.getColumnIndex(randomBlock);
                int rowIndex = this.getRowIndex(randomBlock);
                // Remove the randomly selected block from the gridpane (game activity area)
                // TO DO
                this.getChildren().remove(randomBlock);
                // If-else processing for dropping block:
                if (rowIndex ==4) {
                    // if current row index was 4, the block's next
                    // cell with the last row, which will destroy the cannon
                    // in that same column index. Available cannons will
                    // decrease and the gridpane cell for the cannon will
                    // be replaced by a "cross"
                    // TO DO
                    this.getChildren().remove(cannons[colIndex]);
                    this.add(getCross(),colIndex,5);
                    cannons[colIndex] = null;
                    canonsLeft--;}
                else {
                    // else add the same block in the same column index
                    // but in the next row
                    // TO DO
                    this.add(randomBlock,colIndex,rowIndex+1);
                }
                // Return true to imply successful game play progress for this interval
                return true;
            }
            else {
                // Game should not proceed any more and must return false
                return false;
            }
        }
    }


    // Inner Class inside Falling Blocks
    // Purpose: Design and features for button and speed controls
    // Design is based on VBox pane.
    class ControlPane extends VBox {
        // Constructor: Create and initialize objects within the control area
        public ControlPane() {
            // Set t/r/b/l padding for all items added in the vbox
            setPadding(new Insets(50, 20, 50, 20));
            // Set spacing between items within vbox
            setSpacing(20);

            // Set height=300 and width=150 for the control panel vbox
            setWidth(150);
            setHeight(300);

            // Instantiate radio buttons (global variables) for
            // easy and difficult levels and set text accordingly
            // TO DO
            radioEasy = new RadioButton("Level: Easy");
            radioDifficult = new RadioButton("Level: Difficult");


            // Instantiate toggle group (global variable) for difficulty
            // level radio buttons, and assign radio buttons to the toggle group.
            // Set default selection for easy-level radio button.
            // TO DO
            toggleGroupLevels = new ToggleGroup();
            radioEasy.setToggleGroup(toggleGroupLevels);
            radioDifficult.setToggleGroup(toggleGroupLevels);
            toggleGroupLevels.selectToggle(radioEasy);
            // Lamdba expressions to be used to handle events triggered
            // from the mouse by clicking on both the radio buttons
            // Invoke update difficulty level method for both selections
            // TO DO
            radioEasy.setOnAction(event ->{
                updateDifficultyLevel();

            });
            radioDifficult.setOnAction(event ->{
                updateDifficultyLevel();

            });
            // Instantiate game button (global variable) with "start" button label
            // Use predefined STRING at the top for label string
            // Include lambda expression to call game button control method
            // TO DO
            gameButton = new Button(BUTTON_LABEL_GAMESTATE_START);
            gameButton.setOnAction(e->{
                gameButtonControl();
            });
            // Add both radio buttons and game button to the control panel vbox
            // TO DO
            this.getChildren().addAll(radioEasy,radioDifficult,gameButton);
            // Set the alignment for the vbox to center-left
            setAlignment(Pos.CENTER_LEFT);

        }

        // Method Purpose: Invoked by difficulty level selector radio-buttons lambda expressions
        void updateDifficultyLevel() {
            // If easy button is selected
            // --- set the game timeline animation rate-property to 1.0
            if (radioEasy.isSelected()){
                gamePlayTimeline.setRate(1.0);

            }
            // If difficult button is selected
            // --- set the game timeline animation rate-property to 2.0 (twice the speed)
            if (radioDifficult.isSelected()){
                gamePlayTimeline.setRate(2.0);
            }
            // TO DO
        }

        // Method Purpose: Invoked by game play button lamba expression
        void gameButtonControl() {
            // Use FINAL STATIC STRING global variables defined at the top
            // If button is showing "start" or "continue"
            // --- play game timeline, and change button text to "pause"
            if(gameButton.getText().equals(BUTTON_LABEL_GAMESTATE_START)|| gameButton.getText().equals(BUTTON_LABEL_GAMESTATE_CONTINUE)){
                gameButton.setText(BUTTON_LABEL_GAMESTATE_PAUSE);
                gamePlayTimeline.play();
            }
            // If button is showing "pause"
            // --- pause game timeline, and change button text to "continue"
            else if(gameButton.getText().equals(BUTTON_LABEL_GAMESTATE_PAUSE)){
                gamePlayTimeline.pause();
                gameButton.setText(BUTTON_LABEL_GAMESTATE_CONTINUE);
            }
            // If button is showing "reset"
            // --- call initiate game method from GamePane
            // --- remove end game message text, and change button text to "start"
            // TO DO
            else if (gameButton.getText().equals(BUTTON_LABEL_GAMESTATE_RESET)){
                initiateGame();
                this.getChildren().remove(gameEndMessage);
                gameButton.setText(BUTTON_LABEL_GAMESTATE_START);
            }
        }
    }

    // Inner Class inside Falling Blocks
    // Purpose: Event handler for timeline animation for game play
    class GamePlayTimelineHandler implements EventHandler<ActionEvent> {
        // Mandatory overridden method for event handler
        @Override
        public void handle(ActionEvent actionEvent) {
            // Call drop random block method from game activity panel
            boolean activeStatus = gamePane.dropRandomBlock();
            // If drop random block method returns "false", it means
            // that the game is over.
            // --- stop the timeline animation.
            // --- add the end of game message to the control panel vbox.
            // --- change game button text in the control panel
            //     to "reset" (use pre-defined STRINGS at the top)
            if (!activeStatus){
                gamePlayTimeline.stop();
                controlPane.getChildren().add(gameEndMessage);
                gameButton.setText(BUTTON_LABEL_GAMESTATE_RESET);
            }
            // TO DO
        }
    }

}