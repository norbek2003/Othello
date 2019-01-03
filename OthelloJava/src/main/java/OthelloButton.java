import java.util.*;
import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;

public class OthelloButton extends JButton implements ActionListener{
    public Coordinate coord;
    public boolean pressed;
    public Othello parent;

    private HashMap<String, Color> colors = new HashMap<String, Color>(){{
        put("x", Color.BLACK);
        put("o", Color.WHITE);
        put("-", new Color(50, 100, 10));
    }};
    private String owner = "-";
    public JLabel possibleValues = new JLabel("", JLabel.CENTER);
    public Graphics g;
    public OthelloButton(Coordinate coord, Othello parent){
        super("");//coord.toString());
        Dimension size = getPreferredSize();
        size.width = size.height = Math.max(size.width,size.height);
        setPreferredSize(size);
        //setBackground(Color.WHITE);
        setContentAreaFilled(false);
        this.coord = coord;
        this.parent = parent;
        setBorders();

        addActionListener(this);
        setFont(new Font("Arial", Font.PLAIN, 30));
        //setBackground(Color.cyan);
        possibleValues.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        add(possibleValues);
        //setBorder(new RoundedBorder(10));
        this.addKeyListener(new KeyAdapter (){
            public void keyPressed(KeyEvent e) {
                if(pressed){

                }
            }
        });
        //setColor(Color.WHITE);
    }
    /**Deals with button press. */
    public void actionPerformed(ActionEvent e) {
	if(owner == "-"){
	    pressed = !pressed;
	    parent.press(this);
	}
    }
    /** Creates the borders based on position. */
    public void setBorders(){
        int bottom = 1;
        int left = 1;
        int right = 1;
        int top = 1;

        setBorder(BorderFactory.createMatteBorder(top, left, bottom, right, Color.black));
    }
    /** Marks it as a permanent unclickable value. */

    protected void paintComponent(Graphics g) {
        g.setColor(colors.get(owner));
        g.fillOval(0, 0, getSize().width-1,getSize().height-1);

        super.paintComponent(g);
    }
    protected void paintBorder(Graphics g) {
        this.g = g;
        g.setColor(colors.get(owner));
        g.drawOval(0, 0, getSize().width-1, getSize().height-1);
    }
    Shape shape;
    public boolean contains(int x, int y) {
        if (shape == null ||
                !shape.getBounds().equals(getBounds())) {
            shape = new Ellipse2D.Float(0, 0, getWidth(), getHeight());
        }
        return shape.contains(x, y);
    }
    public void flipToColor(String c){
        if(!c.equals("-")) {
            owner = c;
            repaint();
            //paintComponent();
        }
    }
    public String getOwner(){
        return owner;
    }
}
