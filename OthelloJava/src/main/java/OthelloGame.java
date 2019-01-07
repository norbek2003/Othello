import java.util.*;

public class OthelloGame {
    private static final String DEFAULT_BOARD = "---------------------------xo------ox---------------------------";
    public static final String DEFAULT_PLAY = "x";
    public static final String MOVES = "xo";
    public static final ArrayList<Coordinate> NEIGHBORS = new ArrayList<Coordinate>(Arrays.asList(new Coordinate(-1, -1), new Coordinate(-1, 0), new Coordinate(-1, 1), new Coordinate(0, -1), new Coordinate(0, 1), new Coordinate(1, -1), new Coordinate(1, 0), new Coordinate(1, 1)));
    public String play, other;
    public HashMap<String, Integer> scores = new HashMap<String, Integer>();
    public HashMap<String, Object> bestMove = new HashMap<String, Object>();
    public Coordinate parentCoord;
    private String[][] board = new String[8][8];
    private String[][] origBoard = new String[8][8];
    private int depth;
    private boolean orig;
    private ArrayList<OthelloGame> children = new ArrayList<OthelloGame>();

    public OthelloGame(int depth) {
        this(DEFAULT_BOARD, DEFAULT_PLAY, depth, true, null);
    }

    public OthelloGame(String board, String play, int depth, boolean orig, Coordinate parentCoord) {
        for (int j = 0; j < 8; j++) {
            int i = 7 - j;// 7 - j;
            String[] temp = board.substring(i * 8, (i + 1) * 8).split("");
            for (int k = 0; k < 8; k++) {
                this.board[j][k] = temp[k];
                this.origBoard[j][k] = temp[k];
            }

        }
        this.depth = depth;
        //    this.depth *= 2;
        this.play = play;
        this.other = MOVES.replace(play, "");
        this.orig = orig;
        this.parentCoord = parentCoord;
        int xCount = 64 - board.replace("x", "").length();
        int oCount = 64 - board.replace("o", "").length();
        this.scores.put("x", xCount);
        this.scores.put("o", oCount);
        this.bestMove.put("coord", parentCoord);
        this.bestMove.put("scores", scores);
        //System.out.println(depth + " " + play);
        //printBoard();
            getBestMove();


    }
    public void printTree(){
        System.out.println(depth);
        printBoard();
        for(OthelloGame g : children){
            g.printTree();
        }
    }
    public static void main(String[] args) {
        //Max is 7
        OthelloGame o = new OthelloGame(Integer.valueOf(args[0]));
        //o.printBoard();
        //o.printTree();
        //OthelloGame o = new OthelloGame(4);//"default", "default", 0, true, null);
        //o.playGame(100);
    }

    public static String stringifyBoard(String[][] board) {
        StringBuilder str = new StringBuilder();
        for (int i = 7; i >= 0; i--) {
            str.append(Arrays.toString(board[i]).replace(", ", "").replace("[", "").replace("]", ""));
        }
        return str.toString();
    }

    public String[][] matrixCopy(String[][] a) {
        String[][] d = new String[8][8];
        for (int i = 0; i < 8; i++) {
            System.arraycopy(a[i], 0, d[i], 0, 8);
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
        for (Coordinate c : NEIGHBORS) {
            int x1 = c.x + x;
            int y1 = c.y + y;
            if (0 <= x1 && x1 < 8 && 0 <= y1 && y1 < 8 && board[y1][x1].equals("-")) {
                neighbors.add(new Coordinate(x1, y1));
            }

        }
        return neighbors;
    }

    public void printBoard() {
        for (int i = 0; i < 8; i++) {
            System.out.println(Arrays.toString(board[i]).replace(", ", " ").replace("[", "").replace("]", ""));
        }
        System.out.println("\n");
    }
    public boolean isValidPlay(Coordinate coord) {
        int x = coord.x;
        int y = coord.y;
        boolean[] otherFound = {false, false, false, false, false, false, false, false};
        //Right
        if (x < 7 && board[y][x + 1].equals(other)) {
            for (int x1 = x + 1; x1 < 8; x1++) {
                if (board[y][x1].equals(other)) {
                    otherFound[0] = true;
                } else if (board[y][x1].equals(play) && otherFound[0]) {
                    return true;
                } else break;
            }
        }
        //Left
        if (x > 0 && board[y][x - 1].equals(other)) {
            for (int x1 = x - 1; x1 >= 0; x1--) {
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
            for (int y1 = y + 1; y1 < 8; y1++) {
                if (board[y1][x].equals(other)) {
                    otherFound[3] = true;
                } else if (board[y1][x].equals(play) && otherFound[3]) {
                    return true;
                } else break;
            }
        }
        //Y = x right
        //new PythonRange(1, 8 - Math.max(x, y) - 1)
        for (int inc : new PythonRange(1, Math.min(8 - x, y))) {

            if (board[y - inc][x + inc].equals(other)) {
                otherFound[4] = true;
            } else if (board[y - inc][x + inc].equals(play) && otherFound[4]) {
                return true;
            } else break;
        }
        //Y = x left
        for (int inc : new PythonRange(1, Math.min(x, 8 - y))) {//(inc in xrange(0, Math.min(x, y))) {
            if (board[y + inc][x - inc].equals(other)) {
                otherFound[5] = true;
            } else if (board[y + inc][x - inc].equals(play) && otherFound[5]) {
                return true;
            } else break;
        }
        //Y = -x left
        for (int inc : new PythonRange(1, Math.min(x, y))) {
            if (board[y - inc][x - inc].equals(other)) {
                otherFound[6] = true;
            } else if (board[y - inc][x - inc].equals(play) && otherFound[6]) {
                return true;
            } else break;
        }
        //Y = -x right
        for (int inc : new PythonRange(1, Math.min(8 - x, 8 - y))) {
            if (board[y + inc][x + inc].equals(other)) {
                otherFound[7] = true;
            } else if (board[y + inc][x + inc].equals(play) && otherFound[7]) {
                return true;
            } else break;
        }
        return false;
    }

    public void getBestMove() {
            children = new ArrayList<OthelloGame>();
            if (depth > 0) {
                for (Coordinate coord : getPossiblePlays()) {
                    playMove(coord);
                    children.add(new OthelloGame(stringifyBoard(board), other, depth - 1, false, coord));
                    //children.add(new OthelloGame(stringifyBoard(board), other, depth - 1, false, coord));
                    board = matrixCopy(origBoard);
                }
                //latch.await();
                OthelloGame bm;
                if (depth > 1) {
                    bm = Collections.max(children, myComparatorB.INSTANCE);
                } else {
                    bm = Collections.max(children, myComparatorA.INSTANCE);
                }
                bestMove.put("coord", bm.parentCoord);
                bestMove.put("scores", bm.bestMove.get("scores"));
            }

    }

    public void playMove() {
        playMove((Coordinate) bestMove.get("coord"));
        origBoard = matrixCopy(board);
        board = matrixCopy(origBoard);
    }

    public void playMove(Coordinate coord, boolean player) {
        int x = coord.x;
        int y = coord.y;
        board[y][x] = play;
        boolean[] otherFound = {false, false, false, false, false, false, false, false};
        HashSet<Coordinate> values = new HashSet<Coordinate>();
        HashSet<Coordinate> temp = new HashSet<Coordinate>();
        //Check ifchange
        //Right
        if (x < 7 && board[y][x + 1].equals(other)) {
            temp = new HashSet<Coordinate>();
            for (int x1 = x + 1; x1 < 8; x1++) {
                temp.add(new Coordinate(x1, y));
                if (board[y][x1].equals(other)) {
                    otherFound[0] = true;
                } else if (board[y][x1].equals(play) && otherFound[0]) {
                    values.addAll(temp);

                    break;
                } else break;
            }
        }
        //Left
        if (x > 0 && board[y][x - 1].equals(other)) {
            temp = new HashSet<Coordinate>();
            for (int x1 = x - 1; x1 >= 0; x1--) {
                temp.add(new Coordinate(x1, y));
                if (board[y][x1].equals(other)) {
                    otherFound[1] = true;
                } else if (board[y][x1].equals(play) && otherFound[1]) {
                    
                    values.addAll(temp);

                    break;
                } else break;
            }
        }
        //Up
        if (y > 0 && board[y - 1][x].equals(other)) {
            temp = new HashSet<Coordinate>();
            for (int y1 = y - 1; y1 >= 0; y1--) {
                temp.add(new Coordinate(x, y1));
                if (board[y1][x].equals(other)) {
                    otherFound[2] = true;
                } else if (board[y1][x].equals(play) && otherFound[2]) {
                    
                    values.addAll(temp);

                    break;
                } else break;
            }
        }
        //Down
        if (y < 7 && board[y + 1][x].equals(other)) {
            temp = new HashSet<Coordinate>();
            for (int y1 = y + 1; y1 < 8; y1++) {
                temp.add(new Coordinate(x, y1));
                if (board[y1][x].equals(other)) {
                    otherFound[3] = true;
                } else if (board[y1][x].equals(play) && otherFound[3]) {
                    
                    values.addAll(temp);

                    break;
                } else break;
            }
        }
        //Y = x right
        temp = new HashSet<Coordinate>();
        for (int inc : new PythonRange(1, Math.min(8 - x, y))) {//(inc in xrange(1, 8 - Math.max(x, y) - 1)) {
            temp.add(new Coordinate(x + inc, y - inc));
            if (board[y - inc][x + inc].equals(other)) {
                otherFound[4] = true;
            } else if (board[y - inc][x + inc].equals(play) && otherFound[4]) {
                
                values.addAll(temp);

                break;
            } else break;
        }
        //Y = x left
        temp = new HashSet<Coordinate>();
        for (int inc : new PythonRange(1, Math.min(x, 8 - y))) {//(inc in xrange(0, Math.min(x, y))) {
            temp.add(new Coordinate(x - inc, y + inc));
            if (board[y + inc][x - inc].equals(other)) {
                otherFound[5] = true;
            } else if (board[y + inc][x - inc].equals(play) && otherFound[5]) {
                
                values.addAll(temp);

                break;
            } else break;
        }
        //Y = -x left
        temp = new HashSet<Coordinate>();
        for (int inc : new PythonRange(1, Math.min(x, y))) {
            temp.add(new Coordinate(x - inc, y - inc));
            if (board[y - inc][x - inc].equals(other)) {
                otherFound[6] = true;
            } else if (board[y - inc][x - inc].equals(play) && otherFound[6]) {
                
                values.addAll(temp);

                break;
            } else break;
        }
        //Y = -x right
        temp = new HashSet<Coordinate>();
        for (int inc : new PythonRange(1, Math.min(8 - x, 8 - y))) {
            temp.add(new Coordinate(x + inc, y + inc));
            if (board[y + inc][x + inc].equals(other)) {
                otherFound[7] = true;
            } else if (board[y + inc][x + inc].equals(play) && otherFound[7]) {
                
                values.addAll(temp);

                break;
            } else break;
        }
        for(Coordinate c : values){
            board[c.y][c.x] = play;
        }
        //System.out.println(Arrays.toString(
        if (player) {
            origBoard = matrixCopy(board);
        }
    }

    public void playMove(Coordinate coord) {
        playMove(coord, false);
    }

    public void playGame(int length) {
        printBoard();
        for (int i = 0; i < length; i++) {
            System.out.println(bestMove.get("coord") + " " + play);
            playMove((Coordinate) bestMove.get("coord"));
            origBoard = matrixCopy(board);
            printBoard();
            String temp = play;
            play = other;
            other = temp;
            getBestMove();
            if (stringifyBoard(board).replace("-", "").length() == 64) {
                break;
            }
        }
    }

    public int getScore(String side){
        return 64 - stringifyBoard(board).replace(side, "").length();
    }
    public String[][] getBoard() {
        return board;
    }

    public String[][] getOrigBoard() {
        return origBoard;
    }

    public boolean isMovePossible() {
        return (getPossiblePlays().size() != 0);
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

        @SuppressWarnings("unchecked")
        public int compare(OthelloGame a, OthelloGame b) {
            int aScore = ((HashMap<String, Integer>) a.bestMove.get("scores")).get(a.other) - ((HashMap<String, Integer>) a.bestMove.get("scores")).get(a.play);
            int bScore = ((HashMap<String, Integer>) b.bestMove.get("scores")).get(b.other) - ((HashMap<String, Integer>) b.bestMove.get("scores")).get(b.play);
            return aScore - bScore;
        }
    }
}

