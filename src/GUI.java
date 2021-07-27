import javax.swing.*;
import java.awt.*;

class Field extends JLabel {
    private byte sides;
    private boolean way;

    Field(byte sides, boolean way) {
        super();
        this.sides = sides;
        this.way = way;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.setColor(Color.BLACK);

        char[] s = Integer.toBinaryString(sides).toCharArray();

        if (s.length < 1 || s[s.length-1] == '0') {
            g.drawLine(0, 0, 15, 0);
        }
        if (s.length < 2 || s[s.length-2] == '0') {
            g.drawLine(15, 0, 15,15);
        }
        if (s.length < 3 || s[s.length-3] == '0') {
            g.drawLine(0, 15, 15, 15);
        }
        if (s.length < 4 || s[s.length-4] == '0') {
            g.drawLine(0, 0, 0, 15);
        }
        if (s.length >=5 && s[s.length-5] == '1' && !way) {
            g.setColor(Color.RED);
            g.fillRect(1, 1, 14, 14);
        }
        if (way || s.length >=6 && s[s.length-6] == '1') {
            g.setColor(Color.GREEN);
            g.fillRect(1, 1, 14, 14);
        }
    }
}


class GUI extends JFrame {
    private Maze maze;
    private Field[] cells;
    private byte[] sizes;

    GUI(Maze maze) {
        super("Maze");
        this.maze = maze;
        cells = new Field[maze.getCols() * maze.getRows()];
        sizes = maze.getMaze();
        createGUI();
    }

    private void createGUI() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JPanel main = new JPanel();
        main.setLayout(new GridLayout(maze.getRows(), maze.getCols()));
        //main.setBorder(new EtchedBorder());

        for (int i = 0; i < cells.length; i++) {
            cells[i] = new Field(sizes[i], maze.getWay()[i]);
            main.add(cells[i]);
            cells[i].setPreferredSize(new Dimension(16, 16));
            cells[i].setToolTipText(String.valueOf(i+1));
            //cells[i].setBackground(new Color(new Random().nextInt(256), new Random().nextInt(256), new Random().nextInt(256)));
        }

        setContentPane(main);
        pack();
        setVisible(true);
        setLocationRelativeTo(null);
        setResizable(false);
    }
}