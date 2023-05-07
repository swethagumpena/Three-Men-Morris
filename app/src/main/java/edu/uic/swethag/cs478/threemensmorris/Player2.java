package edu.uic.swethag.cs478.threemensmorris;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.util.Random;

public class Player2 implements Runnable {
    private int numPieces = 0;
    private PositionData[] pieces = null;
    public static Handler player2Handler;

    // run method
    public void run() {
        // explicitly create looper for worker thread
        Looper.prepare(); // prepare loop
        createHandler(); // create handler
        Game.handlerInit++; // increment handler variable
        Looper.loop(); // start looping
    }

    private void createHandler() {
        player2Handler = new Handler() {
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

    // STRATEGY 2 - moving to a random location
    private void makeMove() {
        // if all pieces of player 2 are not placed, place a new piece
        if (numPieces < 3) {
            if (pieces == null)
                pieces = new PositionData[3];
            placePiece();
            return;
        }
        // move a random piece to a random location
        int pieceNumber = getRandomPiece();
        PositionData randomPoint = getRandomPosition();
        // move the piece
        Game.movePiece(pieces[pieceNumber], randomPoint);
    }

    private void placePiece() {
        pieces[numPieces] = getRandomPosition();
        Game.movePiece(new PositionData(-1, -1, Constants.PLAYER_2_ID), pieces[numPieces]);
        numPieces++;
    }

    private int getRandomPiece() {
        return new Random().nextInt(3);
    }

    // getting a random non-occupied position
    private PositionData getRandomPosition() {
        int posX = new Random().nextInt(3);
        int posY = new Random().nextInt(3);

        // make sure space is not occupied
        while (!isEmptySpace(posX, posY)) {
            posX = new Random().nextInt(3);
            posY = new Random().nextInt(3);
        }
        return new PositionData(posX, posY, Constants.PLAYER_2_ID);
    }

    private boolean isEmptySpace(int posX, int posY) {
        return Game.getBoard()[posX][posY] == Constants.EMPTY;
    }

    public static void quitLooper() {
        player2Handler.removeCallbacksAndMessages(null);
        player2Handler.getLooper().quit();
    }
}
