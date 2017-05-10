/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Kochfractal;

import Persistance.EdgeData;
import javafx.scene.paint.Color;

import java.io.Serializable;

/**
 *
 * @author Peter Boots
 */
public class Edge implements Serializable {
    public double X1, Y1, X2, Y2;
    public Color color;

    public Edge(double X1, double Y1, double X2, double Y2, Color color) {
        this.X1 = X1;
        this.Y1 = Y1;
        this.X2 = X2;
        this.Y2 = Y2;
        this.color = color;
    }

    public Edge(EdgeData e) {
        X1 = e.X1;
        Y1 = e.Y1;
        X2 = e.X2;
        Y2 = e.Y2;
        color = new Color(e.Red, e.Green, e.Blue, 1);
    }

    @Override
    public String toString() {
        return "Edge{" +
                "X1=" + X1 +
                ", Y1=" + Y1 +
                ", X2=" + X2 +
                ", Y2=" + Y2 +
                ", color=" + color +
                '}';
    }

}
