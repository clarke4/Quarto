//package quarto;
//Board.java
//By Sarah Clarke

import java.util.ArrayList;
import java.util.Arrays;


/**
 * This class defines a Board to be used to contain Pieces in a
 * game of Quarto.
 * @author Sarah Clarke - A00425098
 */
public class Board 
{
    
    // ---- CLASS VARIABLES --------------------------------------------- //
    public static final String NO_QUARTO = "no quarto";
    // ------------------------------------------------------------------ //
    
    
    // ---- INSTANCE VARIABLES ------------------------------------------ //
    private Piece[][] board = new Piece[4][4];
    private Piece[] piecesLeft = new Piece[] {
        new Piece(0,  true,  true,  true,  true),
        new Piece(1,  true,  true,  true,  false),
        new Piece(2,  true,  true,  false, true),
        new Piece(3,  true,  true,  false, false),
        new Piece(4,  true,  false, true,  true),
        new Piece(5,  true,  false, true,  false),
        new Piece(6,  true,  false, false, true),
        new Piece(7,  true,  false, false, false),
        new Piece(8,  false, true,  true,  true),
        new Piece(9,  false, true,  true,  false),
        new Piece(10, false, true,  false, true),
        new Piece(11, false, true,  false, false),
        new Piece(12, false, false, true,  true),
        new Piece(13, false, false, true,  false),
        new Piece(14, false, false, false, true),
        new Piece(15, false, false, false, false)
    };
    private ArrayList<String> halfQuartos = new ArrayList<>(); //stores 2/4 Quartos
    private ArrayList<String> almostQuartos = new ArrayList<>(); //stores 3/4 Quartos
    // ------------------------------------------------------------------ //
    
    
    // ------------------------------------------------------------------ //
    // ---- INSTANCE METHODS -------------------------------------------- //
    // ------------------------------------------------------------------ //
    
    /**
     * Attempts to place a Piece on the square at the given coordinates and 
     * returns whether the placement was successful or not.
     * 
     * @param pieceID id of the piece being moved
     * @param row row of the space the piece is being moved to
     * @param col column of the space the piece is being moved to
     * @return whether the move was successful or not
     */
    public boolean movePiece(int pieceID, int row, int col) 
    {
        if (!isSpaceValid(row, col) || !canMovePiece(pieceID)) //if space isn't empty
        {
            return false;
        }
        board[row][col] = getPiece(pieceID); //assign piece to board space
        return true;
    }
    
    /**
     * AI-centred method that removes a piece from the board
     * (to be used in AIPlayer only)
     * 
     * @param row row of the space being wiped
     * @param col column of the space being wiped
     * @return whether the wipe was successful or not
     */
    public boolean removePiece(int row, int col) 
    {
        if (!isSpaceValid(row, col)) //if space isn't valid
        {
            return false;
        }
        board[row][col] = null; //make space blank
        return true;
    }
    
    /**
     * Returns a copy of the board's current state to be printed. The copy 
     * cannot be used to manipulate the board outside of the manipulation
     * methods given.
     * @return copy of the board's current state
     */
    public Piece[][] getBoard() {
        Piece[][] copy = new Piece[4][4];
        for (int i = 0; i < 4; i++) {
            copy[i] = Arrays.copyOf(board[i], 4);
        }
        return copy;
    }
    
    /**
     * Returns an ArrayList of Strings that represent an almost complete Quarto,
     * which is a line that contains only three pieces, and those pieces share 
     * at least one trait. Each String contains four characters that represent 
     * information about the half Quarto.
     * 
     * (If need be, this method can be altered to give more info, such as
     * the row/column/diagonal line in which the Quarto appears)
     * 
     * First character: 1 if all pieces are tall, 2 if they're short
     * Second character: 1 if all pieces are light, 2 if they're dark
     * Third character: 1 if all pieces are circular, 2 if they're square
     * Fourth character: 1 if all pieces are solid, 2 if they're hollow
     * A 0 in any of these positions means that the pieces do not share this
     * trait.
     * 
     * @return an ArrayList of Strings that each describe details about a 
     * almost completed Quarto
     */
    public ArrayList<String> getAlmostQuartos() {
        return (ArrayList)almostQuartos.clone();
    }
    
    /**
     * Returns an ArrayList of Strings that represent a half Quarto, which is
     * a line that contains only two pieces, and those pieces share at least 
     * one trait. Each String contains four characters that represent 
     * information about the half Quarto.
     * 
     * (If need be, this method can be altered to give more info, such as
     * the row/column/diagonal line in which the Quarto appears)
     * 
     * First character: 1 if all pieces are tall, 2 if they're short
     * Second character: 1 if all pieces are light, 2 if they're dark
     * Third character: 1 if all pieces are circular, 2 if they're square
     * Fourth character: 1 if all pieces are solid, 2 if they're hollow
     * A 0 in any of these positions means that the pieces do not share this
     * trait.
     * 
     * @return an ArrayList of Strings that each describe details about a 
     * half Quarto
     */
    public ArrayList<String> getHalfQuartos() {
        return (ArrayList)halfQuartos.clone();
    }
    
    /**
     * Returns an ArrayList of Strings that each describe a partial Quarto that
     * exists in the board given as input. This method can be used to return
     * an ArrayList of half Quartos or almost Quartos depending on whether you
     * give '2' or '3' as the second parameter.
     * 
     * First four characters of the Strings identify the matching traits:
     * Char 1: Height   (1 for tall,    2 for short,   0 for no match)
     * Char 2: Colour   (1 for light,    2 for dark,   0 for no match)
     * Char 3: Shape    (1 for circular, 2 for square, 0 for no match)
     * Char 4: Material (1 for solid,    2 for hollow, 0 for no match)
     * 
     * Characters 5 and 6 describe position on the board of the partial Quarto.
     * Here is what the two digits mean:
     * '0#' - row number # (0 <= # <= 3)
     * '1#' - column number # (0 <= # <= 3)
     * '20' - diagonal from top left to bottom right
     * '21' - diagonal from bottom left to top right
     * 
     * 
     * @param board board being checked for partial Quartos
     * @param type either 2 (for half Quartos) or 2 (for almost Quartos)
     * @return ArrayList of Strings describing each half Quarto on the board
     */
    public ArrayList<String> getPartialQuartos(Piece[][] board, char type) {
        if (type != '2' && type != '3') {
            System.out.println("Incorrect useage of "
                    + "Board.getPartialQuartos(Piece[][] board, char type) - "
                    + "char type must either be '2' or '3'");
        }
        
        //initialize vars
        Piece[] check;
        String result;
        ArrayList<String> list = new ArrayList();
        
        //check rows
        for (int row = 0; row < 4; row++) {
            check = new Piece[]{board[row][0], board[row][1], board[row][2], board[row][3]};
            result = Piece.comparePieces(check);
            if ((result.substring(1).contains("1") 
                    || result.substring(1).contains("2"))
                    && result.charAt(0) == type)
                    list.add(result.substring(1) + "0" + row);
        }
        
        //check cols
        for (int col = 0; col < 4; col++) {
            check = new Piece[]{board[0][col], board[1][col], board[2][col], board[3][col]};
            result = Piece.comparePieces(check);
            if ((result.substring(1).contains("1") 
                    || result.substring(1).contains("2"))
                    && result.charAt(0) == type)
                    list.add(result.substring(1) + "1" + col);
        }
        
        //check diagonal
        //top left to bottom right
        check = new Piece[]{board[0][0], board[1][1], board[2][2], board[3][3]};
        result = Piece.comparePieces(check);
        if ((result.substring(1).contains("1") 
                    || result.substring(1).contains("2"))
                    && result.charAt(0) == type)
                    list.add(result.substring(1) + "20");
        
        //bbottom left to top right
        check = new Piece[]{board[0][3], board[1][2], board[2][1], board[3][0]};
        result = Piece.comparePieces(check);
        if ((result.substring(1).contains("1") 
                    || result.substring(1).contains("2"))
                    && result.charAt(0) == type)
                    list.add(result.substring(1) + "21");
        
        return list;
    }
    
    /**
     * Returns a copy of the array of pieces that have not yet been placed
     * on the board. 
     * @return remaining pieces not yet on the board
     */
    public Piece[] getRemainingPieces() {
        return Arrays.copyOf(piecesLeft, piecesLeft.length);
    }
    
    /**
     * Returns how many pieces remain to be placed on the board
     * @return number of pieces left off of the board
     */
    public int getRemainingPieceCount() {
        int counter = 0;
        for (Piece p : piecesLeft)
            counter += (p == null) ? 0 : 1;
        return counter;
    }
    
    /**
     * Method checks for half Quartos, almost complete Quartos, and full
     * Quartos. When it detects a Quarto, it stops checking all other lines
     * for information because it assumes that the Quarto will lead to the
     * end of the game.
     * 
     * If a Quarto has been detected, one of these three outputs will occur:
     * row #: a Quarto has been discovered at row # (between 0 and 3)
     * col #: a Quarto has been discovered at column # (between 0 and 3)
     * dgn #: a Quarto has been discovered at diagonal 0 (top let to bottom
     * right) or diagonal 1 (top right to bottom left)
     * 
     * If no Quarto has been detected, then the method returns "no quarto"
     * 
     * @return a String indicating whether a Quarto! has been detected, 
     * and where
     */
    public String checkForQuarto() 
    {
        Piece[] check;
        String result;
        
        //empty halfQuartos and almostQuartos so they cam be refilled
        halfQuartos.clear();
        almostQuartos.clear();
        
        //check rows
        for (int row = 0; row < 4; row++) {
            check = new Piece[]{board[row][0], board[row][1], board[row][2], board[row][3]};
            result = Piece.comparePieces(check);
            if (result.contains("1") || result.substring(1).contains("2")) {
                if (result.charAt(0) == '2')
                    halfQuartos.add(result.substring(1));
                if (result.charAt(0) == '3')
                    almostQuartos.add(result.substring(1));
                if (result.charAt(0) == '4')
                    return "row " + row;
            }
        }
        
        //check cols
        for (int col = 0; col < 4; col++) {
            check = new Piece[]{board[0][col], board[1][col], board[2][col], board[3][col]};
            result = Piece.comparePieces(check);
            if (result.contains("1") || result.substring(1).contains("2")) {
                if (result.charAt(0) == 2)
                    halfQuartos.add(result.substring(1));
                if (result.charAt(0) == 3)
                    almostQuartos.add(result.substring(1));
                if (result.charAt(0) == 4)
                    return "col " + col;
            }
        }
        
        //check diagonal
        check = new Piece[]{board[0][0], board[1][1], board[2][2], board[3][3]};
        result = Piece.comparePieces(check);
        if (result.contains("1") || result.substring(1).contains("2")) {
            if (result.charAt(0) == 2)
                halfQuartos.add(result.substring(1));
            if (result.charAt(0) == 3)
                almostQuartos.add(result.substring(1));
            if (result.charAt(0) == 4)
            return "dgn 0";
        }
        
        check = new Piece[]{board[0][3], board[1][2], board[2][1], board[3][0]};
        result = Piece.comparePieces(check);
        if (result.contains("1") || result.substring(1).contains("2")) {
            if (result.charAt(0) == 2)
                halfQuartos.add(result.substring(1));
            if (result.charAt(0) == 3)
                almostQuartos.add(result.substring(1));
            if (result.charAt(0) == 4)
                return "dgn 1";
        }
        
        return NO_QUARTO;
    }
    
    
    
    
    // ------------------------------------------------------------------ //
    // ----- HELPER METHODS --------------------------------------------- //
    // ------------------------------------------------------------------ //

    /**
     * Helper method that checks whether the piece with the given ID exists
     * off of the board. Used to determine whether that piece can be given
     * to the opponent.
     * @param pieceID
     * @return 
     */
    private boolean canMovePiece(int pieceID) {
        //if a piece with that ID exists aoff the board, return true
        for (Piece p : piecesLeft) {
            if (p == null)
                continue;
            if (p.ID == pieceID)
                return true;
        }
        //else return false
        return false;
    }
    
    /**
     * Helper method that checks if the coordinates yield a valid empty space.
     * Returns true if so, and false if not.
     * 
     * @param row row that the Piece is in
     * @param col column that the Piece is in
     * @return the Piece on the space, or null if there is no piece
     */
    private boolean isSpaceValid(int row, int col) 
    {
        //check for valid coords
        if (row < 4 && col < 4 && row >= 0 && col >= 0)
            //return true if space is empty
            return board[row][col] == null;
        //otherwise return false
        return false;
    }

    /**
     * Helper method for fetching a piece from off the board to be placed on
     * the board. The piece will be removed from the piecesLeft array as
     * it will presumably be placed on the board (if not, then the piece
     * returned will be lost if not saved somewhere else.)
     * 
     * Pre: the piece with the given ID must be a valid piece that exists off
     * of the board (in the piecesLeft array). The Pieces are also sorted
     * in the array so that the piece ID corresponds with the index position
     * of the Piece in the array, so it is assumed that this array has not
     * been tampered with.
     * 
     * Post: the piece asked for is removed from piecesLeft and returned by
     * the method
     * 
     * @param pieceID id of the desired piece
     * @return piece asked for with the corresponding id
     */
    private Piece getPiece(int pieceID) {
        Piece p = piecesLeft[pieceID];
        piecesLeft[pieceID] = null;
        return p;
    }
    
}
