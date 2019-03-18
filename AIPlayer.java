//package quarto;
//AIPlayer.java
//By Sarah Clarke

/*
Most turns start with the player already having a piece to place. However,
there is a chace that the AI will be expected to start the game by choosing a
piece to give to the opponent. The AI must be able to handle executing this
"half turn" of sorts.
*/

/**
 * This class creates an AI player for Quarto that makes decisions about the
 * game using information that it can retrieve from the board
 * @author Sarah Clarke - A00425098
 */
public class AIPlayer implements Player {
    
    public Board game;
    
    /**
     * Constructor for an AI player
     * @param game 
     */
    public AIPlayer(Board game) {
        this.game = game;
    }

    /**
     * Method for choosing the piece to give to the opponent.
     * 
     * TODO: Fell free to add more commentary if you feel it necessary :)
     * 
     * @return chosen piece
     */
    @Override
    public Piece getPiece() {
        return null;
    }

    /**
     * Method for placing the piece given to the Player on the board
     * 
     * TODO: Fell free to add more commentary if you feel it necessary :)
     * 
     * @param piece piece to place on board
     * @return whether the move was successful
     */
    @Override
    public boolean move(Piece piece) {
        return false;
    }
    
}
