/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kefu;

import com.vividsolutions.jts.geom.Coordinate;
import java.util.List;

/**
 * Stellt einen Weg mitsamt seiner Zielkreuzung dar, der von einer dieser Klasse
 * nicht bekanntne Vorgänger aus erreicht werden kann.
 *
 * @author marco
 */
public class CrossingConnection {
    private Crossing target;
    private final Link linkToTarget;

    /**
     *
     * @param target
     * @param linkToTarget
     */
    public CrossingConnection(Crossing target, Link linkToTarget) {
        this.target = target;
        this.linkToTarget = linkToTarget;
    }

    /**
     * @return the target
     */
    public Crossing getTarget() {
        return target;
    }

    /**
     * @param target the target to set
     */
    public void setTarget(Crossing target) {
        this.target = target;
    }

    /**
     * Fährt soweit in Richtung des Zielcrossings, wie es in der zur Verfügung
     * stehenden Zeit möglich ist.
     *
     * @param startMinutes Startzeit
     * @param maxMinutes Maximale Zeit
     * @return Koordinaten auf dem Weg zum Ziel, die innerhalb der Zeit liegen
     */
    public List<Coordinate> driveUpToMinutes(double startMinutes, double maxMinutes) {
        return linkToTarget.drive(startMinutes, maxMinutes);
    }
}
