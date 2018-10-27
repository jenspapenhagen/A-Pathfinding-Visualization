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
package eu.devoncrawford.pathfindingvisualization.entity;

/**
 * Node.java is an object used for every node (or block) on the grid for path
 * finding. Including walls, open, closed path, start and end nodes. This class
 * allows each node to store its position, calculations (f, g, h), parents, and
 * determine equalities to other nodes.
 *
 * @author Devon Crawford
 */
public class Node implements Comparable<Node> {

    private int x, y, g, h, f;
    private Node parent;

    public Node(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getG() {
        return g;
    }

    public int getH() {
        return h;
    }

    public int getF() {
        return f;
    }

    public Node getNode() {
        return parent;
    }

    public Node getParent() {
        return parent;
    }

    public void setXY(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setG(int g) {
        this.g = g;
    }

    public void setH(int h) {
        this.h = h;
    }

    public void setF(int f) {
        this.f = f;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Node) {
            Node that = (Node) obj;

            if (this.getX() == that.getX() && this.getY() == that.getY()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + this.x;
        hash = 59 * hash + this.y;
        return hash;
    }

    @Override
    public int compareTo(Node that) {
        return that.getF() - this.getF();
    }
}
