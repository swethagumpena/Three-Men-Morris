package edu.uic.swethag.cs478.threemensmorris;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static ImageView[][] board;
    private static Thread player1Thread;
    private static Thread player2Thread;
    protected Button newGameBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initializing threads to null
        player1Thread = null;
        player2Thread = null;

        newGameBtn = findViewById(R.id.newGameBtn);

        newGameBtn.setOnClickListener(v -> {
            onNewGameClicked(v);
        });

        initBoard();
    }

    // handler for UI thread [main thread]
    private final Handler mainHandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            int what = msg.what;
            switch (what) {
                case Constants.MOVE_MADE:
                    updateUI((PositionData) msg.obj, msg.arg1, msg.arg2);
                    break;
            }
        }
    };

    public void onNewGameClicked(View view) {
        if (player1Thread != null)
            stopThreads();

        new Game(mainHandler);
        initThreads();
        clearBoard();
        newGame();
    }

    private void initThreads() {
        player1Thread = new Thread(new Player1());
        player2Thread = new Thread(new Player2());
    }

    private void clearBoard() {
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++) {
                board[i][j].setAlpha(0.0f);
            }
    }

    private void newGame() {
        // set looper init count to 0
        Game.handlerInit = 0;

        // start threads
        player1Thread.start();
        player2Thread.start();

        // get starting thread. Always starting with player 2
        int startingThread = 2;

        // wait for loopers to initialize
        while (Game.handlerInit < 2) ;

        // 1 for Player 1, 2 for player 2
        if (startingThread == 1)
            Player1.player1Handler.sendMessage(Player1.player1Handler.obtainMessage(Constants.MOVE_MADE));
        else if (startingThread == 2)
            Player2.player2Handler.sendMessage(Player2.player2Handler.obtainMessage(Constants.MOVE_MADE));
    }

    private void initBoard() {
        board = new ImageView[3][3];
        // set corresponding image views
        setImageViews();
    }

    private void setImageViews() {
        board[0][0] = findViewById(R.id.iv00);
        board[0][1] = findViewById(R.id.iv01);
        board[0][2] = findViewById(R.id.iv02);
        board[1][0] = findViewById(R.id.iv10);
        board[1][1] = findViewById(R.id.iv11);
        board[1][2] = findViewById(R.id.iv12);
        board[2][0] = findViewById(R.id.iv20);
        board[2][1] = findViewById(R.id.iv21);
        board[2][2] = findViewById(R.id.iv22);
    }

    private void updateUI(PositionData newPosition, int prevX, int prevY) {
        // if a new one is placed, reset old
        if (prevX != -1) {
            board[prevX][prevY].setAlpha(0.0f);
        }

        // get positions
        int newX = newPosition.getPosX();
        int newY = newPosition.getPosY();

        // place player piece
        if (newPosition.getPlayerId() == Constants.PLAYER_1_ID)
            board[newX][newY].setImageResource(R.drawable.orange_circle);
        else
            board[newX][newY].setImageResource(R.drawable.black_circle);

        // show the piece
        board[newX][newY].setAlpha(1.0f);

        // check for victory
        int hasWinner = checkHasWinner();

        // if winner is obtained, come out of function and call winner attained function
        if (hasWinner != -1) {
            winnerAttained(hasWinner, this);
            return;
        }

        // otherwise get next move
        nextMove(newPosition, prevX, prevY);
    }

    private int checkHasWinner() {
        int[][] gameState = Game.getBoard();

        //  Nested loop to iterate over each row and column of the game board
        //  For each row and column, the method keeps track of the ID of the first cell in the row or column, and counts how many cells in that row or column have the same ID
        for (int i = 0; i < 3; i++) {
            int xID = gameState[i][0];
            int yID = gameState[0][i];
            int horizontalCount = 0;
            int verticalCount = 0;

            for (int j = 0; j < 3; j++) {
                if (gameState[i][j] == xID)
                    horizontalCount++;
                if (gameState[j][i] == yID)
                    verticalCount++;
            }

            //  if the count for any row or column is 3 (all three cells have the same ID), and the ID is not 0 (row or column is not empty), then the method returns the ID of the player who won the game
            //  if no player has won the game, the method returns -1.
            if (xID != Constants.EMPTY && horizontalCount == 3) {
                return xID;
            }
            if (yID != Constants.EMPTY && verticalCount == 3) {
                return yID;
            }
        }
        // indicates that the game is still in progress
        return -1;
    }

    private void winnerAttained(int winnerId, Context activity) {
        stopThreads();
        if (winnerId == Constants.PLAYER_1_ID)
            Toast.makeText(activity, "Player 1 is the Winner", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(activity, "Player 2 is the Winner!", Toast.LENGTH_LONG).show();
    }

    private void stopThreads() {
        // post runnables to exit threads
        Player1.player1Handler.post(new Runnable() {
            @Override
            public void run() {
                Player1.quitLooper();
            }
        });
        Player2.player2Handler.post(new Runnable() {
            @Override
            public void run() {
                Player2.quitLooper();
            }
        });

        // making sure threads are dead
        while (player1Thread.isAlive()) ;
        while (player2Thread.isAlive()) ;

        // setting threads to null
        player1Thread = null;
        player2Thread = null;

        // clearing UI queue
        mainHandler.removeCallbacksAndMessages(null);
    }

    private static void nextMove(PositionData newPosition, int prevX, int prevY) {
        //  send a message to the handler of the opposing player with information about a move that has been made
        if (newPosition.getPlayerId() == Constants.PLAYER_2_ID)
            Player1.player1Handler.sendMessage(Player1.player1Handler.obtainMessage(Constants.MOVE_MADE, prevX, prevY, newPosition));
        else
            Player2.player2Handler.sendMessage(Player2.player2Handler.obtainMessage(Constants.MOVE_MADE, prevX, prevY, newPosition));
    }
}