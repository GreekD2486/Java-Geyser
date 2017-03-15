/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package geysermodel;

/**
 *
 * @author stoumbosd
 */
public class Pool {
    
    private HOH[] pool;
    private Water[] wpool;
    private int surface;
    
    public Pool(int size, double inittemp)
    {
        pool = new HOH[size];
        for(int i=0; i<pool.length; i++)
        {
            pool[i] = new HOH(inittemp, true);
        }
    }
    
    public void addTo(int amount, double temp)
    {
        HOH[] newpool = new HOH[pool.length + amount];
        for(int i=0; i<pool.length; i++)
        {
            newpool[i] = pool[i];
        }
        for(int i=0; i<amount; i++)
        {
            newpool[i] = new HOH(temp, true);
        }
        pool=newpool;
    }
    
    public void addTo(HOH[] tba)
    {
        HOH[] newpool = new HOH[pool.length + tba.length];
        for(int i=0; i<pool.length; i++)
        {
            newpool[i] = pool[i];
        }
        for(int i=0; i<tba.length; i++)
        {
            newpool[i] = tba[i+pool.length];
        }
        pool=newpool;
    }
    
    public void mark(int tbm)
    {
        int index;
        for(int i=0; i<tbm; i++)
        {
            index = (int)(Math.random()*pool.length);
            while(pool[index].isMarked())
            {
                index++;
                index%=pool.length;
            }
            pool[index].mark();
            System.out.println(pool[index].isMarked());
            //System.out.println("length = " + pool.length);
        }
    }
    
    public void deMark()
    {
        for(int i=0; i<pool.length; i++)
        {
            pool[i].deMark();
        }
    }
    
    public double getTemp(int i)
    {
        return pool[i].getHeat();
    }
    
    public int getVolume()
    {
        return pool.length;
    }
    
}
