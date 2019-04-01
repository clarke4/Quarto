//Piece.java
//By Sarah Clarke

/*
TODO
Are the enums necessary/computationally more difficult?
They aren't used anywhere outside of Piece
*/

/**
 * This class defines a piece for a game of Quarto. It contains four enums
 * that define the height, colour, shape, and solidity of the piece. 
 * @author Sarah Clarke - A00420598
 */
public class Piece {
    
    // ----- ENUMS (perhaps redundant/replaceable) ---------------------- //
    public enum Height { TALL, SHORT } 
    public enum Color { LIGHT, DARK }
    public enum Shape { CIRCLE, SQUARE }
    public enum Material { SOLID, HOLLOW }
    // ------------------------------------------------------------------ //
    
    // ---- INSTANCE VARIABLES ------------------------------------------ //
    public final int ID;
    public final Height HEIGHT;
    public final Color COLOR;
    public final Shape SHAPE;
    public final Material MATERIAL;
    // ------------------------------------------------------------------ //
    
    
    // ---- CONSTRUCTOR ------------------------------------------------ //
    
    /**
     * Creates a Piece for as game of Quarto that has 
     * 
     * @param id the given ID for the piece
     * @param a indicates whether the piece is tall or not
     * @param b indicates whether the piece is light or not
     * @param c indicates whether the piece is circular or not
     * @param d indicates whether the piece is solid or not
     */
    public Piece(int id, boolean a, boolean b, boolean c, boolean d) {
        this.HEIGHT = a ? Height.TALL : Height.SHORT;
        this.COLOR = b ? Color.LIGHT : Color.DARK;
        this.SHAPE = c ? Shape.CIRCLE : Shape.SQUARE;
        this.MATERIAL = d ? Material.SOLID : Material.HOLLOW;
        this.ID = id;
    }
    
    // ------------------------------------------------------------------ //
    
    
    
    // ------------------------------------------------------------------ //
    // ---- PUBLIC INSTANCE METHODS ------------------------------------- //
    // ------------------------------------------------------------------ //
    
    /**
     * Prints the piece ID and traits.
     * @param t whether or not it prints in-depth info
     * @return piece info
     */
    public String toString(boolean t) {
        if (!t) {
            return toString();
        }
        return "Piece #" + ID + " is " + 
                (HEIGHT == Height.TALL ? "tall" : "short") + ", " + 
                (COLOR == Color.LIGHT ? "light" : "dark") + ", " + 
                (SHAPE == Shape.CIRCLE ? "circular" : "square") + ", and " + 
                (MATERIAL == Material.SOLID ? "solid" : "hollow") + ".";
    }
    
    /**
     * Returns a four character String describing the piece.
     * First character: 1 means tall, 2 means short
     * Second character: 1 means light, 2 means dark
     * Third character: 1 means circular, 2 means square
     * Fourth character: 1 means solid, 2 means hollow
     * 
     * @return piece info
     */
    @Override
    public String toString() {
        return  (HEIGHT == Height.TALL ? "1" : "2") +
                (COLOR == Color.LIGHT ? "1" : "2") +
                (SHAPE == Shape.CIRCLE ? "1" : "2") +
                (MATERIAL == Material.SOLID ? "1" : "2");
    }
    
    /**
     * A hack method for determining how two pieces are similar.
     * 
     * First digit is height (1 is tall, 2 is short)
     * Second digit is color (1 is light, 2 is dark)
     * Third digit is shape (1 is circular, 2 is square)
     * Fourth digit is material (1 is solid, 2 is hollow)
     * 0 means that the two pieces do not share that trait.
     * 
     * This method will not warn you if you attempt to compare a piece
     * with itself, and will simply display results indicating that the
     * pieces share identical traits without telling you that it is the
     * same piece.
     * 
     * @param p the piece being compared to this instance of a piece
     * @return a String of length 4 that tells you how the pieces are similar
     */
    public String equals(Piece p) {
        int a = p.HEIGHT == this.HEIGHT ? 
                (p.HEIGHT == Height.TALL ? 1 : 2) : 0;
        int b = p.COLOR == this.COLOR ? 
                (p.COLOR == Color.LIGHT ? 1 : 2) : 0;
        int c = p.SHAPE == this.SHAPE ? 
                (p.SHAPE == Shape.CIRCLE ? 1 : 2) : 0;
        int d = p.MATERIAL == this.MATERIAL ? 
                (p.MATERIAL == Material.SOLID ? 1 : 2) : 0;
        return a+b+c+d+"";
    }
    
    
    // ------------------------------------------------------------------ //
    // ---- PUBLIC CLASS METHODS ---------------------------------------- //
    // ------------------------------------------------------------------ //
    
    /**
     * Determines whether all of the pieces in an array of pieces share any
     * traits. Returns a String of length 5 with a character per trait that
     * states whether that trait is shared.
     * 
     * Character 0: the number of pieces it scanned through.
     * Character 1: 1 means tall, 2 means short.
     * Character 2: 1 means light, 2 means dark
     * Character 3: 1 means circular, 2 means square
     * Character 4: 1 means solid, 2 means hollow
     * A 0 in any of these four positions means that the trait isn't shared
     * amongst the pieces in the array examined.
     * 
     * The input array can have null spots. If there are identical pieces in
     * the array they are treated like separate pieces, so take care to not put
     * duplicates in the array. This method also gracefully handles null
     * values in the array by ignoring them.
     * 
     * @param p array of pieces
     * @return String identifying what traits these pieces share
     */
    public static String comparePieces(Piece[] p) {
        Height h = null;
        Color c = null;
        Shape s = null;
        Material m = null;
        boolean sameH = true, sameC = true, sameS = true, sameM = true;
        int pieceCounter = 0;
        
        for (Piece piece : p) {
            if (piece == null)
                continue;
            pieceCounter++;
            //check for same height
            h = (h==null) ? piece.HEIGHT : h;
            sameH = sameH && piece.HEIGHT == h;
            //check for same height
            c = (c==null) ? piece.COLOR : c;
            sameC = sameC && piece.COLOR == c;
            //check for same height
            s = (s==null) ? piece.SHAPE : s;
            sameS = sameS && piece.SHAPE == s;
            //check for same height
            m = (m==null) ? piece.MATERIAL : m;
            sameM = sameM && piece.MATERIAL == m;
        }
        
        return pieceCounter
                + (sameH ? (h==Height.TALL ? "1" : "2") : "0")
                + (sameC ? (c==Color.LIGHT ? "1" : "2") : "0")
                + (sameS ? (s==Shape.CIRCLE ? "1" : "2") : "0")
                + (sameM ? (m==Material.SOLID ? "1" : "2") : "0");
    }
    
}
    
