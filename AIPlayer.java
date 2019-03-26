package quarto;
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
    
    private String chosenAttributes = "0000";
    private Piece pieceToGiveOpponent;
    int att1 = -1;
    int att2 = -1;
    
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
     * TODO: Feel free to add more commentary if you feel it necessary
     * 
     * @return chosen piece
     */
    @Override
    public Piece getPiece() {
        //If piece not generated, give random piece.
        if (pieceToGiveOpponent == null) {
            for (Piece p : game.getRemainingPieces()) {
                if (p != null) {
                    return p;
                }
            }
        }
        //Else, give chosen piece
        return pieceToGiveOpponent;
    }

    /**
     * Method for placing the piece given to the Player on the board
     * 
     * TODO: Feel free to add more commentary if you feel it necessary
     * 
     * @param piece piece to place on board
     * @return whether the move was successful
     */
    @Override
    public boolean move(Piece piece) {
        //if taking the first move, randomize
        
        //else, do what heuristic says
        String coords = earlyGameHeuristic(piece);
        //get coords
        int row = (char)coords.charAt(0)-'0';
        int col = (char)coords.charAt(1)-'0';
        
        boolean wasSuccess = game.movePiece(piece.ID, row, col);
        if (!wasSuccess) {
            System.out.println("Something went wrong and the AI move did not happen.");
        }
        return wasSuccess;
    }
    
    
    
    
    
    
    
    
    
    
    /**
     * A helper method that strategizes for the early game of Quarto.
     * This early game heuristic uses the strategy outlined on the webpage:
     * https://ourpastimes.com/win-quarto-2325455.html where there is a focus
     * on giving the opponent certain pieces based on the AI's goal attributes
     * (the attributes the AI is trying to win a Quarto with) and trying not 
     * to create rows of three pieces if there's a space that can create 
     * a line of two or less pieces.
     * 
     * @param piece the piece given to move with
     * @return a String indicating what move should be made
     */
    private String earlyGameHeuristic(Piece piece) {
        //if there are no lines of 2 pieces on the board, randomize move
        if (Board.getPartialQuartos(game.getBoard(), '2').isEmpty()) {
            while (true) {
                int row = (int)(Math.random()*4);
                int col = (int)(Math.random()*4);
                if (Board.isSpaceValid(game.getBoard(), row, col))
                    return row + "" + col;
            }
        }
        
        //get goal attributes
        if (!(chosenAttributes.contains("1") 
                && chosenAttributes.contains("2"))) {
            chosenAttributes = getChosenAttributes();
        }
        
        //choose place to put piece
        String move = getOptimalSpace(game.getBoard(), 2);
        
        //choose piece to give opponent
        //get remaining pieces
        Piece[] piecesLeft = game.getRemainingPieces();
        //filter out piecesthat opponent can use to win
        piecesLeft = filterBadPieces(piecesLeft);
        //choose a piece to give
        pieceToGiveOpponent = choosePiece(piecesLeft);        
        //return move
        return move;
    }
    
    /**
     * Takes in a board and an attribute and returns how many pieces on the
     * board have that attribute.
     * Attribute indicators:
     * 1 - tall        5 - circular
     * 2 - short       6 - square
     * 3 - light       7 - solid
     * 4 - dark        8 - hollow
     * 
     * @param board board being examined
     * @param attribute the attribute being searched for
     * @return number of pieces on the board with the attribute specified
     */
    private int numPiecesOnBoardWithAttribute(int attribute) {
        int counter = 0;
        int attrib = ((attribute + 1) / 2) - 1;
        int type = attribute % 2==0 ? 2 : 1;
        
        for (Piece[] row:game.getBoard()) {
            for (Piece p:row) {
                if (p != null) {
                    char c = p.toString().charAt(attrib);
                    counter += c == (char)type ? 1 : 0;
                }
            }
        }
        return counter;
    }
    
    private String getChosenAttributes() {
        //retrieve data about pieces on board
        int tallCount   = numPiecesOnBoardWithAttribute(1);
        int shortCount  = numPiecesOnBoardWithAttribute(2);
        int lightCount  = numPiecesOnBoardWithAttribute(3);
        int darkCount   = numPiecesOnBoardWithAttribute(4);
        int circleCount = numPiecesOnBoardWithAttribute(5);
        int squareCount = numPiecesOnBoardWithAttribute(6);
        int solidCount  = numPiecesOnBoardWithAttribute(7);
        int hollowCount = numPiecesOnBoardWithAttribute(8);
        
        //randomly choose a dominant trait to watch for
        boolean rand1 = Math.random() < 0.5;
        boolean rand2 = Math.random() < 0.5;
        
        char c1 = (rand1  && rand2)  && !(rand1  && rand2)  ? 0 
                : tallCount < shortCount ? '2' : '1';
        char c2 = (!rand1 && rand2)  && !(!rand1 && rand2)  ? 0 
                : lightCount < darkCount ? '2' : '1';
        char c3 = (rand1  && !rand2) && !(rand1  && !rand2) ? 0 
                : circleCount < squareCount ? '2' : '1';
        char c4 = (!rand1 && !rand2) && !(!rand1 && !rand2) ? 0 
                : solidCount < hollowCount ? '2' : '1';
        
        return c1+""+c2+""+c3+""+c4;
    }
    
    

    /**
     * Searches for the optimal space by choosing a space where all intersecting
     * lines (that is, the row, column, and diagonal lines the space is in) 
     * contain no more than numPiecesPerLine pieces in them.
     * If the method cannot find such a space, it returns null. Otherwise,
     * it returns a String of length 2 where the first character is the row
     * number and the second character is the column number.
     * 
     * @param board the board being searched for this optimal space
     * @param numPiecesPerLine the max number of pieces that can be in the
     * lines that the optimal space resides in
     * @return either a String indicating where the optimal space is or null
     * if no optimal space is found.
     */
    private String getOptimalSpace(Piece[][] board, int numPiecesPerLine) {
        String rowCoord = "", colCoord = "";
        
        //get rows with no pieces
        for (int i = 0; i < 4; i++) {
            rowCoord += 
                    Piece.comparePieces(board[i]).charAt(0) - '0' <= numPiecesPerLine
                    ? i : "";
        }
        //get cols with no pieces
        for (int i = 0; i < 4; i++) {
            Piece[] col = new Piece[]{board[0][i], board[1][i],
                                      board[2][i], board[3][i]};
            colCoord += Piece.comparePieces(col).charAt(0) - '0' <= numPiecesPerLine 
                    ? i : "";
        }
        
        //search through valid coords for good spot
        for (int x = 0; x < rowCoord.length(); x++) {
            for (int y = 0; y < colCoord.length(); y++) {
                int row = Integer.parseInt(rowCoord.substring(x, x+1));
                int col = Integer.parseInt(colCoord.substring(y, y+1));
                
                //check if space is empty
                if (!Board.isSpaceValid(board, row, col)) {
                    continue;
                }
                
                //check if interferes with diagonal (top left to bottom right)
                int numPiecesInDiag = 0;
                if (row - col == 0) {
                    Piece[] diag = new Piece[]{board[0][0], board[1][1], 
                        board[2][2], board[3][3]};
                    numPiecesInDiag = Integer.parseInt(Piece.comparePieces(diag).substring(0, 1));
                }
                
                if (numPiecesInDiag > numPiecesPerLine)
                    continue;

                //check if interferes with diagonal (bottom left to top right)
                if (row + col == 3) {
                Piece[] diag = new Piece[]{board[0][3], board[1][2], 
                    board[2][1], board[3][0]};
                numPiecesInDiag = Integer.parseInt(Piece.comparePieces(diag).substring(0, 1));
                }
                
                if (numPiecesInDiag > numPiecesPerLine)
                    continue;
                
                //move piece here
                return row + "" + col;
            }
        }
        
        //if no valid move is chosen, return null
        if (numPiecesPerLine < 4) {
            return getOptimalSpace(board, numPiecesPerLine+1);
        } else {
            //randomize move - failsafe
            while (true) {
                int row = (int)(Math.random()*4.0);
                int col = (int)(Math.random()*4.0);
                if (Board.isSpaceValid(board, row, col)) {
                    return row + "" + col;
                }
            }
        }
    }
    
    /**
     * Returns ID of optimal piece, or returns -1 if none was found.
     * 
     * @param remainingPieces array of pieces that can be chosen. It is
     * crucial that this set not contain a piece that, if given to the opponent,
     * would win them the game (if this is avoidable)
     * @return ID of good piece to give opponent, or -1 if there are none
     */
    private Piece choosePiece(Piece[] remainingPieces) {
        //if odd number of non-goal attribute on board, choose piece of
        //non-goal attribute. Else choose piece of goal attribute.
        int[] chosen = new int[]{0, 0, 0, 0};
        
        for (int i = 0; i < 4; i++) { //for all four attribute slots
            
            //if attribute is on AI's radar
            if (chosenAttributes.charAt(i) != 0) { 
                //save opposite att that is in chosenAttributes
                int att = chosenAttributes.charAt(i) == '1' ? 2 : 1;
                int numPieces = numPiecesOnBoardWithAttribute(att);
                //if there are odd num pieces with attribute attrib
                if (numPieces % 2 == 1) {
                    //then prioritize giving piece with attribute attrib
                    chosen[i] = (char)('0'+att);
                } else {
                    //else give piece with attribute in chosenAttribute
                    chosen[i] = att == 1 ? '2' : '1';
                }
                //save position of chosen attribute
                if (att1 == -1) {
                    att1 = i;
                } else {
                    att2 = i;
                }
            }
        }
        
        //search for piece with both attributes
        for (Piece p : remainingPieces) {
            if (p != null) {
                String piece = p.toString();
                if (piece.charAt(att1) == (char)('0'+chosen[att1]) &&
                        piece.charAt(att2) == (char)('0'+chosen[att2]))
                    return p;
            }
        }
        
        //if not found, search for piece with one attribute
        for (Piece p : remainingPieces) {
            if (p != null) {
                String piece = p.toString();
                if (piece.charAt(att1) == (char)('0'+chosen[att1]) ||
                        piece.charAt(att2) == (char)('0'+chosen[att2]))
                    return p;
            }
        }
        
        //if none found, return -1;
        return null;
    }

    private Piece[] filterBadPieces(Piece[] piecesLeft) {
        //TODO - write code
        return piecesLeft;
    }
    
}
