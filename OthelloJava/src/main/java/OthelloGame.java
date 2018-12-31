import java.io.*;
import java.util.*;
import java.lang.Math;
public class OthelloGame {
    public static final String DEFAULT_BOARD = "---------------------------xo------ox---------------------------";
    public static final String DEFAULT_PLAY = "x";
    public static final int DEFAULT_DEPTH = 2;
    public static final String MOVES = "xo";
    public static final ArrayList<Coordinate> NEIGHBORS = new ArrayList<Coordinate>(Arrays.asList(new Coordinate(-1, -1), new Coordinate(-1, 0), new Coordinate(-1, 1), new Coordinate(0, -1), new Coordinate(0, 1), new Coordinate(1, -1), new Coordinate(1, 0), new Coordinate(1, 1)));
    private String[][] board = new String[8][8];
    private String[][] origBoard = new String[8][8];
    private String play, other;
    private int depth, xCount, oCount;
    private boolean orig;
    private ArrayList<OthelloGame> children = new ArrayList<OthelloGame>();
    private HashMap<String, Integer> scores = new HashMap<String, Integer>();
    private HashMap<String, Object> bestMove = new HashMap<String, Object>();
    private Coordinate parentCoord;

    public OthelloGame(int depth) {
        this(DEFAULT_BOARD, DEFAULT_PLAY, depth, true, null);
    }

    public OthelloGame(String board, String play, int depth, boolean orig, Coordinate parentCoord) {
        for (int j = 0; j < 8; j++) {
            int i = 7 - j;
            this.origBoard[i] = board.substring(i * 8, (i + 1) * 8).split("");
            this.board[i] = board.substring(i * 8, (i + 1) * 8).split("");
        }
        this.play = play;
        this.other = MOVES.replace(play, "");
        this.depth = depth;
        this.orig = orig;
        this.parentCoord = parentCoord;
        this.xCount = 64 - board.replace("x", "").length();
        this.oCount = 64 - board.replace("o", "").length();
        this.scores.put("x", xCount);
        this.scores.put("o", oCount);
        this.bestMove.put("coord", parentCoord);
        this.bestMove.put("scores", scores);
        printBoard();
        System.out.println(getPossiblePlays());

    }


    public static void main(String[] args) {
        OthelloGame o = new OthelloGame(2);//"default", "default", 0, true, null);
    }



    private String[][] matrixCopy(String[][] a) {
        String[][] d = new String[8][8];
        for (int i = 0; i < 8; i++) {
            d[i] = a[i];
        }
        return d;
    }

    private HashSet<Coordinate> getPossiblePlays() {
        HashSet<Coordinate> plays = new HashSet<Coordinate>();
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                if (board[y][x].equals(other)) {
                    plays.addAll(getNeighbors(new Coordinate(x, y)));
                }
            }
        }
        plays.removeIf(p -> !isValidPlay(p));
        return plays;

    }

    private HashSet<Coordinate> getNeighbors(Coordinate coord) {
        int x = coord.x;
        int y = coord.y;
        HashSet<Coordinate> neighbors = new HashSet<Coordinate>();
        for (Coordinate c : NEIGHBORS){
                int x1 = c.x + x;
                int y1 = c.y + y;
                if (0 <= x1 && x1 < 8 && 0 <= y1 && y1 < 8 && board[y1][x1].equals("-")) {
                    neighbors.add(new Coordinate(x1, y1));
                }

        }
        return neighbors;
    }

    public void printBoard() {
        for (String[] line : board){
                System.out.println(Arrays.toString(line).replace(", ", " ").replace("[", "").replace("]", ""));
        }
        System.out.println("\n");

    }

    private boolean isValidPlay(Coordinate coord) {
        int x = coord.x;
        int y = coord.y;
        boolean[] otherFound = {false, false, false, false, false, false, false, false};
        //Right
        if (x < 7 && board[y][x + 1].equals(other)) {
            for(int x1 = x + 1; x1 < 8; x1++) {
                if (board[y][x1].equals(other)) {
                    otherFound[0] = true;
                } else if (board[y][x1].equals(play) && otherFound[0]) {
                    return true;
                } else break;
            }
        }

        //Left
        if (x > 0 && board[y][x - 1].equals(other)) {
            for(int x1 = x - 1; x1 >= 0; x1--){
                if (board[y][x1].equals(other)) {
                    otherFound[1] = true;
                } else if (board[y][x1].equals(play) && otherFound[1]) {
                    return true;
                } else break;
            }
        }

        //Up
        if (y > 0 && board[y - 1][x].equals(other)) {
            for (int y1 = y - 1; y1 >= 0; y1--) {
                if (board[y1][x].equals(other)) {
                    otherFound[2] = true;
                } else if (board[y1][x].equals(play) && otherFound[2]) {
                    return true;
                } else break;
            }
        }

        //Down
        if (y < 7 && board[y + 1][x].equals(other)) {
            for(int y1 = y + 1; y1 < 8; y1++){
                if (board[y1][x].equals(other)) {
                    otherFound[3] = true;
                } else if (board[y1][x].equals(play) && otherFound[3]) {
                    return true;
                } else break;
            }
        }
        //Y = x right
        for (int inc = 1; inc < 8 - Math.max(x, y) - 1; inc++){//(inc in xrange(1, 8 - Math.max(x, y) - 1)) {
            if (board[y - inc][x + inc].equals(other)) {
                otherFound[4] = true;
            } else if (board[y - inc][x + inc].equals(play) && otherFound[4]) {
                return true;
            } else break;
        }

        //Y = x left
        for (int inc = 0; inc < Math.min(x, y); inc++){//(inc in xrange(0, Math.min(x, y))) {
            if (board[y + inc][x - inc].equals(other)) {
                otherFound[5] = true;
            } else if (board[y + inc][x - inc].equals(play) && otherFound[5]) {
                return true;
            } else break;
        }
        //Y = -x left
        for (int inc = 0; inc < Math.min(x, y); inc++){
            if (board[y - inc][x - inc].equals(other)) {
                otherFound[6] = true;
            } else if (board[y - inc][x - inc].equals(play) && otherFound[6]) {
                return true;
            } else break;
        }
        //Y = -x right
        for (int inc = 1; inc < 8 - Math.max(x, y) - 1; inc++){
            if (board[y + inc][x + inc].equals(other)) {
                otherFound[7] = true;
            } else if (board[y + inc][x + inc].equals(play) && otherFound[7]) {
                return true;
            } else break;
        }
        return false;
    }

}

