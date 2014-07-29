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
    
    private double temp;
    private int queue;
    
    private boolean isliquid;
    private boolean marked;
    
    public HOH(double inittemp, boolean liquid)
    {
        temp=inittemp;
        queue=0;
        if(liquid){isliquid=true;}else{isliquid=false;}
        marked=false;
    }
    
    public double mixWith(HOH w)
    {
        double rand = Math.pow(Math.random(), 0.01)/2;
        double average = (temp + w.temp)/2;
        temp = average + (temp-w.temp)*rand;
        return temp - 2*(temp-w.temp)*rand;
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
    
    public double getTemp()
    {
        return temp;
    }
    
    public void setTemp(double t)
    {
        temp=t;
    }
    
    public void addTemp(double t)
    {
        temp+=t;
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
