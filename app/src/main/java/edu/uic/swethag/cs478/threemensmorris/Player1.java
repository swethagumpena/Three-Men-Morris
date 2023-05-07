package edu.uic.swethag.cs478.threemensmorris;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.util.Random;

public class Player1 implements Runnable {
    private int numPieces = 0;
    private PositionData[] pieces = null;
    public static Handler player1Handler;

    // run method
    public void run() {
        // explicitly create looper for worker thread
        Looper.prepare(); // prepare loop
        createHandler(); // create handler
        Game.handlerInit++; // increment handler variable
        Looper.loop(); // start looping
    }

    private void createHandler() {
        player1Handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                int what = msg.what;
                switch (what) {
                    case Constants.MOVE_MADE:
                        makeMove();
                        break;
                }

            }
        };
    }

    // STRATEGY 1 - moving to a row major position. Starts from lookin at 00 followed by 01 then 02 the 10 and so on..
    private void makeMove() {
        // if all pieces of player 1 are not placed, place a new piece
        if (numPieces < 3) {
            if (pieces == null)
                pieces = new PositionData[3];
            placePiece();
            return;
        }
        // move a random piece to a row major position
        int pieceNumber = getRandomPiece();
        PositionData randomPoint = getRowMajorPosition();
        // move the piece
        Game.movePiece(pieces[pieceNumber], randomPoint);
    }

    private void placePiece() {
        pieces[numPieces] = getRowMajorPosition();
        Game.movePiece(new PositionData(-1, -1, Constants.PLAYER_1_ID), pieces[numPieces]);
        numPieces++;
    }

    private int getRandomPiece() {
        return new Random().nextInt(3);
    }

    // getting a row major non-occupied position
    private PositionData getRowMajorPosition() {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                if (isEmptySpace(row, col)) {
                    return new PositionData(row, col, Constants.PLAYER_1_ID);
                }
            }
        }
        // If no empty spot found, return an invalid move
        return new PositionData(-1, -1, Constants.PLAYER_1_ID);
    }

    private boolean isEmptySpace(int posX, int posY) {
        return Game.getBoard()[posX][posY] == Constants.EMPTY;
    }

    public static void quitLooper() {
        player1Handler.removeCallbacksAndMessages(null);
        player1Handler.getLooper().quit();
    }
}
