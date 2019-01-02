import java.io.*;
import java.util.*;

public class OthelloGame {
    public static final String DEFAULT_BOARD = "---------------------------xo------ox---------------------------";
    public static final String DEFAULT_PLAY = "x";
    public static final int DEFAULT_DEPTH = 2;
    public static final String MOVES = "xo";
    public static final ArrayList<Coordinate> NEIGHBORS = new ArrayList<Coordinate>(Arrays.asList(new Coordinate(-1, -1), new Coordinate(-1, 0), new Coordinate(-1, 1), new Coordinate(0, -1), new Coordinate(0, 1), new Coordinate(1, -1), new Coordinate(1, 0), new Coordinate(1, 1)));
    public String play, other;
    public HashMap<String, Integer> scores = new HashMap<String, Integer>();
    public HashMap<String, Object> bestMove = new HashMap<String, Object>();
    public Coordinate parentCoord;
    private String[][] board = new String[8][8];
    private String[][] origBoard = new String[8][8];
    private int depth, xCount, oCount;
    private boolean orig;
    private ArrayList<OthelloGame> children = new ArrayList<OthelloGame>();
    public OthelloGame(int depth) {
        this(DEFAULT_BOARD, DEFAULT_PLAY, depth, true, null);
    }
    public OthelloGame(String board, String play, int depth, boolean orig, Coordinate parentCoord) {
        for(int j = 0; j < 8; j++) {
            int i = 7 - j;// 7 - j;
            String[] temp = board.substring(i * 8, (i + 1) * 8).split("");
            for(int k = 0; k < 8; k ++){
                this.board[j][k] = temp[k];
                this.origBoard[j][k] = temp[k];
            }

        }
        this.depth = depth;
        if(orig){
        //    this.depth *= 2;
        }
        this.play = play;;
        this.other = MOVES.replace(play, "");
        this.orig = orig;
        this.parentCoord = parentCoord;
        this.xCount = 64 - board.replace("x", "").length();
        this.oCount = 64 - board.replace("o", "").length();
        this.scores.put("x", xCount);
        this.scores.put("o", oCount);
        this.bestMove.put("coord", parentCoord);
        this.bestMove.put("scores", scores);
        //System.out.println(depth + " " + play);
        //printBoard();
        getBestMove();

    }
    public static void main(String[] args) {
        //Max is 7
        OthelloGame o = new OthelloGame(4);//"default", "default", 0, true, null);
        o.playGame(100);
    }
    public static String stringifyBoard(String[][] board) {
        String str = "";
        for(int i = 7; i >= 0; i--) {
            str += Arrays.toString(board[i]).replace(", ", "").replace("[", "").replace("]", "");
        }
        return str;
    }
    public String[][] matrixCopy(String[][] a) {
        String[][] d = new String[8][8];
        for(int i = 0; i < 8; i++) {
            for(int k = 0; k < 8; k++) {
                d[i][k] = a[i][k];
            }
            //d[i] = a[i];
        }
        return d;
    }
    private HashSet<Coordinate> getPossiblePlays() {
        HashSet<Coordinate> plays = new HashSet<Coordinate>();
        for(int y = 0; y < 8; y++) {
            for(int x = 0; x < 8; x++) {
                if(board[y][x].equals(other)) {
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
        for(Coordinate c : NEIGHBORS) {
            int x1 = c.x + x;
            int y1 = c.y + y;
            if(0 <= x1 && x1 < 8 && 0 <= y1 && y1 < 8 && board[y1][x1].equals("-")) {
                neighbors.add(new Coordinate(x1, y1));
            }

        }
        return neighbors;
    }
    public void printBoard() {
        for(int i = 0; i < 8; i++) {
            System.out.println(Arrays.toString(board[i]).replace(", ", " ").replace("[", "").replace("]", ""));
        }
        System.out.println("\n");
    }
    public boolean isValidPlay(Coordinate coord) {
        int x = coord.x;
        int y = coord.y;
        boolean[] otherFound = {false, false, false, false, false, false, false, false};
        //Right
        if(x < 7 && board[y][x + 1].equals(other)) {
            for(int x1 = x + 1; x1 < 8; x1++) {
                if(board[y][x1].equals(other)) {
                    otherFound[0] = true;
                } else if(board[y][x1].equals(play) && otherFound[0]) {
                    return true;
                } else break;
            }
        }

        //Left
        if(x > 0 && board[y][x - 1].equals(other)) {
            for(int x1 = x - 1; x1 >= 0; x1--) {
                if(board[y][x1].equals(other)) {
                    otherFound[1] = true;
                } else if(board[y][x1].equals(play) && otherFound[1]) {
                    return true;
                } else break;
            }
        }

        //Up
        if(y > 0 && board[y - 1][x].equals(other)) {
            for(int y1 = y - 1; y1 >= 0; y1--) {
                if(board[y1][x].equals(other)) {
                    otherFound[2] = true;
                } else if(board[y1][x].equals(play) && otherFound[2]) {
                    return true;
                } else break;
            }
        }

        //Down
        if(y < 7 && board[y + 1][x].equals(other)) {
            for(int y1 = y + 1; y1 < 8; y1++) {
                if(board[y1][x].equals(other)) {
                    otherFound[3] = true;
                } else if(board[y1][x].equals(play) && otherFound[3]) {
                    return true;
                } else break;
            }
        }
        //Y = x right
        //new PythonRange(1, 8 - Math.max(x, y) - 1)
        for(int inc : new PythonRange(1, Math.min(8 - x, y))){

            if (board[y - inc][x + inc].equals(other)) {
                otherFound[4] = true;
            } else if (board[y - inc][x + inc].equals(play) && otherFound[4]) {
                return true;
            } else break;
        }


        //Y = x left
        for(int inc : new PythonRange(Math.min(x, y))) {//(inc in xrange(0, Math.min(x, y))) {
            if(board[y + inc][x - inc].equals(other)) {
                otherFound[5] = true;
            } else if(board[y + inc][x - inc].equals(play) && otherFound[5]) {
                return true;
            } else break;
        }
        //Y = -x left
        for(int inc : new PythonRange(Math.min(x, y))) {
            if(board[y - inc][x - inc].equals(other)) {
                otherFound[6] = true;
            } else if(board[y - inc][x - inc].equals(play) && otherFound[6]) {
                return true;
            } else break;
        }
        //Y = -x right
        for(int inc : new PythonRange(1, 8 - Math.max(x, y) - 1)){
            if(board[y + inc][x + inc].equals(other)) {
                otherFound[7] = true;
            } else if(board[y + inc][x + inc].equals(play) && otherFound[7]) {
                return true;
            } else break;
        }
        return false;
    }
    public void getBestMove() {
        children = new ArrayList<OthelloGame>();
        if(depth > 0) {
            for(Coordinate coord : getPossiblePlays()) {
                playMove(coord);
                children.add(new OthelloGame(stringifyBoard(board), other, depth - 1, false, coord));
                board = matrixCopy(origBoard);
            }
            if(depth > 1) {
                if(children.size() > 0) {
                    OthelloGame bm = Collections.max(children, myComparatorB.INSTANCE);
                    bestMove.put("coord", bm.parentCoord);
                    bestMove.put("scores", bm.bestMove.get("scores"));
                }
            } else {
                if(children.size() > 0) {
                    OthelloGame bm = Collections.max(children, myComparatorA.INSTANCE);
                    bestMove.put("coord", bm.parentCoord);
                    bestMove.put("scores", bm.bestMove.get("scores"));
                }
            }
        }
    }
    public void playMove(){
        playMove((Coordinate) bestMove.get("coord"));
    }
    public void playMove(Coordinate coord) {
        int x = coord.x;
        int y = coord.y;
        board[y][x] = play;;
        boolean[] otherFound = {false, false, false, false, false, false, false, false};
        boolean[] terminatorFound = {false, false, false, false, false, false, false, false};
        //Check ifchange
        //Right
        if(x < 7 && board[y][x + 1].equals(other)) {
            for(int x1 = x + 1; x1 < 8; x1++) {
                if(board[y][x1].equals(other)) {
                    otherFound[0] = true;
                } else if(board[y][x1].equals(play) && otherFound[0]) {
                    terminatorFound[0] = true;
                    break;
                } else break;
            }
        }
        //Left
        if(x > 0 && board[y][x - 1].equals(other)) {
            for(int x1 = x - 1; x1 >= 0; x1--) {
                if(board[y][x1].equals(other)) {
                    otherFound[1] = true;
                } else if(board[y][x1].equals(play) && otherFound[1]) {
                    terminatorFound[1] = true;
                    break;
                } else break;
            }
        }
        //Up
        if(y > 0 && board[y - 1][x].equals(other)) {
            for(int y1 = y - 1; y1 >= 0; y1--) {
                if(board[y1][x].equals(other)) {
                    otherFound[2] = true;
                } else if(board[y1][x].equals(play) && otherFound[2]) {
                    terminatorFound[2] = true;
                    break;
                } else break;
            }
        }
        //Down
        if(y < 7 && board[y + 1][x].equals(other)) {
            for(int y1 = y + 1; y1 < 8; y1++) {
                if(board[y1][x].equals(other)) {
                    otherFound[3] = true;
                } else if(board[y1][x].equals(play) && otherFound[3]) {
                    terminatorFound[3] = true;
                    break;
                } else break;
            }
        }
        //Y = x right
        if(y > 0 && x < 7) {
            for (int inc : new PythonRange(1, Math.min(8 - x, y))) {//(inc in xrange(1, 8 - Math.max(x, y) - 1)) {
                if (board[y - inc][x + inc].equals(other)) {
                    otherFound[4] = true;
                } else if (board[y - inc][x + inc].equals(play) && otherFound[4]) {
                    terminatorFound[4] = true;
                    break;
                } else break;
            }
        }
        //Y = x left
        for(int inc : new PythonRange(Math.min(x, y))) {//(inc in xrange(0, Math.min(x, y))) {
            if(board[y + inc][x - inc].equals(other)) {
                otherFound[5] = true;
            } else if(board[y + inc][x - inc].equals(play) && otherFound[5]) {
                terminatorFound[5] = true;
                break;
            } else break;
        }
        //Y = -x left
        for(int inc : new PythonRange(Math.min(x, y))) {
            if(board[y - inc][x - inc].equals(other)) {
                otherFound[6] = true;
            } else if(board[y - inc][x - inc].equals(play) && otherFound[6]) {
                terminatorFound[6] = true;
                break;
            } else break;
        }
        //Y = -x right
        for(int inc : new PythonRange(1, 8 - Math.max(x, y) - 1)) {
            if(board[y + inc][x + inc].equals(other)) {
                otherFound[7] = true;
            } else if(board[y + inc][x + inc].equals(play) && otherFound[7]) {
                terminatorFound[7] = true;
                break;
            } else break;
        }

        //Flip
        //power of regexp
        //(if s)(.*)(:)\n(.*)play(\n.*elif)(.*):(\n.*)break
        //if\(s$2\){\n$4play\n}$5\($6\){$7\nbreak;\n}\n}\n}
        //Right
        if(terminatorFound[0]) {
            for(int x1 = x + 1; x1 < 8; x1++) {
                if(board[y][x1].equals(other)) {
                    board[y][x1] = play;
                } else if(board[y][x1].equals(play)) {
                    break;
                }
            }
        }
        //Left
        if(terminatorFound[1]) {
            for(int x1 = x - 1; x1 >= 0; x1--) {
                if(board[y][x1].equals(other)) {
                    board[y][x1] = play;
                } else if(board[y][x1].equals(play)) {
                    break;
                }
            }
        }
        //Up
        if(terminatorFound[2]) {
            for(int y1 = y - 1; y1 >= 0; y1--) {
                if(board[y1][x].equals(other)) {
                    board[y1][x] = play;
                } else if(board[y1][x].equals(play)) {
                    break;
                }
            }
        }
        //Down
        if(terminatorFound[3]) {
            for(int y1 = y + 1; y1 < 8; y1++) {
                if(board[y1][x].equals(other)) {
                    board[y1][x] = play;
                } else if(board[y1][x].equals(play)) {
                    break;
                }
            }
        }
        //Y = x right
        if(terminatorFound[4]) {
            for(int inc : new PythonRange(1, Math.min(8 - x, y))) {
                if(board[y - inc][x + inc].equals(other)) {
                    board[y - inc][x + inc] = play;
                } else if(board[y - inc][x + inc].equals(play)) {
                    break;
                }
            }
        }
        //Y = x left
        if(terminatorFound[5]) {
            for(int inc : new PythonRange(Math.min(x, y))) {
                if(board[y + inc][x - inc].equals(other)) {
                    board[y + inc][x - inc] = play;
                } else if(board[y + inc][x - inc].equals(play)) {
                    break;
                }
            }
        }
        //Y = x left
        if(terminatorFound[6]) {
            for(int inc : new PythonRange(Math.min(x, y))) {
                if(board[y - inc][x - inc].equals(other)) {
                    board[y - inc][x - inc] = play;
                } else if(board[y - inc][x - inc].equals(play)) {
                    break;
                }
            }
        }
        //Y = x right
        if(terminatorFound[7]) {
            for(int inc : new PythonRange(1, Math.min(8 - x, y))) {
                if(board[y + inc][x + inc].equals(other)) {
                    board[y + inc][x + inc] = play;
                } else if(board[y + inc][x + inc].equals(play)) {
                    break;
                }
            }
        }

    }
    enum myComparatorA implements Comparator<OthelloGame> {
        INSTANCE;

        public int compare(OthelloGame a, OthelloGame b) {
            return a.scores.get(a.other) - b.scores.get(b.other);
        }
    }
    enum myComparatorB implements Comparator<OthelloGame> {
        //best move
        INSTANCE;

        public int compare(OthelloGame a, OthelloGame b) {
            return ((HashMap<String, Integer>) a.bestMove.get("scores")).get(a.other) - ((HashMap<String, Integer>) b.bestMove.get("scores")).get(b.other);
        }
    }
    public void playGame(int length){
        printBoard();
        for(int i = 0; i < length; i++){
            System.out.println(bestMove.get("coord") + " " + play);
            playMove((Coordinate) bestMove.get("coord"));
            origBoard = matrixCopy(board);
            printBoard();
            String temp = play;
            play = other;
            other = temp;
            getBestMove();
            if(stringifyBoard(board).replace("-", "").length() == 64){
                break;
            }
        }
    }
    public String[][] getBoard(){
        return board;
    }
}

