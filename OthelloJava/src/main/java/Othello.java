import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
public class Othello{
    private JFrame frame = new JFrame();
    private JSplitPane splitPane;
    //Deals with the grid
    private JPanel gridPanel = new JPanel();
    private JPanel gridContainer = new JPanel();
    private boolean pressable = false;
    private OthelloButton[][] grid = new OthelloButton[8][8];
    //Control Panel
    private JPanel controlSuper = new JPanel();
    private JPanel control = new JPanel();
    private JLabel xScore = scoreDisplayer("x");
    private JLabel oScore = scoreDisplayer("o");

    //Othello Games
    private OthelloGame game;
    public Othello(){
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        game = new OthelloGame(4);
        setupGrid();
        //control.setLayout()//new BoxLayout(control, BoxLayout.PAGE_AXIS));
        control.setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.NORTH;
        c.insets = new Insets(10, 5, 0, 5);
        c.ipadx = 10;
        c.ipady = 5;
        c.gridx = 0;
        c.gridy = 0;
        //control.add(generatorContainer, c);
        c.gridy ++;
        controlSuper.add(control);
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, controlSuper, gridContainer);
        frame.getContentPane().add(splitPane);
        frame.pack();
        frame.setVisible(true);
    }
    public static void main(String[] args){
        Othello s = new Othello();
    }
    /** Deals with the pressing of the buttons on the Othello grid. */
    public void press(OthelloButton b){
        if(game.isValidPlay(b.coord)) {
            System.out.println("Pressed");
            game.playMove(b.coord, true);
            game.printBoard();
            game.printOrigBoard();
            updateBoard();
            System.out.println("Updated");
            game.play = "o";
            game.other = "x";
            game.getBestMove();
            javax.swing.Timer timer = new javax.swing.Timer(3000, new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent arg0) {
                    System.out.println("Last");
                    game.printBoard();
                    System.out.println("Thought of retaliation");
                    game.playMove();
                    System.out.println("Retaliated");
                    game.printBoard();
                    updateBoard();
                    System.out.println("Updated");
                    game.play = "x";
                    game.other = "o";
                }
            });
            timer.setRepeats(false);
            timer.start();


        }

    }

    /** Sets up the Othello grid.*/
    private void setupGrid(){
        gridPanel.setLayout(new GridLayout(8, 8, 10, 10));
        gridPanel.setBackground(new Color(30, 60, 0));
        for(int y = 0; y < 8; y++){
            for(int x = 0; x < 8; x ++){
                OthelloButton b = new OthelloButton(new Coordinate(x, y),this);
                grid[y][x] = b;
                gridPanel.add(b);
            }
        }
        updateBoard();
        gridPanel.setBorder(BorderFactory.createMatteBorder(5, 5, 5, 5, Color.blue));
        gridPanel.setPreferredSize(new Dimension(500, 500));
        gridContainer.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 1;
        c.weighty = 1;
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.CENTER;
        gridContainer.add(gridPanel, c);
        c.gridy = 0;
        c.weightx = 2;

        Box box = Box.createHorizontalBox();
        c.anchor = GridBagConstraints.NORTH;
        box.add(xScore);
        box.add(oScore);
        gridContainer.add(box, c);
        /*
        c.anchor = GridBagConstraints.NORTHEAST;
        gridContainer.add(oScore, c);
        c.anchor = GridBagConstraints.NORTHWEST;
        gridContainer.add(xScore, c);
        */
        gridContainer.setPreferredSize(new Dimension(700, 700));
    }
    private void updateBoard(){
        String[][] board = game.getBoard();
        for(int y = 0; y < 8; y++){
            for(int x = 0; x < 8; x++){
                if(!board[y][x].equals(grid[y][x].getOwner())) {
                    System.out.println(x + " " + y);
                    //Checking for change saves a lot of time repaint GUI;
                    grid[y][x].flipToColor(board[y][x]);
                }
            }
        }
        oScore.setText(" " + game.getScore("o") + " ");
        xScore.setText(" " + game.getScore("x") + " ");
    }
    private JLabel scoreDisplayer(String side){
        JLabel label = new JLabel(" 2 "){
            @Override
            public Dimension getMaximumSize() {
                Dimension d = super.getMaximumSize();
                d.width = Integer.MAX_VALUE;
                return d;
            }
        };
        label.setFont(new Font("Arial", Font.BOLD, 30));
        label.setBackground(Color.BLACK);
        label.setForeground(Color.WHITE);
        if(side.equals("x")){
            label.setBackground(Color.WHITE);
            label.setForeground(Color.BLACK);
        }
        label.setOpaque(true);

        return label;
    }
}




