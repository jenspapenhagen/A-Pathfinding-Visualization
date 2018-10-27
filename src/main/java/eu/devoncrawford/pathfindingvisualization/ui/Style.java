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
package eu.devoncrawford.pathfindingvisualization.ui;

import java.awt.Color;
import java.awt.Font;

/**
 * Custom fonts and colours used in "Frame.java" class
 *
 * @author Devon Crawford
 */
public @interface Style {
    Font bigText = new Font("arial", Font.BOLD, 24);
    Font REALBIGText = new Font("arial", Font.BOLD, 72);
    Font numbers = new Font("arial", Font.BOLD, 12);
    Font smallNumbers = new Font("arial", Font.PLAIN, 11);
    Color greenHighlight = new Color(132, 255, 138);
    Color redHighlight = new Color(253, 90, 90);
    Color blueHighlight = new Color(32, 233, 255);
    Color btnPanel = new Color(120, 120, 120, 80);
    Color darkText = new Color(48, 48, 48);
    Color lightText = new Color(232, 232, 232);
}
