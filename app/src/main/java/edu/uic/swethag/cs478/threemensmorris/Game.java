package edu.uic.swethag.cs478.threemensmorris;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class Game {
    // instance variables
    private static int[][] board;
    public static int handlerInit;
    private static Handler mainHandler;

    public Game(Handler mainHandler) {
        // set handler variable to 0
        handlerInit = 0;
        initBoard();
        this.mainHandler = mainHandler;
    }

    // set all points on board to empty (0)
    private void initBoard() {
        board = new int[3][3];
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                board[i][j] = Constants.EMPTY;
    }

    public synchronized static void movePiece(PositionData prevPosition, PositionData newPosition) {
        // pause thread for 1 second, so user can view move
        pauseThread();

        // get previous position values
        int prevX = prevPosition.getPosX();
        int prevY = prevPosition.getPosY();

        // get new position values
        int newX = newPosition.getPosX();
        int newY = newPosition.getPosY();

        // check if it is a new placement
        if (prevX != -1)
            board[prevX][prevY] = Constants.EMPTY;

        // set playerId at new location
        board[newX][newY] = newPosition.getPlayerId();

        prevPosition.setPosX(newX);
        prevPosition.setPosY(newY);

        // post move by sending message
        Message msg = mainHandler.obtainMessage(Constants.MOVE_MADE, prevX, prevY, prevPosition);
        mainHandler.sendMessage(msg);
    }

    public synchronized static int[][] getBoard() {
        return board;
    }

    // pause thread for 1 second
    public static void pauseThread() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException i) {
        }
    }
}
