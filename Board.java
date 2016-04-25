package gol;

import java.util.Random;

public class Board {

    /**
     * Creates a int array to store the cells value (alive or dead)
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
            return;
        }

    }
}
