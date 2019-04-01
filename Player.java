//Player.java
//By Sarah Clarke

/*
TODO
Is this interace sufficient?
*/

/**
 * This class defines a player that can interact with the game of Quarto!
 * @author Sarah Clarke - A00425098
 */
public interface Player {
    
    /**
     * Method for choosing the piece to give to the opponent
     * @return chosen piece
     */
    public Piece getPiece();
    
    /**
     * Method for placing the piece given to the Player on the board
     * @param piece piece to place on board
     * @return whether the move was successful
     */
    public boolean move(Piece piece);
    
}
