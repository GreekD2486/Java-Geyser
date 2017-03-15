/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package geysermodel;

/**
 *
 * @author stoumbosd
 */
public class HOH {
    
    private double heat;
    private int queue;
    
    private boolean isliquid;
    private boolean marked;
    
    public HOH(double inittemp, boolean liquid)
    {
        heat=inittemp;
        queue=0;
        isliquid = liquid;
        marked=false;
    }
    
    public double mixWith(HOH w)
    {
        double rand = Math.pow(Math.random(), 0.01)/2;
        double average = (heat + w.heat)/2;
        heat = average + (heat-w.heat)*rand;
        return heat - 2*(heat-w.heat)*rand;
    }
    
    public void mixWith(HOH w, double thresh, double latent)
    {
        double rand = Math.pow(Math.random(), 100)/2;
        double difference = w.getTemp(thresh, latent) - getTemp(thresh, latent);
        heat += difference*rand;
        w.addHeat(0 - difference*rand);
    }
    
    public boolean isMarked()
    {
        return marked;
    }
    
    public void mark()
    {
        marked=true;
    }
    
    public void deMark()
    {
        marked=false;
    }
    
    public boolean getPhase()
    {
        return isliquid;
    }
    
    public void boil()
    {
        isliquid=false;
    }
    
    public void collapse()
    {
        isliquid=true;
    }
    
    public double getHeat()
    {
        return heat;
    }
    
    public double getTemp(double thresh, double latent)
    {
        if(heat < thresh)
        {
            return heat;
        }else if(heat > (thresh + latent))
        {
            return (heat - latent);
        }else
        {
            return thresh;
        }
    }
    
    public void setHeat(double t)
    {
        heat=t;
    }
    
    public void addHeat(double t)
    {
        heat+=t;
    }
    
    public int getQueue()
    {
        return queue;
    }
    
    public void startQueue(int bubbdist)
    {
        queue=bubbdist;
    }
    
    public void rise()
    {
        queue--;
    }
    
    public void jumpQueue(int jump)
    {
        queue-=jump;
    }
    
}
