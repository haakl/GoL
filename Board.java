package gol;

import javafx.scene.control.Alert;

import java.io.*;
import java.net.URL;
import java.util.Random;
import java.io.FileReader;

public class Board {

    /**
     * Creates an int array to store the cells value (alive or dead)
     */
    private int[][] board;

    public Board(int size) {
        board = new int[size][size];
    }

    public void setField(int x, int y, int value) {
        board[x][y] = value;
    }

    public int getField(int x, int y) {
        return board[x][y];
    }

    public int getSize() {
        return board.length;
    }

    /**
     *
     * @param density is used to define the density of cells drawn on the board (recommended is 0.5)
     */
    public void randomBoard(double density) {
        Random random = new Random();
        for (int x = 0; x < board.length; x++) {
            for (int y = 0; y < board.length; y++) {
                if (random.nextDouble() > density) {
                    board[x][y] = 1;
                }
            }
        }
    }

    public void firstShape(double firstShape) {
        //stable x=1,1,2,2 y=1,2,1,2
        board[1][1] = 1;
        board[1][2] = 1;
        board[2][1] = 1;
        board[2][2] = 1;
        }
    public void secondShape(double secondShape) {
        //stable x=1,2,3,1 y= 1,1,1,2
        board[1][1] = 1;
        board[2][1] = 1;
        board[3][1] = 1;
        board[1][2] = 1;
    }
    public void thirdShape(double thirdShape) {
        //stable x=1,1,1,1 y=1,2,3,4
        board[1][1] = 1;
        board[1][2] = 1;
        board[1][3] = 1;
        board[1][4] = 1;
    }
    public void fourthShape(double fourthShape) {
        //dead x=1,2,3,4 y=1,2,2,2
        board[1][1] = 1;
        board[2][2] = 1;
        board[3][2] = 1;
        board[4][2] = 1;
    }
    public void fifthShape(double fifthShape) {
        //repeat x=6,5,6,7 y=5,6,6,6
        board[6][5] = 1;
        board[5][6] = 1;
        board[6][6] = 1;
        board[7][6] = 1;
    }
    public void sixthShape(double sixthShape) {
        //stable x=5,3,4,4 y=5,6,6,7
        board[5][5] = 1;
        board[3][6] = 1;
        board[4][6] = 1;
        board[4][7] = 1;
    }

    public void RunLengthEncodingFromFile(String fileName) {
        String[] value = new String[2];
        String src = "";
        try {
            FileReader fr = new FileReader(fileName);
            BufferedReader br = new BufferedReader(fr);
            String line = br.readLine();
            String lineOne = "";
            boolean firstLine = true;
            while (line != null) {
                if (line.charAt(0) != '#') {
                    if (firstLine) {
                        lineOne += line;
                        firstLine = false;
                        value = lineOne.split(",");
                        value[1] = value[1].replaceAll("\\D+", "");
                        value[0] = value[0].replaceAll("\\D+", "");
                    } else {
                        src += line;
                    }
                }
                line = br.readLine();
            }
        } catch (IOException e) {
            Alert alert2 = new Alert(Alert.AlertType.INFORMATION);
            alert2.setTitle("Whoops!");
            alert2.setHeaderText("");
            alert2.setContentText("Something went wrong when reading from file provided.");
            alert2.showAndWait();
        }

        String RLEString = decode(src); //returns decoded version of run-length encoded string.
        RLEString = RLEString.replace("b", "0"); //replace b with 0 to represent dead cell
        RLEString = RLEString.replace("o", "1"); //replace o with 1 to represent life
        int m = Integer.parseInt(value[0]); //width
        int n = Integer.parseInt(value[1]); //height

        if (m > GameOfLife.CELLCOUNT || n > GameOfLife.CELLCOUNT) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("");
            alert.setTitle("Error");
            alert.setContentText("The pattern you tried to load is too large for this board.");
            alert.showAndWait();
        }

        int startX = 10; //starting point on board
        int startY = 10; //starting point on board
        int charID = 0; //charID to keep track of what character we are currently reading on the decoded string.
        int stringInt; //Integer to parseInt the current character from RLEString
        int y = 0;

        System.out.println(RLEString);


        char character;
        for (int x = 0; y <= n; x++) {
            character = RLEString.charAt(charID);
            if (character == '$') {
                x = -1; //if $, x-value (x-axis counter) back to zero. -1 because loop adds 1.
                y++; //y-value (y-axis counter) one step down (+1).
                charID++; //next character
            } else if (character == '!') {
                break; //End of RLEString
            } else {
                stringInt = Integer.parseInt(character + ""); //parseInt to make character an int
                board[y+startY][x+startX] = stringInt; //define dead or alive cell at target cell.
                charID++; //next character
            }
        }
    }

    public void RunLengthEncoding(String url) {
        String[] value = new String[2];  //x and y-values, [0] and [1] respectively
        String src = "";      //Value from every following line, in other words the RLE-code itself.

        try {
            //Creates URL from the supplied url-String
            URL link = new URL(url);
            //BufferedReader enables readLine
            BufferedReader br = new BufferedReader(new InputStreamReader(link.openStream()));
            String line = br.readLine();   //Reads from line one at URL-page
            String lineOne = "";     //Set as value from first line not starting with #
            boolean firstLine = true;
            while (line != null) {
                if (line.charAt(0) != '#') {     //If first character is #, no action, read next line
                    if (firstLine) {
                        firstLine = false;  //First line is written, putting firstLine to false.
                        lineOne += line;  //First line not starting with # gets written to lineOne
                        value = lineOne.split(","); //Splits at commas to get x = ? and y = ? in separate arrays
                        value[0] = value[0].replaceAll("\\D+", ""); //Replace all non-numbers and we are left with x-value
                        value[1] = value[1].replaceAll("\\D+", ""); //--"-- y-value
                        //Value[0] contains x-value, value[1] contains y-value for length and width of pattern.
                    } else {
                        src += line;    //lines after x- and y-value are added to src.
                    }
                }
                line = br.readLine();   //reads next line
            }

            System.out.println(lineOne);
            System.out.println(src);
        } catch (IOException e) {  //If any errors occur, this message is displayed
            Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
            alert1.setTitle("Whoops!");
            alert1.setHeaderText("");
            alert1.setContentText("Something went wrong when reading from the URL provided.");
            alert1.showAndWait();
        }

        String RLEString = decode(src); //returns decoded version of run-length encoded string.
        RLEString = RLEString.replace("b", "0"); //replace b with 0 to represent dead cell
        RLEString = RLEString.replace("o", "1"); //replace o with 1 to represent life
        int m = Integer.parseInt(value[0]); //width
        int n = Integer.parseInt(value[1]); //height

        if (m > GameOfLife.CELLCOUNT || n > GameOfLife.CELLCOUNT) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setContentText("The pattern you tried to load is too large for this board.");
            alert.setHeaderText("");
            alert.showAndWait();
        }

        int startX = 10; //starting point on board
        int startY = 10; //starting point on board
        int charID = 0; //charID to keep track of what character we are currently reading on the decoded string.
        int stringInt; //Integer to parseInt the current character from RLEString
        int y = 0;

        System.out.println(RLEString);


        char character;
        for (int x = 0; y <= n; x++) {
            character = RLEString.charAt(charID);
            if (character == '$') {
                charID++; //next character
                x = -1; //if $, x-value (x-axis counter) back to zero. -1 because loop adds 1.
                y++; //y-value (y-axis counter) one step down (+1).
            } else if (character == '!') {
                break; //End of RLEString
            } else {
                charID++; //next character
                stringInt = Integer.parseInt(character + ""); //parseInt to make character an int
                board[y+startY][x+startX] = stringInt; //define dead or alive cell at target cell.
            }
        }
    }

    public static String decode(String source) {
        String res = "";
        String numberBufferString;
        int numberBuffer = 0;
        //runs the for-loop until all characters of source has been "checked".
        for (int i = 0; i < source.length(); i++) {
            if (source.charAt(i) == '!') {    //if we reach the '!', we know to stop the loop.
                res += source.charAt(i);
                break;
            }
            String dual = "" + source.charAt(i + 1);  //this was only required to parseInt a character and will only
            //be used if two numbers in a row.
            String dual2 = "" + source.charAt(i + 2); //Same as above but with three numbers in a row
            //String dual3 = "" + source.charAt(i + 3); //four numbers in a row

            if (source.charAt(i) != 'o' && source.charAt(i) != 'b' && source.charAt(i) != '$' && source.charAt(i) != '!') {
                numberBufferString = "" + source.charAt(i);
                numberBuffer = Integer.parseInt(numberBufferString);
            } //^^if character is a number, we want to put that number into our numberBuffer.
            if (source.charAt(i) == 'o' || source.charAt(i) == 'b' || source.charAt(i) == '$') {
                res += source.charAt(i);  //non-numbers are added to result
            } else {

                if (source.charAt(i + 1) == 'b') {
                    for (int z = 0; z < numberBuffer; z++) { //if next value is not a number, we loop while adding to result
                        res += source.charAt(i + 1);         //the number of times given in numberBuffer
                    }
                    i++;
                } else if (source.charAt(i + 1) == 'o') {
                    for (int z = 0; z < numberBuffer; z++) {
                        res += source.charAt(i + 1);
                    }
                    i++; //add to i because we used one character ahead.
                } else if (source.charAt(i + 1) == '$') {
                    for (int z = 0; z < numberBuffer; z++) {
                        res += source.charAt(i + 1);
                    }
                    i++;
                } else if (Integer.parseInt(dual) <= 9) { //in case of two numbers in a row
                    if (source.charAt(i + 2) == 'b') {
                        numberBufferString = "" + source.charAt(i) + source.charAt(i + 1);
                        numberBuffer = Integer.parseInt(numberBufferString); //update numberBuffer and loop that amount of times
                        for (int y = 0; y < numberBuffer; y++) {
                            res += source.charAt(i + 2);
                        }
                        i += 2; //add 2 to i because we used two characters ahead.
                    } else if (source.charAt(i + 2) == 'o') {
                        numberBufferString = "" + source.charAt(i) + source.charAt(i + 1);
                        numberBuffer = Integer.parseInt(numberBufferString);
                        for (int y = 0; y < numberBuffer; y++) {
                            res += source.charAt(i + 2);
                        }
                        i += 2;
                    } else if (source.charAt(i + 2) == '$') {
                        numberBufferString = "" + source.charAt(i) + source.charAt(i + 1);
                        numberBuffer = Integer.parseInt(numberBufferString);
                        for (int y = 0; y < numberBuffer; y++) {
                            res += source.charAt(i + 2);
                        }
                        i += 2;
                    } else if (Integer.parseInt(dual2) <= 9) {
                        if (source.charAt(i + 3) == 'b') {
                            numberBufferString = "" + source.charAt(i) + source.charAt(i + 1) + source.charAt(i + 2);
                            numberBuffer = Integer.parseInt(numberBufferString);
                            for (int y = 0; y < numberBuffer; y++) {
                                res += source.charAt(i + 3);
                            }
                            i += 3;
                        } else if (source.charAt(i + 3) == 'o') {
                            numberBufferString = "" + source.charAt(i) + source.charAt(i + 1) + source.charAt(i + 2);
                            numberBuffer = Integer.parseInt(numberBufferString);
                            for (int y = 0; y < numberBuffer; y++) {
                                res += source.charAt(i + 3);
                            }
                            i += 3;
                        } else if (source.charAt(i + 3) == '$') {
                            numberBufferString = "" + source.charAt(i) + source.charAt(i + 1) + source.charAt(i + 2);
                            numberBuffer = Integer.parseInt(numberBufferString);
                            for (int y = 0; y < numberBuffer; y++) {
                                res += source.charAt(i + 3);
                            }
                            i += 3;
                        }/* else if (Integer.parseInt(dual3) <= 9) {
                            if (source.charAt(i + 4) == 'b') {
                                numberBufferString = "" + source.charAt(i) + source.charAt(i + 1) + source.charAt(i + 2) + source.charAt(i + 3);
                                numberBuffer = Integer.parseInt(numberBufferString);
                                for(int y = 0; y < numberBuffer; y++) {
                                    res += source.charAt(i + 4);
                                }
                                i += 4;
                            } else if (source.charAt(i + 4) == 'o') {
                                numberBufferString = "" + source.charAt(i) + source.charAt(i + 1) + source.charAt(i + 2) + source.charAt(i + 3);
                                numberBuffer = Integer.parseInt(numberBufferString);
                                for(int y = 0; y < numberBuffer; y++) {
                                    res += source.charAt(i + 4);
                                }
                                i += 4;
                            } else if (source.charAt(i + 4) == '$') {
                                numberBufferString = "" + source.charAt(i) + source.charAt(i + 1) + source.charAt(i + 2) + source.charAt(i + 3);
                                numberBuffer = Integer.parseInt(numberBufferString);
                                for(int y = 0; y < numberBuffer; y++) {
                                    res += source.charAt(i + 4);
                                }
                                i += 4;
                            }
                        }*/
                    }
                }
            }
        }
        return res;
    }


    /**
     * nextGeneration defines the next iteration of the game board. The length of the int array stays the same, but the
     * function calls for the checkboard method to see if the rules comply with the current state. Once determined
     * it will adjust the board for the next iteration and repeat itself.
     */
    public void nextGeneration() {
        int[][] newBoard = new int[board.length][board.length];
        for(int x=0; x<board.length;x++) {
            for(int y=0; y<board.length;y++) {
                newBoard[x][y] = getField(x, y); //Copy the value into a new board
                checkBoard(x, y, newBoard);
            }
        }
        board = newBoard;
    }

    /**
     *
     * @param x is the first parameter to the checkBoard method.
     * @param y is the second parameter to the checkBoard method.
     * @param newBoard is the third parameter to the checkBoard method.
     */
    public void checkBoard(int x, int y, int[][] newBoard) {
        int[] indexX = {-1,0 ,1 ,-1,1 ,-1,0 ,1 };
        int[] indexY = {1 ,1 ,1 ,0 ,0 ,-1,-1,-1};

        int aliveOrDead = board[x][y];

        int neighbours = 0;
        for(int i=0;i<8;i++) {
            if(x+indexX[i]>=0 && y+indexY[i]>=0 && x+indexX[i]<board.length && y+indexY[i]<board.length) {
                neighbours = neighbours + getField(x+ indexX[i], y+indexY[i]);
            }
        }

        if(aliveOrDead==0 && neighbours==3) {
            //setField(x, y, 1); //If the cell is dead and has 3 neighbours it gets reborn
            newBoard[x][y] = 1;
            return;
        }

        if(aliveOrDead==1 && neighbours<2) {
            //setField(x, y, 0); //If the cell is alive and has less than two neigbours it dies
            newBoard[x][y] = 0;
            return;
        }


        if(aliveOrDead==1 && (neighbours==2 || neighbours==3)) {
            //Two or three alive neighbors and a live cell makes no changes
            return;
        }

        if(aliveOrDead==1 && neighbours>3) {
            //setField(x, y, 0); //If there is more than three alive neihgbors around a living cell it dies
            newBoard[x][y] = 0;
        }

    }
}