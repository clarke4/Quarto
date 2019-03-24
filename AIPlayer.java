//package quarto;
//AIPlayer.java
//By Sarah Clarke

import java.util.ArrayList;
import java.util.Arrays;

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
    
    /**
     * Returns an ArrayList of indices of pieces remaining.
     * 
     * @return ArrayList of indices of pieces remaining
     */
    public ArrayList<Integer> validPieces() {
        Piece[] remainingPieces = game.getRemainingPieces();
        ArrayList<Integer> validPieces = new ArrayList<>();
        for (int i = 0; i < remainingPieces.length; i++) {
            if (remainingPieces[i] != null) {
                int index = remainingPieces[i].ID;
                validPieces.add(index);
            }
        }
        return validPieces;
    }
    
    
    /**
     * Returns a random piece from the remaining pieces.
     * 
     * @return random piece from remaining pieces
     */
    public Piece getRandomPiece() {
        Piece[] remainingPieces = game.getRemainingPieces();
        ArrayList<Integer> validPieces = validPieces();
        int randIndex = validPieces.get((int) (Math.random() * validPieces.size()));
        return remainingPieces[randIndex];
    }
    
    
    /**
     * Returns the coordinates of the move, given piece p, that minimizes the 
     * chances of winning for the other player. If such a move cannot be 
     * found, returns the coordinates of a valid move that does not lead to a
     * win for the other player. If all moves lead to a win by other player, 
     * returns the last valid move instead.
     * 
     * @param p piece to place
     * @return coordinates of the min move given piece p
     */
    public int[] minMoveCoord (Piece p) {
        ArrayList<String> almostQuartos = game.getAlmostQuartos();
        int minAlmostQuartos = almostQuartos.size();
        int currAlmostQuartos;
        int[] minCoord = {-1, -1};
        int[] betterValidCoord = {-1, -1};
        int[] lastValidCoord = new int[2];
        // for each position
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                // if piece can be moved and is moved to that position
                if (game.movePiece(p.ID, row, col)) {
                    // set last valid coordinates to that position
                    lastValidCoord[0] = row;
                    lastValidCoord[1] = col;
                    game.checkForQuarto();
                    almostQuartos = game.getAlmostQuartos();
                    currAlmostQuartos = almostQuartos.size();
                    // if we can choose a piece from the remaining pieces
                    // that does not lead to a quarto for the opponent
                    if (noQuartoPiece()) {
                        // set better valid coordinates to that position
                        betterValidCoord[0] = row;
                        betterValidCoord[1] = col;
                        // if current almostQuartos is less than the minimum almostQuartos
                       if (currAlmostQuartos <= minAlmostQuartos) {
                           // set minimum almostQuartos to current almostQuartos
                            minAlmostQuartos = currAlmostQuartos;
                            // set coordinates to current position
                            minCoord[0] = row;
                            minCoord[1] = col;
                        } 
                    }
                    // reset board to previous state
                    game.removePiece(row, col);
                }
            }
        }
        // if no minimizing coordinates are found
        if (minCoord[0] == -1) {
            // if no better valid coordinates are found
            if (betterValidCoord[0] == -1) {
                // return last valid coordinates
                return lastValidCoord;
            }
            // otherwise, return better valid coordinates
            return betterValidCoord;
        }
        // otherwise, return coordinates
        return minCoord;
    }
    
    /**
     * Checks if there is a piece among the remaining ones that does not lead 
     * to a Quarto and returns true if that is the case.
     * 
     * @return whether at least one of the pieces available to give to the other 
     * player leads to a no quarto state
     */
    public boolean noQuartoPiece () {
        Piece[] remainingPieces = game.getRemainingPieces();
        ArrayList<Integer> validPieces = validPieces();
        ArrayList<String> almostQuartos = game.getAlmostQuartos();
        // for each piece
        for (int i = 0; i < validPieces.size(); i++) {
            boolean noWin = true;
            String piece = remainingPieces[validPieces.get(i)].toString();
            // compare with attributes of each almostQuarto
            for (int j = 0; j < almostQuartos.size(); j++) {
                for (int a = 0; a < 4; a++) {
                    // if any attribute is the same, the piece will lead to a win
                    // for the opponent
                    if (almostQuartos.get(j).charAt(a) == piece.charAt(a)) noWin = false;
                }
            }
            // if piece will not lead to a win for opponent, return true
            if (noWin) return true;    
        }
        // if all pieces lead to a win for opponent, return false
        return false;
    }
    
    /**
     * Returns a piece that will not result in a Quarto for the other player,
     * if possible. Otherwise, returns a random piece.
     * 
     * @return a piece to give to the other player
     */
    public Piece noQuartoGetPiece () {
        Piece[] remainingPieces = game.getRemainingPieces();
        ArrayList<Integer> validPieces = validPieces();
        ArrayList<String> almostQuartos = game.getAlmostQuartos();
        // for each piece
        for (int i = 0; i < validPieces.size(); i++) {
            boolean noWin = true;
            String piece = remainingPieces[validPieces.get(i)].toString();
            // compare with attributes of each almostQuarto
            for (int j = 0; j < almostQuartos.size(); j++) {
                for (int a = 0; a < 4; a++) {
                    // if any attribute is the same, the piece will lead to a win
                    // for the opponent
                    if (almostQuartos.get(j).charAt(a) == piece.charAt(a)) noWin = false;
                }
            }
            // if piece will not lead to a win for opponent, return it
            if (noWin) return remainingPieces[i];    
        }
        // if all pieces lead to a win for opponent, return random piece
        return getRandomPiece();       
    }
    
    
    /**
     * Moves the piece given to win, if possible. Otherwise, moves the piece 
     * to a position that minimizes the other player's chances of winning.
     * 
     * @param p piece to place
     */
    public void almostQuartoMove(Piece p) {
        String piece = p.toString();
        String winLine;
        boolean won = false;
        ArrayList<String> almostQuartos = game.getAlmostQuartos();
        
        int i = 0;
        while (i < almostQuartos.size() && !won) {
            for (int j = 0; j < 4; j++) {
                if (almostQuartos.get(i).charAt(j) == piece.charAt(j)) {
                    winLine = almostQuartos.get(i).substring(4);
                    // if almost quarto in row
                    if (winLine.charAt(0) == '0') {
                        //try to move to each col position in that row
                        for (int col = 0; col < 4; col++) {
                            if (game.movePiece(p.ID, Character.getNumericValue(winLine.charAt(1)), col)) won = true;
                        }
                    }                   
                    // if almost quarto in col
                    if (winLine.charAt(0) == '1') { 
                        //try to move to each row position in that col
                        for (int row = 0; row < 4; row++) {
                            if (game.movePiece(p.ID, row, Character.getNumericValue(winLine.charAt(1)))) won = true;
                        }
                    }                       
                    // if almost quarto in top left to bottom right diagonal
                    if (winLine.equals("20")) {
                        //try to move to each position in that diagonal
                        for (int x = 0; x < 4; x++) {
                            if (game.movePiece(p.ID, x, x)) won = true;
                        }
                    }
                    // if almost quarto in top right to bottom left diagonal
                    if (winLine.equals("21")) {
                        //try to move to each position in that diagonal
                        for (int x = 0; x < 4; x++) {
                            if (game.movePiece(p.ID, x, (3-x))) won = true;
                        }
                    }                    
                }
            }
            i++;
        }
        // if not able to win, moves the piece to a position that minimizes
        // opponent's chances of winning
        if (!won) {
            int[] coord = minMoveCoord(p);
            game.movePiece(p.ID, coord[0], coord[1]);
        }
    } 
    
}
