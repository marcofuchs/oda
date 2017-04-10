/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kefu;

/**
 *
 * @author marco
 */
public class Way
{
    private int firstX;
    private int secondX;
    private int firstY;
    private int secondY;

    /**
     * @return the firstX
     */
    public int getFirstX()
    {
        return firstX;
    }

    /**
     * @param firstX the firstX to set
     */
    public void setFirstX(int firstX)
    {
        this.firstX = firstX;
    }

    /**
     * @return the secondX
     */
    public int getSecondX()
    {
        return secondX;
    }

    /**
     * @param secondX the secondX to set
     */
    public void setSecondX(int secondX)
    {
        this.secondX = secondX;
    }

    /**
     * @return the firstY
     */
    public int getFirstY()
    {
        return firstY;
    }

    /**
     * @param firstY the firstY to set
     */
    public void setFirstY(int firstY)
    {
        this.firstY = firstY;
    }

    /**
     * @return the secondY
     */
    public int getSecondY()
    {
        return secondY;
    }

    /**
     * @param secondY the secondY to set
     */
    public void setSecondY(int secondY)
    {
        this.secondY = secondY;
    }
    
    public Way(int fX, int fY, int sX, int sY)
    {
        this.firstX = fX;
        this.firstY = fY;
        this.secondX = sX;
        this.secondY = sY;
    }
}
