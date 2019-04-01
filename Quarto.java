//Quarto.java
//By Sarah Clarke

import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;


/*
TODO:
Revise init so args can be used to initialize players
(so that if two AIs play they can do it without outside prompting)
*/


/**
 * This is the test driver class for a game of Quarto.
 * @author Sarah Clarke - A00425098
 */
public class Quarto {

    private void pause() {
        if (withPauses) {
            System.out.print("[Enter] ...");
            kbd.nextLine();
        }
    }
    
    // ----- INSTANCE VARAIBLES ----------------------------------------- //
    public Board board;
    public Player player1;
    public Player player2;
    public boolean withPauses = true;
    public Scanner kbd = new Scanner(System.in);
    private FileWriter recordkeeper;
    // ------------------------------------------------------------------ //
    
    // ----- CLASS CONSTANTS -------------------------------------------- //
    public static final String HORIZONTAL = "+----+----+----+----+\n";
    // ------------------------------------------------------------------ //

    
    // ------------------------------------------------------------------ //
    // ----- CLASS DRIVER ----------------------------------------------- //
    // ------------------------------------------------------------------ //
    
    /**
     * Class driver
     * @param args
     */
    public static void main(String[] args) {
        //initialize game
        Quarto obj = new Quarto();
        obj.recordkeeper = null;
        obj.init(args);
        
        
        //choose player1
        Player currentPlayer = obj.player1;
        Piece currentPiece;
        String winSearcher = Board.NO_QUARTO;
        int currentPlayerNumber = 1;
        String results;

//        //TEST
//        quartoTester(obj.board);
//        winSearcher = obj.board.checkForQuarto();
        
        //print board
        obj.printBoard();
        obj.pause();
        System.out.println();
        
        while (
                winSearcher.equals(Board.NO_QUARTO) 
                && obj.board.getRemainingPieceCount() != 0
        ) {
            
            //player1 chooses a piece
            System.out.println("\n\tPlayer " + currentPlayerNumber 
                    + " chooses a piece.");
            currentPiece = currentPlayer.getPiece();
            
            //switch active player
            System.out.println("\n\tSwitching players... ");
            currentPlayer = (currentPlayer == obj.player1) 
                    ? obj.player2 : obj.player1;
            currentPlayerNumber = currentPlayerNumber == 2 ? 1 : 2;
            
            //player2 moves the piece
            System.out.println("\tPlayer " + currentPlayerNumber 
                    + "'s turn to place their piece.");
            currentPlayer.move(currentPiece);
            
            //print board
            obj.printBoard();
            obj.pause();
            
            //check for Quarto
            winSearcher = obj.board.checkForQuarto();
        }
        
        //if ended with tie, print necessary info
        if (winSearcher.equals(Board.NO_QUARTO))
            results = "Tie!";
        else {
            //if Quarto found, last player who moved has won
            String winner = currentPlayer == obj.player1 ? "1" : "2";
            results = "The winner is player " + winner + " with " 
                    + winSearcher + " and " 
                    + (16-obj.board.getRemainingPieceCount()) 
                    + " pieces on the board!";
        }
        
        System.out.println(results);
        if (obj.recordkeeper != null) obj.writeResults(results);
        System.out.println("Thank you for playing!");
        obj.pause();
        
        
    }
    
    
    // ------------------------------------------------------------------ //
    // ----- CLASS CONSTRUCTOR ------------------------------------------ //
    // ------------------------------------------------------------------ //
    
    /**
     * Constructor for a game of Quarto
     */
    public Quarto() {
        board = new Board();
    }
    
    
    // ------------------------------------------------------------------ //
    // ----- INSTANCE METHODS ------------------------------------------- //
    // ------------------------------------------------------------------ //
    
    /**
     * Prints the Board's current state into the console.
     */
    public void printBoard() {
        Piece[][] b = board.getBoard();
        
        System.out.print("\n\n\n\n\n\n\n\n\n\n     0    1    2    3\n   ");
        System.out.print(HORIZONTAL
        + " 0 | " + (b[0][0] == null ? "  " : b[0][0].toString().substring(0, 2))
        + " | " + (b[0][1] == null ? "  " : b[0][1].toString().substring(0, 2))
        + " | " + (b[0][2] == null ? "  " : b[0][2].toString().substring(0, 2))
        + " | " + (b[0][3] == null ? "  " : b[0][3].toString().substring(0, 2))
        + " |\n"
        + "   | " + (b[0][0] == null ? "  " : b[0][0].toString().substring(2, 4))
        + " | " + (b[0][1] == null ? "  " : b[0][1].toString().substring(2, 4))
        + " | " + (b[0][2] == null ? "  " : b[0][2].toString().substring(2, 4))
        + " | " + (b[0][3] == null ? "  " : b[0][3].toString().substring(2, 4))
        + " |\n   " + HORIZONTAL
        + " 1 | " + (b[1][0] == null ? "  " : b[1][0].toString().substring(0, 2))
        + " | " + (b[1][1] == null ? "  " : b[1][1].toString().substring(0, 2))
        + " | " + (b[1][2] == null ? "  " : b[1][2].toString().substring(0, 2))
        + " | " + (b[1][3] == null ? "  " : b[1][3].toString().substring(0, 2))
        + " |\n"
        + "   | " + (b[1][0] == null ? "  " : b[1][0].toString().substring(2, 4))
        + " | " + (b[1][1] == null ? "  " : b[1][1].toString().substring(2, 4))
        + " | " + (b[1][2] == null ? "  " : b[1][2].toString().substring(2, 4))
        + " | " + (b[1][3] == null ? "  " : b[1][3].toString().substring(2, 4))
        + " |\n   " + HORIZONTAL
        + " 2 | " + (b[2][0] == null ? "  " : b[2][0].toString().substring(0, 2))
        + " | " + (b[2][1] == null ? "  " : b[2][1].toString().substring(0, 2))
        + " | " + (b[2][2] == null ? "  " : b[2][2].toString().substring(0, 2))
        + " | " + (b[2][3] == null ? "  " : b[2][3].toString().substring(0, 2))
        + " |\n"
        + "   | " + (b[2][0] == null ? "  " : b[2][0].toString().substring(2, 4))
        + " | " + (b[2][1] == null ? "  " : b[2][1].toString().substring(2, 4))
        + " | " + (b[2][2] == null ? "  " : b[2][2].toString().substring(2, 4))
        + " | " + (b[2][3] == null ? "  " : b[2][3].toString().substring(2, 4))
        + " |\n   " + HORIZONTAL
        + " 3 | " + (b[3][0] == null ? "  " : b[3][0].toString().substring(0, 2))
        + " | " + (b[3][1] == null ? "  " : b[3][1].toString().substring(0, 2))
        + " | " + (b[3][2] == null ? "  " : b[3][2].toString().substring(0, 2))
        + " | " + (b[3][3] == null ? "  " : b[3][3].toString().substring(0, 2))
        + " |\n"
        + "   | " + (b[3][0] == null ? "  " : b[3][0].toString().substring(2, 4))
        + " | " + (b[3][1] == null ? "  " : b[3][1].toString().substring(2, 4))
        + " | " + (b[3][2] == null ? "  " : b[3][2].toString().substring(2, 4))
        + " | " + (b[3][3] == null ? "  " : b[3][3].toString().substring(2, 4))
        + " |\n   " + HORIZONTAL
        );
        
    }

    /**
     * Initializes the game by prompting the user to enter whether players 1
     * and 2 will be human or AI.
     */
    private void init(String[] args) {
        String answer;
        if (areValidArgs(args)) {
            return;
        }
        withPauses = true;
        
        //initialize variables
        int player = 1; //which player being initialized
        int playerType = 0; //the type of player, -1 is AI and 1 is Human
        
        //while both players haven't been initialized
        while (player < 3) {
            //prompt user for input
            System.out.print("Is player " + player 
                    + " a human or an AI? [H/A]: ");
            answer = kbd.next();
            kbd.nextLine();

            //analyzing answer
            switch(answer.toLowerCase().charAt(0)) {
                case 'h':
                    //player is human
                    System.out.println("Player " + player
                            + " is a human!");
                    playerType = 1;
                    break;
                case 'a':
                    //player is AI
                    System.out.println("Player " + player
                            + " is an AI!");
                    playerType = -1;
                    break;
                default:
                    //bad answer
                    System.out.println("Sorry, I didn't understand your "
                            + "input. Please try again.");
            }

            //creating player
            if (playerType != 0) {
                switch(player) {
                    case 1: 
                        //create player 1
                        player1 = playerType == -1 
                                ? new AIPlayer(board) 
                                : new HumanPlayer(board);
                        break;
                    case 2: 
                        //create player 2
                        player2 = playerType == -1 
                                ? new AIPlayer(board) 
                                : new HumanPlayer(board);;
                        break;
                }
                
                //reset playertype
                playerType = 0;
                //increment player
                player++;
            } //end switch
        } //end while
        System.out.print("Players initialized! Beginning the game... [Enter]");
        kbd.nextLine();
        System.out.println();
    } //end init
    
    /**
     * Takes in a board and writes a copy of a tie to it, excluding one piece.
     */
    private static void tieTester(Board board) {
        board.movePiece(3, 0, 0);
        board.movePiece(9, 0, 1);
        board.movePiece(2, 0, 2);
        board.movePiece(14, 0, 3);
        board.movePiece(6, 1, 0);
        board.movePiece(10, 1, 1);
        board.movePiece(5, 1, 2);
        board.movePiece(11, 1, 3);
        board.movePiece(8, 2, 0);
        board.movePiece(1, 2, 1);
        board.movePiece(12, 2, 2);
        board.movePiece(7, 2, 3);
        board.movePiece(0, 3, 0);
        board.movePiece(15, 3, 1);
        board.movePiece(4, 3, 2);
    }
    
    /**
     * Takes in a board and writes a copy of a tie to it, excluding one piece.
     */
    private static void quartoTester(Board board) {
        board.movePiece(12, 3, 0);
        board.movePiece(13, 2, 1);
        board.movePiece(14, 1, 2);
        board.movePiece(15, 0, 3);
    }

    private boolean areValidArgs(String[] args) {
        String p1, p2, pause, fileName;
        if(args.length < 3) return false;
        player1 = args[0].toLowerCase().charAt(0) == 'a' ? new AIPlayer(board)
                : args[0].toLowerCase().charAt(0) == '1' ? new AIPlayerV1(board)
                : args[0].toLowerCase().charAt(0) == '2' ? new AIPlayerV2(board)
                : new HumanPlayer(board);
        
        player2 = args[1].toLowerCase().charAt(0) == 'a' ? new AIPlayer(board)
                : args[1].toLowerCase().charAt(0) == '1' ? new AIPlayerV1(board)
                : args[1].toLowerCase().charAt(0) == '2' ? new AIPlayerV2(board)
                : new HumanPlayer(board);
        
        withPauses = (args[2].toLowerCase().charAt(0) == 'y');
        
        if (args.length < 4) return true;
        
        try
        {
            String filename= args[3];
            recordkeeper = new FileWriter(filename,true);
            return true;
        }
        catch(IOException ioe)
        {
            System.err.println("IOException: " + ioe.getMessage());
            return false;
        }
    }

    private void writeResults(String results) {
        try {
            recordkeeper.write(results + "\n");
            recordkeeper.close();
        } catch (IOException ex) {
            System.out.println("Couldn't write results to file.");
        }
    }
}
