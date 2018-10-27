/*
 * The MIT License
 *
 * Copyright 2018 Devon Crawford.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package eu.devoncrawford.pathfindingvisualization;

import eu.devoncrawford.pathfindingvisualization.ui.ControlHandler;
import eu.devoncrawford.pathfindingvisualization.entity.Node;
import eu.devoncrawford.pathfindingvisualization.ui.*;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

/**
 * The main graphics class for APathfinding. Controls the window, and all path
 * finding node graphics. Need to work on zoom function, currently only zooms to
 * top left corner rather than towards mouse
 *
 * @author Devon Crawford
 */
public class Frame extends JPanel implements ActionListener, MouseListener, MouseMotionListener, MouseWheelListener, KeyListener {

    private static final long serialVersionUID = 1L;
    private final ControlHandler ch;
    private final JFrame window;
    private final APathfinding pathfinding;
    private boolean showSteps, btnHover;
    private int size;
    private final double a1;
    private final double a2;
    private char currentKey = (char) 0;
    private Node startNode, endNode;
    private String mode;

    private final Timer timer = new Timer(100, this);
    private int r = randomWithRange(0, 255);
    private int G = randomWithRange(0, 255);
    private int b = randomWithRange(0, 255);

    public Frame() {
        ch = new ControlHandler(this);
        size = 25;
        mode = "Map Creation";
        showSteps = true;
        btnHover = false;
        setLayout(null);
        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
        addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);

        // Set up pathfinding
        pathfinding = new APathfinding(this, size);
        pathfinding.setDiagonal(true);

        // Calculating value of a in speed function 1
        a1 = (5000.0000 / (Math.pow(25.0000 / 5000, 1 / 49)));
        a2 = 625.0000;

        // Set up window
        window = new JFrame();
        window.setContentPane(this);
        window.setTitle("A* Pathfinding Visualization");
        window.getContentPane().setPreferredSize(new Dimension(700, 600));
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);

        // Add all controls
        ch.addAll();

        this.revalidate();
        this.repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Grab dimensions of panel
        int height = getHeight();
        int width = getWidth();

        // If no path is found
        if (pathfinding.isNoPath()) {
            // Set timer for animation
            timer.setDelay(50);
            timer.start();

            // Set text of "run" button to "clear"
            ch.getB("run").setText("clear");

            // Set mode to "No Path"
            mode = "No Path";

            // Set up flicker animation
            Color flicker = new Color(r, G, b);
            g.setColor(flicker);
            g.fillRect(0, 0, getWidth(), getHeight());

            // Place "No Path" text on screen in center
            ch.noPathTBounds();
            ch.getL("noPathT").setVisible(true);
            this.add(ch.getL("noPathT"));
            this.revalidate();
        }

        // If pathfinding is complete (found path)
        if (pathfinding.isComplete()) {
            // Set run button to clear
            ch.getB("run").setText("clear");

            // Set timer delay, start for background animation
            timer.setDelay(50);
            timer.start();

            // Make the background flicker
            Color flicker = new Color(r, G, b);
            g.setColor(flicker);
            g.fillRect(0, 0, getWidth(), getHeight());

            // Set completed mode
            if (showSteps) {
                mode = "Completed";
            } else {
                mode = "Completed in " + pathfinding.getRunTime() + "ms";
            }
        }

        // Draws grid
        g.setColor(Color.lightGray);
        for (int j = 0; j < this.getHeight(); j += size) {
            for (int i = 0; i < this.getWidth(); i += size) {
                g.drawRect(i, j, size, size);
            }
        }

        // Draws all borders
        g.setColor(Color.black);
        for (int i = 0; i < pathfinding.getBorderList().size(); i++) {
            g.fillRect(pathfinding.getBorderList().get(i).getX() + 1, pathfinding.getBorderList().get(i).getY() + 1,
                    size - 1, size - 1);
        }

        // Draws all open Nodes (path finding nodes)
        for (int i = 0; i < pathfinding.getOpenList().size(); i++) {
            Node current = pathfinding.getOpenList().get(i);
            g.setColor(Style.greenHighlight);
            g.fillRect(current.getX() + 1, current.getY() + 1, size - 1, size - 1);

            drawInfo(current, g);
        }

        // Draws all closed nodes
        for (int i = 0; i < pathfinding.getClosedList().size(); i++) {
            Node current = pathfinding.getClosedList().get(i);

            g.setColor(Style.redHighlight);
            g.fillRect(current.getX() + 1, current.getY() + 1, size - 1, size - 1);

            drawInfo(current, g);
        }

        // Draw all final path nodes
        pathfinding.getPathList().stream().forEach((current) -> {
            g.setColor(Style.blueHighlight);
            g.fillRect(current.getX() + 1, current.getY() + 1, size - 1, size - 1);
            drawInfo(current, g);
        });

        // Draws start of path
        if (startNode != null) {
            g.setColor(Color.blue);
            g.fillRect(startNode.getX() + 1, startNode.getY() + 1, size - 1, size - 1);
        }
        // Draws end of path
        if (endNode != null) {
            g.setColor(Color.red);
            g.fillRect(endNode.getX() + 1, endNode.getY() + 1, size - 1, size - 1);
        }

        // If control panel is being hovered, change colours
        if (btnHover) {
            g.setColor(Style.darkText);
            ch.hoverColour();
        } else {
            g.setColor(Style.btnPanel);
            ch.nonHoverColour();
        }
        // Drawing control panel rectangle
        g.fillRect(10, height - 96, 322, 90);

        // Setting mode text
        ch.getL("modeText").setText("Mode: " + mode);

        // Position all controls
        ch.position();

        // Setting numbers in pathfinding lists
        ch.getL("openC").setText(Integer.toString(pathfinding.getOpenList().size()));
        ch.getL("closedC").setText(Integer.toString(pathfinding.getClosedList().size()));
        ch.getL("pathC").setText(Integer.toString(pathfinding.getPathList().size()));

        // Setting speed number text in showSteps or !showSteps mode
        if (showSteps) {
            ch.getL("speedC").setText(Integer.toString(ch.getS("speed").getValue()));
        } else {
            ch.getL("speedC").setText("N/A");
        }

        // Getting values from checkboxes
        showSteps = ch.getC("showStepsCheck").isSelected();
        pathfinding.setDiagonal(ch.getC("diagonalCheck").isSelected());
        pathfinding.setTrig(ch.getC("trigCheck").isSelected());
    }

    // Draws info (f, g, h) on current node
    private void drawInfo(Node current, Graphics g) {
        if (size > 50) {
            g.setFont(Style.numbers);
            g.setColor(Color.black);
            g.drawString(Integer.toString(current.getF()), current.getX() + 4, current.getY() + 16);
            g.setFont(Style.smallNumbers);
            g.drawString(Integer.toString(current.getG()), current.getX() + 4, current.getY() + size - 7);
            g.drawString(Integer.toString(current.getH()), current.getX() + size - 26, current.getY() + size - 7);
        }
    }

    private void MapCalculations(MouseEvent e) {
        // If left mouse button is clicked
        if (SwingUtilities.isLeftMouseButton(e)) {
            // If 's' is pressed create start node
            switch (currentKey) {
                case 's': {
                    int xPosition = e.getX() - (e.getX() % size);
                    int yPosition = e.getY() - (e.getY() % size);
                    // Remove any pre-existing wall
                    pathfinding.removeBorder(new Node(xPosition, yPosition));
                    if (startNode == null) {
                        startNode = new Node(xPosition, yPosition);
                    } else {
                        startNode.setXY(xPosition, yPosition);
                    }
                    repaint();
                    break;
                } // If 'e' is pressed create end node
                case 'e': {
                    int xPosition = e.getX() - (e.getX() % size);
                    int yPosition = e.getY() - (e.getY() % size);
                    // Remove any pre-existing wall
                    pathfinding.removeBorder(new Node(xPosition, yPosition));
                    if (endNode == null) {
                        endNode = new Node(xPosition, yPosition);
                    } else {
                        endNode.setXY(xPosition, yPosition);
                    }
                    repaint();
                    break;
                } // Otherwise, create a wall
                default:
                    int xBorder = e.getX() - (e.getX() % size);
                    int yBorder = e.getY() - (e.getY() % size);
                    Node newBorder = new Node(xBorder, yBorder);
                    // A new wall is added only if the end doesn't exist yet OR if the new border
                    // doesn't overlap the end
                    if (pathfinding.getEnd() == null || !pathfinding.getEnd().equals(newBorder)) {
                        pathfinding.addBorder(newBorder);
                    }
                    repaint();
                    break;
            }
        } // If right mouse button is clicked
        else if (SwingUtilities.isRightMouseButton(e)) {
            int mouseBoxX = e.getX() - (e.getX() % size);
            int mouseBoxY = e.getY() - (e.getY() % size);

            // If 's' is pressed remove start node
            switch (currentKey) {
                // If 'e' is pressed remove end node
                case 's':
                    if (startNode != null && mouseBoxX == startNode.getX() && startNode.getY() == mouseBoxY) {
                        startNode = null;
                        repaint();
                    }
                    break;
                // Otherwise, remove wall
                case 'e':
                    if (endNode != null && mouseBoxX == endNode.getX() && endNode.getY() == mouseBoxY) {
                        endNode = null;
                        repaint();
                    }
                    break;
                default:
                    int Location = pathfinding.searchBorder(mouseBoxX, mouseBoxY);
                    if (Location != -1) {
                        pathfinding.removeBorder(Location);
                    }
                    repaint();
                    break;
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        MapCalculations(e);
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        MapCalculations(e);
    }

    @Override
    // Track mouse on movement
    public void mouseMoved(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        int height = this.getHeight();

        // Detects if mouse is within button panel
        btnHover = x >= 10 && x <= 332 && y >= (height - 96) && y <= (height - 6);
        repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        char key = e.getKeyChar();
        currentKey = key;

        // Start if space is pressed
        if (currentKey == KeyEvent.VK_SPACE) {
            ch.getB("run").setText("stop");
            start();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        currentKey = (char) 0;
    }

    // Starts path finding
    void start() {
        if (startNode != null && endNode != null) {
            if (!showSteps) {
                pathfinding.start(startNode, endNode);
            } else {
                pathfinding.setup(startNode, endNode);
                setSpeed();
                timer.start();
            }
        } else {
            System.out.println("ERROR: Needs start and end points to run.");
        }
    }

    @Override
    // Scales the map with mouse wheel scroll
    public void mouseWheelMoved(MouseWheelEvent m) {
        int rotation = m.getWheelRotation();
        double prevSize = size;
        int scroll = 3;

        // Changes size of grid based on scroll
        if (rotation == -1 && size + scroll < 200) {
            size += scroll;
        } else if (rotation == 1 && size - scroll > 2) {
            size += -scroll;
        }
        pathfinding.setSize(size);
        double ratio = size / prevSize;

        // new X and Y values for Start
        if (startNode != null) {
            int sX = (int) Math.round(startNode.getX() * ratio);
            int sY = (int) Math.round(startNode.getY() * ratio);
            startNode.setXY(sX, sY);
        }

        // new X and Y values for End
        if (endNode != null) {
            int eX = (int) Math.round(endNode.getX() * ratio);
            int eY = (int) Math.round(endNode.getY() * ratio);
            endNode.setXY(eX, eY);
        }

        // new X and Y values for borders
        for (int i = 0; i < pathfinding.getBorderList().size(); i++) {
            int newX = (int) Math.round((pathfinding.getBorderList().get(i).getX() * ratio));
            int newY = (int) Math.round((pathfinding.getBorderList().get(i).getY() * ratio));
            pathfinding.getBorderList().get(i).setXY(newX, newY);
        }

        // New X and Y for Open nodes
        for (int i = 0; i < pathfinding.getOpenList().size(); i++) {
            int newX = (int) Math.round((pathfinding.getOpenList().get(i).getX() * ratio));
            int newY = (int) Math.round((pathfinding.getOpenList().get(i).getY() * ratio));
            pathfinding.getOpenList().get(i).setXY(newX, newY);
        }

        // New X and Y for Closed Nodes
        for (int i = 0; i < pathfinding.getClosedList().size(); i++) {
            if (!pathfinding.getClosedList().get(i).equals(startNode)) {
                int newX = (int) Math.round((pathfinding.getClosedList().get(i).getX() * ratio));
                int newY = (int) Math.round((pathfinding.getClosedList().get(i).getY() * ratio));
                pathfinding.getClosedList().get(i).setXY(newX, newY);
            }
        }
        repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Moves one step ahead in path finding (called on timer)
        if (pathfinding.isRunning() && showSteps) {
            pathfinding.findPath(pathfinding.getPar());
            mode = "Running";
        }
        // Finish pathfinding background flicker!
        if (pathfinding.isComplete() || pathfinding.isNoPath()) {
            r = (int) (Math.random() * ((r + 15) - (r - 15)) + (r - 15));
            G = (int) (Math.random() * ((G + 15) - (G - 15)) + (G - 15));
            b = (int) (Math.random() * ((b + 15) - (b - 15)) + (b - 15));

            if (r >= 240 | r <= 15) {
                r = randomWithRange(0, 255);
            }
            if (G >= 240 | G <= 15) {
                G = randomWithRange(0, 255);
            }
            if (b >= 240 | b <= 15) {
                b = randomWithRange(0, 255);
            }
        }

        // Actions of run/stop/clear button
        if (e.getActionCommand() != null) {
            if (e.getActionCommand().equals("run") && !pathfinding.isRunning()) {
                ch.getB("run").setText("stop");
                start();
            } else if (e.getActionCommand().equals("clear")) {
                ch.getB("run").setText("run");
                mode = "Map Creation";
                ch.getL("noPathT").setVisible(false);
                pathfinding.reset();
            } else if (e.getActionCommand().equals("stop")) {
                ch.getB("run").setText("start");
                timer.stop();
            } else if (e.getActionCommand().equals("start")) {
                ch.getB("run").setText("stop");
                timer.start();
            }
        }
        repaint();
    }

    // Returns random number between min and max
    private int randomWithRange(int min, int max) {
        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }
        return (int) (Math.random() * ((max - min) + 1)) + min;
    }

    // Calculates delay with two exponential functions
    public void setSpeed() {
        int delay = 0;
        int value = ch.getS("speed").getValue();

        if (value == 0) {
            timer.stop();
        } else if (value >= 1 && value < 50) {
            if (!timer.isRunning()) {
                timer.start();
            }
            // Exponential function. value(1) == delay(5000). value (50) == delay(25)
            delay = (int) (a1 * (Math.pow(25 / 5000.0000, value / 49.0000)));
        } else if (value >= 50 && value <= 100) {
            if (!timer.isRunning()) {
                timer.start();
            }
            // Exponential function. value (50) == delay(25). value(100) == delay(1).
            delay = (int) (a2 * (Math.pow(1 / 25.0000, value / 50.0000)));
        }
        timer.setDelay(delay);
    }

    public boolean showSteps() {
        return showSteps;
    }
}
