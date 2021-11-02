package javafx;

import javafx.display.Display;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.*;


public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    private final String colorOfButton = "-fx-background-color: #76DDFF";

    private final String colorOfOtherButton = "-fx-background-color: #FFDA60";

    private final int timeOut = 100;

    private final Display display = new Display();//this class is needed to dynamically adjust to the user's screen

    private Scene scene;

    private Stage stage;

    private Stage stageError;

    private Boolean sort = true;

    private volatile Boolean doSort = false;

    private volatile GridPane matrix;

    private volatile List< Integer > buttons;

    private int elementOfRow = 10;

    private int sizeButton = 60;




    @Override
    public void start(Stage primaryStage) throws Exception{
        stage = primaryStage;
        goToIntro();
    }

    private void goToIntro(){
        stage.close();
        stage = new Stage();
        stage.setTitle("Intro");
        scene = new Scene(getIntro());
        stage.setScene(scene);
        stage.show();
    }

    private void goToSort(int data){
        stage.close();
        stage = new Stage();
        stage.setTitle("sort");
        scene = new Scene(getSort(data));
        stage.setScene(scene);
        stage.show();
    }

    private AnchorPane getIntro(){
        AnchorPane intro = new AnchorPane();
        intro.setPrefWidth(display.getWidth());
        intro.setPrefHeight(display.getHeight());

        int widthSizeElements = 180;//size element
        int heightSizeElements = 10;

        Label label = new Label("How many numbers to display?");
        label.setLayoutX(display.getWidth()/2-widthSizeElements/2);
        label.setLayoutY(display.getHeight()/2-100);
        intro.getChildren().add(label);

        TextField textField = new TextField();
        textField.setPromptText("Enter number");
        textField.setPrefSize(widthSizeElements,heightSizeElements);
        textField.setLayoutX(display.getWidth()/2-widthSizeElements/2);
        textField.setLayoutY(display.getHeight()/2-50);
        intro.getChildren().add(textField);

        Button button = new Button("Enter");
        button.setStyle(colorOfButton);
        button.setPrefWidth(widthSizeElements);
        button.setPrefHeight(heightSizeElements);
        button.setLayoutX(display.getWidth()/2-widthSizeElements/2);
        button.setLayoutY(display.getHeight()/2);
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    int quantityElements = validation(textField.getText());
                    goToSort(quantityElements);
                }catch (NumberFormatException n){
                    error("You cannot enter text, enter a number between 1 and 1000 inclusive!");
                    //text
                }catch (RuntimeException r){
                    //num
                    error("Invalid number, enter a number in the range from 1 to 1000 inclusive!");
                }
            }
        });
        intro.getChildren().add(button);



        return intro;
    }

    private void error(String text){
        AnchorPane error = new AnchorPane();
        error.setPrefWidth(200);
        error.setPrefHeight(150);

        Label errorLabel = new Label(text);
        errorLabel.setWrapText(true);
        errorLabel.setLayoutY(10);
        errorLabel.setLayoutX(15);
        errorLabel.setMaxHeight(110);
        errorLabel.setMaxWidth(170);
        error.getChildren().add(errorLabel);

        Button buttonError = new Button("OK");
        buttonError.setPrefWidth(80);
        buttonError.setLayoutY(110);
        buttonError.setLayoutX(60);
        buttonError.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                stageError.close();
            }
        });
        error.getChildren().add(buttonError);
        if(stageError != null) stageError.close();
        stageError = new Stage();
        stageError.setTitle("error");
        stageError.setScene(new Scene(error));
        stageError.show();
    }

    private int validation(String quantityElements){
        int num = Integer.parseInt(quantityElements);
        if(num > 0 && num < 1001)
        return num;
        else throw new RuntimeException();
    }

    private AnchorPane getSort(int quantityElements){
        AnchorPane sort = new AnchorPane();
        sort.setPrefWidth(display.getWidth());
        sort.setPrefHeight(display.getHeight());

        ScrollPane scrollPane = new ScrollPane(getMatrix(quantityElements));
        scrollPane.setPrefWidth( display.getWidth() / 2 );
        scrollPane.setPrefHeight( display.getHeight() / 2 );
        scrollPane.setLayoutX( display.getWidth() / 4 );
        scrollPane.setLayoutY( display.getHeight() /4 );
        scrollPane.setStyle("-fx-background-color:transparent;");

        Button sortButton = new Button("Sort" );
        sortButton.setStyle(colorOfOtherButton);
        sortButton.setPrefWidth( sizeButton );
        sortButton.setLayoutX( scrollPane.getLayoutX() + scrollPane.getPrefWidth() + 20 );
        sortButton.setLayoutY( display.getHeight() / 4 + 1 );
        sortButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(!doSort) {
                    sortElement();
                }
            }
        });
        sort.getChildren().add( sortButton );

        Button resetButton = new Button("Reset" );
        resetButton.setStyle(colorOfOtherButton);
        resetButton.setPrefWidth( sizeButton );
        resetButton.setLayoutX( sortButton.getLayoutX() );
        resetButton.setLayoutY( sortButton.getLayoutY() + 45 );
        resetButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                goToIntro();
                matrix = null;
                buttons = null;
            }
        });
        sort.getChildren().add( resetButton );
        sort.getChildren().add(scrollPane);

        return sort;
    }

    private GridPane getMatrix(int quantityElements){
        if( matrix == null ) {
            matrix = new GridPane();
        }
        resizing(quantityElements);
        if( buttons == null ){
            randomNumber(quantityElements);
        }
        new Thread(new Runnable() {
            @Override public void run() {
                generateContent();
                }
            }).start();
        return matrix;
    }

    private void resizing(int quantityElements) {
        int startXPosition = (int) ( display.getWidth() / 1.5 ) - ( sizeButton + 20 ) * ( quantityElements < 10? 1 : ( quantityElements / 10 ) + ( quantityElements % 10 != 0? 1 : 0) );
        int startYPosition = display.getHeight() / 2 - sizeButton * 4;

        matrix.setLayoutX( startXPosition );
        matrix.setLayoutY( startYPosition );
        matrix.setHgap(20);
        matrix.setVgap(20);
    }

    volatile int iteration;

    private void generateContent(){
        doSort = true;
        if( matrix.getChildren().size() > 0 ){
//            for( Object elementInGP : matrix.getChildren().toArray()){
//                Thread thread = new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        removeObject( elementInGP );
//                    }
//                });
//                thread.start();
//                try {
//                    Thread.sleep( timeOut );
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
        }
        sort = true;
        for( iteration = 0; iteration < buttons.size(); iteration++ ) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        addObject( createButton( buttons.get( iteration ) ), iteration / elementOfRow, iteration % elementOfRow );
                    }
                }).start();
                try {
                    Thread.sleep( timeOut );
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        doSort = false;
    }

    private synchronized void removeObject( Object elementInGP ){
        Platform.runLater(new Runnable() {
            @Override public void run() {
                matrix.getChildren().remove( elementInGP );
            }
        });
    }

    private synchronized void addObject( Button button, int coordinateX, int coordinateY ){
        Platform.runLater(new Runnable() {
            @Override public void run() {
                matrix.add( button, coordinateX, coordinateY );
            }
        });
    }

    private int[] getMass(){
        int[] mas = new int[buttons.size()];
        int i = 0;
        for(int number : buttons){
            mas[i] = number;
            i++;
        }
        return mas;
    }

    private void sortElement(){
        if( !doSort ){
            doSort = true;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    int[] mas = getMass();
                    quickSort( mas, 0, mas.length - 1, sort);
                    if(sort)sort = false;
                    else sort = true;
                    buttons = new ArrayList<>();
                    for(int number : mas ){
                        buttons.add(number);
                    }
                    doSort = false;
                }
            }).start();
        }
    }


    public void quickSort(int[] array, int low, int high, Boolean way) {
        if (array.length == 0)
            return;

        if (low >= high)
            return;

        int middle = low + (high - low) / 2;
        int opora = array[middle];
        int i = low, j = high;
        while (i <= j) {
            if(way){
                while (array[i] > opora) {
                    i++;
                }

                while (array[j] < opora) {
                    j--;
                }
            }else {
                while (array[i] < opora) {
                    i++;
                }

                while (array[j] > opora) {
                    j--;
                }
            }

            if (i <= j) {
                int temp = array[i];
                array[i] = array[j];
                array[j] = temp;
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                buttonRacking( array );
                i++;
                j--;
            }
        }
        if (low < j)
            quickSort(array, low, j, way);

        if (high > i)
            quickSort(array, i, high, way);
    }

    private synchronized void buttonRacking(int[] array){
        
        for(int i = 0; i < array.length; i++ ){
            Object elementInGP = matrix.getChildren().toArray()[i];
            if( Integer.parseInt( ( (Button) elementInGP).getText() ) != array[i] ){
                int finalI = i;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Platform.runLater(new Runnable() {
                            @Override public void run() {
                                removeObject( elementInGP );
                                addObject( createButton( array[finalI] ), finalI / 10, finalI % 10 );
                            }
                        });
                    }
                }).start();

            }
        }
//        Platform.runLater(new Runnable() {
//            @Override public void run() {
//                removeObject( matrix.getChildren().toArray()[finalI] );
//                removeObject( matrix.getChildren().toArray()[finalJ] );
//
//            }
//        });
//        try {
//            Thread.sleep(100);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        Platform.runLater(new Runnable() {
//            @Override public void run() {
//                addObject( createButton( array[finalJ] ), finalJ / 10, finalJ % 10 );
//                addObject( createButton( array[finalI] ), finalI / 10, finalI % 10 );
//
//            }
//        });
    }

    private void randomNumber(int quantityElements){
        buttons = new ArrayList<>();
        if( quantityElements == 1000){
            for( int iteration = 1; iteration <= quantityElements; iteration++){
                buttons.add(iteration);
            }
            Collections.shuffle(buttons);
        }else {
            Set<Integer> ints = new HashSet<>();
            Boolean ifNumber = false;
            int lastNumber = 0;
            for (int iteration = 1; iteration <= quantityElements; iteration++) {
                while (true) {
                    if (ints.size() == iteration) break;
                    lastNumber = (int) (1 + Math.random() * 999);
                    if (lastNumber <= 30) ifNumber = true;
                    ints.add(lastNumber);
                }
            }
            if (!ifNumber) {
                ints.remove(lastNumber);
                ints.add((int) (1 + Math.random() * 29));
            }
            ints.stream().forEach( i -> buttons.add(i));
        }
    }

    private Button createButton( int nameOfButton){
        Button button = new Button("" + nameOfButton );
        button.setStyle( colorOfButton );
        button.setPrefWidth( sizeButton );
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(!doSort) {
                    int number = Integer.parseInt(button.getText());
                    if (number <= 30) {
                        buttons = null;
                        getMatrix(number);
                    } else {
                        error("Invalid number, enter a number in the range from 1 to 30 inclusive!");
                    }
                }

            }
        });
        return button;
    }


}
