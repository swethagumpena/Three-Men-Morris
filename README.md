# Three Men Morris

(As part of CS 478 - Software Development for Mobile Platforms)

Android strategy game played on a 3x3 board. The app has two Java worker threads play against each other, while the UI thread displays the game and updates the board. The worker threads will wait for a short time, determine the next move, communicate with the UI thread, and wait for the opponent's move. The UI thread will show the initial board, update it after a move, display a button to start the game, check the game's status, show an appropriate message after the game, and signal the threads to stop after the game is over. Handlers are used and different strategies are implemented for winning the game.

<img width="324" alt="Screenshot 2023-05-07 at 6 22 25 PM" src="https://user-images.githubusercontent.com/41111884/236705960-1bc23311-9ffd-4226-aa90-92b1205b093f.png">
