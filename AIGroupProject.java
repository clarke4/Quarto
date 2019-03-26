/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quarto;

import java.util.ArrayList;

/**
 * @author Jacob
 */
public class AIGroupProject
{

    public static Node startNode = new Node();
    public static Piece pieceGiven;
    public static Piece[][] board;
    public static ArrayList<Node> tree = new ArrayList();
    public static ArrayList<Node> copy = new ArrayList();

    public static void buildTree(Node currentNode)
    {
        tree.add(currentNode);
        for (int i = 0; i < currentNode.children.size(); i++)
        {
            buildTree(currentNode.children.get(i));
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {

        Board.movePiece(0, 0, 0);
        Board.movePiece(1, 0, 2);
        Board.movePiece(2, 1, 1);
        Board.movePiece(8, 3, 0);
        Board.movePiece(4, 2, 0);
        Board.movePiece(5, 0, 3);
        Board.movePiece(6, 3, 1);
        Board.movePiece(7, 1, 0);
        Board.movePiece(3, 3, 2);
        Board.movePiece(9, 2, 3);
        //Board.movePiece(10, 1, 2);
        Board.movePiece(11, 0, 1);
        Board.movePiece(12, 2, 1);

        board = Board.getBoard();
        printBoard(board);

        //if <= 6 empty spaces
        int check = 16;
        for (int i = 0; i < 4; i++)
        {
            for (int j = 0; j < 4; j++)
            {
                if (board[i][j] != null)
                {
                    check--;
                }
            }
        }
        if (check <= 7)
        {
            //going to be input
            pieceGiven = new Piece(14, false, false, false, true);
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
            // System.out.println(tree.size());

            makeMove();
        }

        //highest least number of 3 shared attribute blocks (Opponent heuristic)
        //create nodes to hold value of piece and heuristic value and parent node
        //place current piece in all possible places, add new state to array
        //for each piece reaminging, place it in all possible squares for each of the instances above (opponents move)
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

    //debugging method
    private static void printBoard(Piece[][] boardClone)
    {
        for (int i = 0; i < 4; i++)
        {
            for (int j = 0; j < 4; j++)
            {
                if (boardClone[i][j] == null)
                {
                    System.out.print("---- | ");
                }
                else
                {
                    System.out.print(boardClone[i][j].toString() + " | ");
                }
            }
            System.out.println("");
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

    private static void makeMove()
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
                        //give remainingPieces(board)[0];
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
                        //give current.piecePlaced
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
                                //give remainingPieces(board)[0];
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
                System.out.println("");
                Board.movePiece(pieceGiven.ID, row, col);
                board = Board.getBoard();
                printBoard(board);
                System.out.println("");
                System.out.println(startNode.children.get(0).children.get(0).piecePlaced.toString());
                System.out.println(startNode.children.get(0).children.get(0).piecePlaced.ID);
            }
        }
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
