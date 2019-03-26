package quarto;
//AIPlayer.java
//By Sarah Clarke

import java.util.ArrayList;

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
    
    
    public static Node startNode = new Node();
    public static Piece pieceGiven;
    public static Piece[][] board;
    public static ArrayList<Node> tree = new ArrayList();
    public static ArrayList<Node> copy = new ArrayList();
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
        
        if (game.getRemainingPieceCount() < 7) {
            //brute force method - Jacob
            return null;
            
        } else if (Board.getPartialQuartos(game.getBoard(), '3').isEmpty()) {
            //early game heuristic - Sarah
            if (pieceToGiveOpponent == null) {
                for (Piece p : game.getRemainingPieces()) {
                    if (p != null) {
                        return p;
                    }
                }
            }
            //Else, give chosen piece
            return pieceToGiveOpponent;
            
        } else {
            //midgame heuristic - Rajiv
            return noQuartoGetPiece();
        }
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
        if (game.getRemainingPieceCount() < 7) {
            //brute force method - Jacob
            pieceGiven = piece;
            return false;
            
        } else if (Board.getPartialQuartos(game.getBoard(), '3').isEmpty()) {
            //early game heuristic - Sarah
            
            String coords = earlyGameHeuristic(piece);
            //get coords
            int row = (char)coords.charAt(0)-'0';
            int col = (char)coords.charAt(1)-'0';

            boolean wasSuccess = game.movePiece(piece.ID, row, col);
            if (!wasSuccess) {
                System.out.println("Something went wrong and the AI move did not happen.");
            }
            return wasSuccess;

        } else {
            //midgame heuristic - Rajiv
            almostQuartoMove(piece);
            return true;
        }
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    // SARAH //
    
    
    
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
        ArrayList<String> choices = new ArrayList<>();
        
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
                choices.add(row + "" + col);
            }
        }
        
        if (!choices.isEmpty()) {
            //return random move from list
            return choices.get((int)(Math.random()*((double)choices.size())));
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
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    // RAJIV //
    
    
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
    
    
    
    //JACOB
    
    public static void buildTree(Node currentNode)
    {
        tree.add(currentNode);
        for (int i = 0; i < currentNode.children.size(); i++)
        {
            buildTree(currentNode.children.get(i));
        }
    }

    public static piece drive()
    {

        board = Board.getBoard();

        //create first node with current board
        startNode.state = board.clone();
        startNode.parent = null;
        startNode.turn = 1;
        //recursively get all possible states
        getTree(startNode);

        tree.clear();
        buildTree(startNode);
        // System.out.println(tree.size());

        eliminateLosingMoves(startNode);
        tree.clear();
        buildTree(startNode);
        // System.out.println(tree.size());

        //REMEBER AFTER THIS
        eliminateRedundantWins(startNode);
        tree.clear();
        buildTree(startNode);
        // System.out.println(tree.size());

        //copy after eliminating redundant/losing moves
        for (int u = 0; u < tree.size(); u++)
        {
            copy.add(tree.get(u));
        }

        int comp1 = 1;
        int comp2 = 0;

        //loop until the method no longer changes the tree
        while (comp1 != comp2)
        {
            tree.clear();
            buildTree(startNode);
            comp1 = tree.size();
            removeUnfavorable(startNode);
            tree.clear();
            buildTree(startNode);
            comp2 = tree.size();
        }

        tree.clear();
        buildTree(startNode);

        return makeMove();

    }

    //Get remaining pieces for a certain state
    public static Piece[] remainingPieces(Piece[][] thisBoard)
    {
        //All pieces
        ArrayList<Piece> allPieces = new ArrayList();
        {
            allPieces.add(new Piece(0, true, true, true, true));
            allPieces.add(new Piece(1, true, true, true, false));
            allPieces.add(new Piece(2, true, true, false, true));
            allPieces.add(new Piece(3, true, true, false, false));
            allPieces.add(new Piece(4, true, false, true, true));
            allPieces.add(new Piece(5, true, false, true, false));
            allPieces.add(new Piece(6, true, false, false, true));
            allPieces.add(new Piece(7, true, false, false, false));
            allPieces.add(new Piece(8, false, true, true, true));
            allPieces.add(new Piece(9, false, true, true, false));
            allPieces.add(new Piece(10, false, true, false, true));
            allPieces.add(new Piece(11, false, true, false, false));
            allPieces.add(new Piece(12, false, false, true, true));
            allPieces.add(new Piece(13, false, false, true, false));
            allPieces.add(new Piece(14, false, false, false, true));
            allPieces.add(new Piece(15, false, false, false, false));
        };

        //remove any piece on board from all pieces
        for (int i = 0; i < 4; i++)
        {
            for (int j = 0; j < 4; j++)
            {
                if (thisBoard[i][j] != null)
                {
                    for (int g = 0; g < allPieces.size(); g++)
                    {
                        if (allPieces.get(g).toString()
                            .equals(thisBoard[i][j].toString()))
                        {
                            allPieces.remove(g);
                        }
                    }
                }
            }
        }

        //Create a piece[] with all pieces not removed
        Piece[] returnPieces = new Piece[allPieces.size()];
        for (int o = 0; o < allPieces.size(); o++)
        {
            returnPieces[o] = allPieces.get(o);
        }

        return returnPieces;
    }

    //Get all possible routes
    private static void getTree(Node clone)
    {
        //create copy of board
        Piece[][] currentBoard = new Piece[4][4];
        currentBoard[0] = clone.state[0].clone();
        currentBoard[1] = clone.state[1].clone();
        currentBoard[2] = clone.state[2].clone();
        currentBoard[3] = clone.state[3].clone();

        //get remaining pieces
        Piece[] remaining;
        if (clone.turn == 1)
        {
            Piece[] onePiece = new Piece[]
            {
                pieceGiven
            };
            remaining = onePiece;
        }
        else
        {
            remaining = remainingPieces(currentBoard);
        }
        //for each remaining piece
        for (int k = 0; k < remaining.length; k++)
        {
            //Check all squares on the board that are null
            for (int i = 0; i < 4; i++)
            {
                for (int j = 0; j < 4; j++)
                {
                    if (currentBoard[i][j] == null)
                    {

                        //set each null square to the piece
                        //and call getTree
                        Piece[][] boardClone = new Piece[4][4];
                        boardClone[0] = currentBoard[0].clone();
                        boardClone[1] = currentBoard[1].clone();
                        boardClone[2] = currentBoard[2].clone();
                        boardClone[3] = currentBoard[3].clone();

                        boardClone[i][j] = remaining[k];
                        //create node with relevant data
                        Node node = new Node();
                        node.state = boardClone.clone();

                        node.turn = clone.turn + 1;
                        node.parent = clone;
                        node.piecePlaced = remaining[k];
                        clone.children.add(node);
                        //   nodeList.add(node);

                        if (!checkForQuarto(node.state))
                        {
                            getTree(node);
                        }
                    }
                }
            }
        }
    }

    private static void eliminateLosingMoves(Node node)
    {
        for (int i = 0; i < node.children.size(); i++)
        {
            ArrayList<Piece> toRemove = new ArrayList();
            for (int k = 0; k < node.children.get(i).children.size(); k++)
            {
                if (checkForQuarto(node.children.get(i).children.get(k).state))
                {
                    toRemove.add(node.children.get(i).children.get(k).piecePlaced);
                }
            }
            //remove all pieces where opponent can win
            for (int z = 0; z < toRemove.size(); z++)
            {
                for (int f = 0; f < node.children.get(i).children.size(); f++)
                {
                    if (toRemove.get(z).toString().equals(node.children
                        .get(i).children.get(f).piecePlaced.toString()))
                    {
                        node.children.get(i).children.remove(f);
                        f--;
                    }
                }
            }
        }

        //remove children that arent quarto and have no children
        for (int g = 0; g < node.children.size(); g++)
        {
            if (node.children.get(g).children.isEmpty()
                && !checkForQuarto(node.children.get(g).state))
            {
                node.children.remove(g);
                g--;
            }
            else
            {
                for (int h = 0; h < node.children.get(g).children.size(); h++)
                {
                    eliminateLosingMoves(node.children.get(g).children.get(h));
                }
            }
        }
    }

    public static void eliminateRedundantWins(Node node)
    {

        //if you can Quarto with a move, all other moves can be pruned
        for (int i = 0; i < node.children.size(); i++)
        {
            if (checkForQuarto(node.children.get(i).state))
            {
                ArrayList<Node> toAdd = new ArrayList();
                for (int z = 0; z < node.children.size(); z++)
                {
                    if (!node.children.get(z).piecePlaced.toString().
                        equals(node.children.get(i).piecePlaced.toString()))
                    {
                        toAdd.add(node.children.get(z));
                    }
                }
                toAdd.add(node.children.get(i));
                node.children = toAdd;
            }
            else
            {
                eliminateRedundantWins(node.children.get(i));
            }
        }
    }

    private static void removeUnfavorable(Node node)
    {
        //if off turn (AI player)
        if (node.turn % 2 == 1)
        {
            ArrayList<Node> toAdd = new ArrayList();
            for (int i = 0; i < node.children.size(); i++)
            {
                if (!(!checkForQuarto(node.children.get(i).state)
                    && node.children.get(i).children.isEmpty()))
                {
                    toAdd.add(node.children.get(i));
                    removeUnfavorable(node.children.get(i));
                }
            }
            node.children = toAdd;
        }
        //human player
        else
        {
            ArrayList<Piece> toRemove = new ArrayList();
            for (int i = 0; i < node.children.size(); i++)
            {

                if (!checkForQuarto(node.children.get(i).state)
                    && node.children.get(i).children.isEmpty())
                {
                    toRemove.add(node.children.get(i).piecePlaced);
                }
            }

            ArrayList<Node> toAdd = new ArrayList();
            for (int j = 0; j < node.children.size(); j++)
            {
                int isGood = 1;
                for (int z = 0; z < toRemove.size(); z++)
                {
                    if (toRemove.get(z).toString().equals(node.children.get(j).piecePlaced.toString()))
                    {
                        isGood = 0;
                    }
                }
                if (isGood == 1)
                {
                    toAdd.add(node.children.get(j));
                    removeUnfavorable(node.children.get(j));
                }
            }
            node.children = toAdd;
        }
    }

    private static Piece makeMove()
    {
        //You lost so make first move
        if (copy.size() == 1)
        {
            for (int i = 0; i < 4; i++)
            {
                for (int k = 0; k < 4; k++)
                {
                    if (board[i][k] == null)
                    {
                        Board.movePiece(pieceGiven.ID, i, k);
                        return remainingPieces(board)[0];
                    }
                }

            }
        }
        //can win
        else
        {
            if (startNode.children.isEmpty())
            {
                int foundQuarto = 1;
                for (int s = 0; s < copy.size(); s++)
                {
                    if (checkForQuarto(copy.get(s).state))
                    {
                        foundQuarto = 0;
                        Node current = copy.get(s);
                        while (current.parent.parent.parent != null)
                        {
                            current = current.parent;
                        }
                        int row = -1;
                        int col = -1;

                        for (int i = 0; i < 4; i++)
                        {
                            for (int k = 0; k < 4; k++)
                            {
                                if ((current.parent.state[i][k] != null && current.parent.parent.state[i][k] == null)
                                    || (current.parent.state[i][k] == null && current.parent.parent.state[i][k] != null))
                                {
                                    row = i;
                                    col = k;
                                }
                            }
                        }
                        System.out.println("");
                        Board.movePiece(pieceGiven.ID, row, col);
                        return current.piecePlaced;
                    }
                }
                if (foundQuarto == 1)
                {
                    for (int i = 0; i < 4; i++)
                    {
                        for (int k = 0; k < 4; k++)
                        {
                            if (board[i][k] == null)
                            {
                                Board.movePiece(pieceGiven.ID, i, k);
                                return remainingPieces(board)[0];
                            }
                        }
                    }
                }
            }
            else
            {
                int row = -1;
                int col = -1;

                for (int i = 0; i < 4; i++)
                {
                    for (int k = 0; k < 4; k++)
                    {
                        if ((startNode.state[i][k] != null && startNode.children.get(0).state[i][k] == null)
                            || (startNode.state[i][k] == null && startNode.children.get(0).state[i][k] != null))
                        {
                            row = i;
                            col = k;
                        }
                    }
                }

                Board.movePiece(pieceGiven.ID, row, col);
                return startNode.children.get(0).children.get(0).piecePlaced;

            }
        }
        return null;
    }

    //store relevant data
    public static class Node
    {
        public Piece[][] state;
        public int value;
        public Node parent;
        public int turn;
        public ArrayList<Node> children = new ArrayList();
        public Piece piecePlaced;
    }

    public static boolean checkForQuarto(Piece[][] board)
    {

        ArrayList<Piece[]> toCheck = new ArrayList();
//cols
        for (int x = 0; x < 4; x++)
        {
            int isGood = 0;
            Piece[] cols = new Piece[4];
            for (int y = 0; y < 4; y++)
            {
                if (board[y][x] != null)
                {
                    cols[y] = board[y][x];
                }
                else
                {
                    isGood = 1;
                }
            }
            if (isGood == 0)
            {
                toCheck.add(cols);
            }
        }
        //rows
        for (int x = 0; x < 4; x++)
        {
            int isGood = 0;
            Piece[] rows = new Piece[4];
            for (int y = 0; y < 4; y++)
            {
                if (board[x][y] != null)
                {
                    rows[y] = board[x][y];
                }
                else
                {
                    isGood = 1;
                }
            }
            if (isGood == 0)
            {
                toCheck.add(rows);
            }
        }
        //diag
        if (board[0][0] != null
            && board[1][1] != null
            && board[2][2] != null
            && board[3][3] != null)
        {
            Piece[] diag = new Piece[]
            {
                board[0][0], board[1][1], board[2][2], board[3][3]
            };
            toCheck.add(diag);
        }
        if (board[0][3] != null
            && board[1][2] != null
            && board[2][1] != null
            && board[3][0] != null)
        {
            Piece[] diag = new Piece[]
            {
                board[0][3], board[1][2], board[2][1], board[3][0]
            };
            toCheck.add(diag);
        }
        for (int s = 0; s < toCheck.size(); s++)
        {
            int check = 0;
            for (int d = 0; d < 4; d++)
            {
                check += Integer.parseInt(toCheck.get(s)[d].toString());
            }
            for (int h = 0; h < 4; h++)
            {
                if (Integer.toString(check).substring(h, h + 1).equals("8")
                    || Integer.toString(check).substring(h, h + 1).equals("4"))
                {
                    return true;
                }
            }
        }
        return false;
    }
  } 
    

