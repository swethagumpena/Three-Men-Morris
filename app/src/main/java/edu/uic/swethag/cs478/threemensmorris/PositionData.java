package edu.uic.swethag.cs478.threemensmorris;

public class PositionData {
    private int posX;
    private int posY;
    private int playerId;

    public PositionData(int x, int y, int playerId) {
        posX = x;
        posY = y;
        this.playerId = playerId;
    }

    // get x position
    public int getPosX() {
        return posX;
    }

    // set x position
    public void setPosX(int posX) {
        this.posX = posX;
    }

    // get y position
    public int getPosY() {
        return posY;
    }

    // set the y position
    public void setPosY(int posY) {
        this.posY = posY;
    }

    // get the player id
    public int getPlayerId() {
        return playerId;
    }

    // set the player id
    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }
}
