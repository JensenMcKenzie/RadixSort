package com.example.radixsort;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.util.Duration;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;
import java.util.ArrayList;

public class RadixSortVisualizer extends Application {
    //Javafx items
    public Group rectangles;
    public TextField userInput;
    public Label sortInfo;
    public Label currentNumber;

    //Animation variables
    Timeline every2Seconds = new Timeline();

    //List of all integers
    private static ArrayList<Integer> l;
    //RadixSortVisualizer object
    private static RadixSort r;
    //Current rectangle index
    private int current;

    //RadixSortVisualizer class
    public class RadixSort{
        //List of integers
        ArrayList<Integer> list;
        //Greatest number of digits present in a single number in the list
        int greatestDigits;

        //Constructor
        RadixSort(ArrayList<Integer> list){
            //Set the greatest digits to 0
            greatestDigits = 0;
            //Initialize the list of integers
            this.list = new ArrayList<>();
            for (int i : list){
                //Update the greatestDigits variable if the current number has more digits than the current greatest amount
                if (getLength(i) > greatestDigits){
                    greatestDigits = getLength(i);
                }
                //Copy each item from the passed in list to the new list
                this.list.add(i);
            }
        }

        //Sorts the list by the given digit place (0 based, 0 is ones, 1 is tens, ect.)
        public boolean sortListByDigit(int place){
            //Set the count to 0, this is used to track the completion of the sort
            int count = 0;
            //Loop through the entire array
            for(int a = 0; a < list.size()-1; a++) {
                //Check to see if the current number is greater than the next number
                if (getDigit(list.get(a),place) > getDigit(list.get(a+1),place)){
                    //If so, swap places, and update the current rectangle
                    current = a;
                    int temp = list.get(a);
                    list.set(a,list.get(a+1));
                    list.set(a+1, temp);
                }else{
                    //If the current number is less than the next number, increment the count
                    count++;
                }
            }
            //Return a true or false value, have we completed the sort based on the digit place?
            return count == list.size()-1;
        }

        //Returns the digit at a given place
        public int getDigit(int number, int place){
            //Convert the number to a string
            String num = Integer.toString(number);
            //Ensure the given place is valid
            if (place > num.length()-1 || place < 0){
                return 0;
            }
            //Return the integer at the requested digit place
            return Character.getNumericValue(num.charAt(num.length()-place-1));
        }

        //Returns the length (in digits) of the given number
        public int getLength(int number){
            //Convert the number to a string
            String num = Integer.toString(number);
            //Return the length of the string
            return num.length();
        }

        //Returns the current list of integers
        public ArrayList<Integer> getList(){
            return list;
        }

        //Returns the current greatest digit
        public int getGreatestDigit(){
            return greatestDigits;
        }
    }

    //Called when the pause button is pressed
    @FXML
    protected void pauseClick(){
        //Check to see if we are already running
        if (every2Seconds.getStatus() == Animation.Status.RUNNING){
            //Pause the animation
            every2Seconds.pause();
        }
        //Check to see if we are already paused
        else if (every2Seconds.getStatus() == Animation.Status.PAUSED){
            //Play the animation
            every2Seconds.play();
        }
    }

    //Called when the start button is pressed
    @FXML
    protected void startClick(){
        //Check to see if an animation is not already running
        if (every2Seconds.getStatus() == Animation.Status.STOPPED){
            //Create a new arraylist of integers
            l = new ArrayList<>();
            //Set up random number generation
            Random rand = new Random();
            //Add a user specified amount of random numbers to the list
            for (int i = 0; i < Integer.parseInt(userInput.getText()); i++){
                //Generate a new random integer from 1-200
                int x = rand.nextInt(200)+1;
                //Add the random integer to the list
                l.add(x);
            }
            //Create a new radix sorting object
            r = new RadixSort(l);
            //Initialize the value of the sortInfo text
            sortInfo.setText("Now sorting the 1 digit");
            //Set up the sorting animation, it will run ever .02 seconds
            every2Seconds = new Timeline(new KeyFrame(Duration.seconds(.02), new EventHandler<>() {
                //Count keeps track of the current digit place being sorted
                int count = 0;
                //Repeats keeps track of how many iterations we use while sorting, used to calculate time spent and comparisons
                int repeats = 0;
                //Called every time the animation is run
                @Override
                public void handle(ActionEvent actionEvent){
                    //Store the semi sorted list in the l variable
                    l = r.getList();
                    //Clear all previous rectangles
                    rectangles.getChildren().clear();
                    //For each item in the list, create a new scaled rectangle, proportional to the size of the window, and its integer value
                    for (int i = 0; i < l.size(); i++){
                        //Create a new rectangle
                        Rectangle r = new Rectangle(((rectangles.getScene().getWidth()*.9/l.size()))*i,0,((rectangles.getScene().getWidth()*.9/l.size()))*.5,l.get(i));
                        //Check to see if we are on the current index
                        if (i == current){
                            //Set the current rectangle's color to red
                            r.setFill(Color.RED);
                        }
                        //Add the rectangle to the group
                        rectangles.getChildren().add(r);
                    }
                    //Rotate the group 180 degrees, so the rectangles scale from the bottom of the screen, rather than the top
                    rectangles.setRotate(180);
                    //Increament the iteration counter
                    repeats++;
                    //Update the current number text to display the current number being pushed forward
                    currentNumber.setText("Pushing the number " + l.get(current));
                    //Check to see if we are done sorting the current digit place
                    boolean done = r.sortListByDigit(count);
                    //Check to see if the list is completely sorted, either by the current digit place, or overall
                    if (done){
                        //Create a new file path, for the output of semi sorted lists
                        Path file = Path.of("src/main/java/output.txt");
                        //See if this is a new group of integers
                        if (count == 0){
                            //Clear the output file to make way for new data
                            try{
                                Files.writeString(file, "");
                            }catch (IOException i){
                                //Print file not found error message
                                System.out.println("File not found at " + file);
                            }
                        }
                        try{
                            //Set the output variable to the contents of the file, then add the new sorted list to the output. Then write the output to the file
                            StringBuilder output = new StringBuilder();
                            output.append(Files.readString(file));
                            output.append("Sorted by ").append(count).append(" digit:\n");
                            for (int i : l){
                                output.append(i).append("\n");
                            }
                            output.append("\n\n");
                            Files.writeString(file, output.toString());
                        }catch (IOException i){
                            //Print file not found error message
                            System.out.println("File not found at " + file);
                        }
                        //Increment the digit place counter
                        count++;
                        //Check to see if we are not completely done sorting
                        if (count < r.getGreatestDigit()) {
                            //Update the information text to reflect the new digit place
                            sortInfo.setText("Now sorting the " + (count + 1) + " digit");
                        }
                        else
                            //Print the finished sorting text, as well as the time taken and the total comparisons expended
                            sortInfo.setText("Done sorting in " + (double)Math.round(every2Seconds.getCycleDuration().toSeconds() * repeats * 100)/100 + " seconds.\nUsed " + repeats*(l.size()-1) + " comparisons");
                    }
                    //Check to see if we are completely done sorting
                    if (count == r.getGreatestDigit()){
                        //End the animation
                        every2Seconds.stop();
                        current = 0;
                    }
                }
            }));
            //Set the cycles of the animation to infinite, as it will stop automatically when done sorting
            every2Seconds.setCycleCount(Timeline.INDEFINITE);
            //Play the animation
            every2Seconds.play();
        }else{
            //If we are already running an animation, stop it
            every2Seconds.stop();
            current = 0;
        }
    }
    @Override
    public void start(Stage stage) throws IOException {
        //Load fxml files
        FXMLLoader fxmlLoader = new FXMLLoader(RadixSortVisualizer.class.getResource("RaxidSortVisualizer.fxml"));
        //Load a new scene, and set the title
        Scene scene = new Scene(fxmlLoader.load(), 500, 500);
        //Display the scene to the stage
        stage.setTitle("Radix Sort");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}