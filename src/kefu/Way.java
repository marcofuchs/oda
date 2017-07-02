package kefu;

/**
 *
 * @author Marco Verwaltet die Informationen einer Weg-Instanz
 */
public class Way {

    private int firstX;
    private int secondX;
    private int firstY;
    private int secondY;

    private double length = -1;

    /**
     * @return the firstX
     */
    public int getFirstX() {
        return firstX;
    }

    /**
     * @param firstX the firstX to set
     */
    public void setFirstX(int firstX) {
        this.firstX = firstX;
    }

    /**
     * @return the secondX
     */
    public int getSecondX() {
        return secondX;
    }

    /**
     * @param secondX the secondX to set
     */
    public void setSecondX(int secondX) {
        this.secondX = secondX;
    }

    /**
     * @return the firstY
     */
    public int getFirstY() {
        return firstY;
    }

    /**
     * @param firstY the firstY to set
     */
    public void setFirstY(int firstY) {
        this.firstY = firstY;
    }

    /**
     * @return the secondY
     */
    public int getSecondY() {
        return secondY;
    }

    /**
     * @param secondY the secondY to set
     */
    public void setSecondY(int secondY) {
        this.secondY = secondY;
    }

    /**
     * Errechnet die Länge dieses Weges.
     * 
     * @return
     */
    public double getLength() {
        if (length == -1) {
            double a2 = firstX - secondX;
            a2 *= a2;
            double b2 = firstY - secondY;
            b2 *= b2;

            // Pythagoras!
            length = Math.sqrt(a2 + b2);
        }

        return length;
    }

    /**
     * @param length the length to set
     */
    public void setLength(double length) {
        this.length = length;
    }

    /**
     * Erzeugt einen neuen Way mit gegebener Start- und Endkoordinate
     * 
     * @param fX : first X
     * @param fY : first Y
     * @param sX : second X
     * @param sY : second Y
     */
    public Way(int fX, int fY, int sX, int sY) {
        this.firstX = fX;
        this.firstY = fY;
        this.secondX = sX;
        this.secondY = sY;
    }
}
