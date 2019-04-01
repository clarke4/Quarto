//HumanPlayer.java
//By Sarah Clarke

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;


/**
 * This class creates a human player for Quarto that allows a user to
 * interact with the game as a player through console prompts.
 * @author Sarah Clarke - A00425098
 */
public class HumanPlayer implements Player {
    
    public Board game;
    
    public static void main(String[] args) {
        Quarto quarto = new Quarto();
        quarto.board.movePiece(1, 3, 3);
        quarto.board.movePiece(11, 2, 3);
        quarto.board.movePiece(12, 1, 3);
        quarto.board.movePiece(13, 1, 2);
        HumanPlayer obj = new HumanPlayer(quarto.board);
        System.out.println("hello");
        
        quarto.printBoard();
        System.out.println("/nRemaining pieces: ");
        obj.printRemainingPieces();
    }
    
    /**
     * Constructor for a human player
     * @param game 
     */
    public HumanPlayer(Board game) {
        this.game = game;
        Quarto o = new Quarto();
    }

    /**
     * Method for choosing the piece to give to the opponent. Prompts the
     * user to choose from the remaining pieces.
     * 
     * @return chosen piece
     */
    @Override
    public Piece getPiece() {
        Scanner kbd = new Scanner(System.in);
        System.out.println("Choose a piece for your opponent from the available pieces: ");
        ArrayList<Piece> piecesLeft = printRemainingPieces();
        
        Piece chosenPiece = null;
        boolean badAnswer = true;
        do {
            System.out.print("Which piece ID do you choose?: ");
            int answer = kbd.nextInt();
            kbd.nextLine();
            for (Piece p : piecesLeft) {
                if (p.ID == answer) {
                    badAnswer = false;
                    chosenPiece = p;
                }
            }
            if (badAnswer)
                System.out.println("Piece #" + answer + " doesn't exist!"
                        + "\nPlease choose again.");
        } while (badAnswer);
        return chosenPiece;
    }

    /**
     * Method for placing the piece given to the Player on the board.
     * Prompts the user to choose a valid square on which to place their
     * piece.
     * 
     * @param piece piece to place on board
     * @return whether the move was successful
     */
    @Override
    public boolean move(Piece piece) {
        //declare important vars
        boolean badInput = true;
        int row, col;
        Scanner kbd = new Scanner(System.in);
        
        //display given piece
        System.out.println("You've been given the following piece: ");
        System.out.printf(" #%02d: %s\n      %s", piece.ID, 
                piece.toString().substring(0, 2), piece.toString().substring(2));
        Piece[][] board = game.getBoard(); 
        do {
            //print prompt
            System.out.print("\nWhere do you want to move your piece?\n"
                    + "(input the row number, then the column number): ");
            
            row = kbd.nextInt();
            col = kbd.nextInt();
            if (row < 0 || col < 0 || row > 3 || col > 3) {
                System.out.println("That's not a valid spot! Please try again.");
            } else if (board[row][col] != null) {
                System.out.println("That space is occupied! Please try again.");
            } else {
                System.out.println("Move made!");
                game.movePiece(piece.ID, row, col);
                badInput = false;
            }
        } while (badInput);
        return true;
    }

    /**
     * Helper method for printing the remaining pieces into the console
     * for a human player to view.
     */
    private ArrayList<Piece> printRemainingPieces() {
        //eliminate nulls from piecesLeft
        ArrayList<Piece> remainingPieces = new ArrayList();
        for (Piece p : game.getRemainingPieces()) {
            if (p != null)
                remainingPieces.add(p);
        }
        
        //loop accomodates for two rows of pieces
        for (int x = 0; x < 16 && x < remainingPieces.size(); x+=8) {
            //print piece ID number
            for (int i = 0; i < 8 && i+x < remainingPieces.size(); i++) {
                Piece piece = remainingPieces.get(i+x);
                System.out.printf(" #%02d: ", piece.ID);
            }
            System.out.println();

            //print first half of piece
            for (int i = 0; i < 8 && i+x < remainingPieces.size(); i++) {
                Piece piece = remainingPieces.get(i+x);
                System.out.print("  " + piece.toString().substring(0, 2)  
                        + "  ");
            }
            System.out.println();

            //print second half of piece
            for (int i = 0; i < 8 && i+x < remainingPieces.size(); i++) {
                Piece piece = remainingPieces.get(i+x);
                System.out.print("  " + piece.toString().substring(2, 4)  
                        + "  ");
            }
            System.out.println("\n");
        }
        
        return remainingPieces;
    }
    
}
