package Persistance;

import Kochfractal.Edge;

import java.io.Serializable;

/**
 * Created by Max Meijer on 10/05/2017.
 * Fontys University of Applied Sciences, Eindhoven
 */
public class EdgeData implements Serializable {
    public double X1, Y1, X2, Y2;
    public double Red, Green, Blue;

    public EdgeData(Edge e) {
        X1 = e.X1;
        Y1 = e.Y1;
        X2 = e.X2;
        Y2 = e.Y2;
        Red = e.color.getRed();
        Green = e.color.getGreen();
        Blue = e.color.getBlue();
    }
}
