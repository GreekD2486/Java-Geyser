/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package geysermodel;

/**
 *
 * @author stoumbosd
 */
public class Water{
    
    private HOH[] pool;
    
    public Water(int size, double inittemp)
    {
        pool = new HOH[size];
        for(int i=0; i<pool.length; i++)
        {
            pool[i] = new HOH((Math.random()*0.1+1)*inittemp, true);
        }
    }
    
    public void addTo(int amount, double temp)
    {
        //System.out.println(amount);
        HOH[] newpool = new HOH[pool.length + amount];
        for(int i=0; i<pool.length; i++)
        {
            newpool[i] = pool[i];
        }
        for(int i=0; i<amount; i++)
        {
            newpool[i+pool.length] = new HOH(temp, true);
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
            newpool[i+pool.length] = tba[i];
        }
        pool=newpool;
    }
    
    public HOH[] boil(double thresh, double latent, int queue)
    {
        int count = countToBoil(thresh, latent);
        HOH[] newpool = new HOH[pool.length - count];
        HOH[] removedpool = new HOH[count];
        int tbr = count;
        for(int i=0; i<pool.length; i++)
        {
            if(pool[i].getPhase())
            {
                newpool[i-count+tbr]=pool[i];
            }else
            {
                pool[i].startQueue(queue);
                removedpool[count-tbr]=pool[i];
                tbr--;
            }
        }
        pool=newpool;
        return removedpool;
    }
    
    private int countToBoil(double thresh, double latent)
    {
        int count = 0;
        for(int i=0; i<pool.length; i++)
        {
            if(pool[i].getHeat()>=(thresh + latent))
            {
                pool[i].boil();
                count++;
            }
        }
        return count;
    }
    
    public void mix(double mixrate, double thresh, double latent)
    {
        if(pool.length>1)
        {
            int mixes = (int)(pool.length*mixrate/100);
            int mix1;
            int mix2;
            for(int i=0; i<mixes; i++)
            {
                mix1=(int)(Math.random()*pool.length);
                mix2=(int)(Math.random()*(pool.length-1));
                if(mix1==mix2){mix1++;}
                pool[mix1].mixWith(pool[mix2], thresh, latent);
            }
        }
    }
    
    public void mix(Steam s, double mixrate, double thresh, double latent)
    {
        if(pool.length>1 && s.getUnitCount()>1)
        {
            int mixes = (int)(s.getUnitCount()*mixrate/100);
            int mix1;
            int mix2;
            for(int i=0; i<mixes; i++)
            {
                mix1=(int)(Math.random()*pool.length);
                mix2=(int)(Math.random()*s.getUnitCount());
                pool[mix1].mixWith(s.getUnit(mix2), thresh, latent);
            }
        }
    }
    
    public void heat(double heatin)
    {
        int distribution = (int)(Math.random()*pool.length/100+1);//could be zero
        mark(distribution);
        for(int i=0; i<pool.length; i++)
        {
            if(pool[i].isMarked())
            {
                pool[i].addHeat(heatin/distribution);
                pool[i].deMark();
            }
        }
    }
    
    public void heat(double heatin, double bias, double thresh, double latent)
    {
        int distribution = (int)(Math.random()*pool.length/100+1);
        mark(distribution, bias, thresh, latent, true);
        for(int i=0; i<pool.length; i++)
        {
            if(pool[i].isMarked())
            {
                pool[i].addHeat(heatin/distribution);
                pool[i].deMark();
            }
        }
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
        }
    }
    
    public void mark(int tbm, double bias, double templimit, double latent, boolean hot)
    {
        int index;
        for(int i=0; i<tbm; i++)
        {
            index = (int)(Math.random()*pool.length);
            while(pool[index].isMarked() || (hot && Math.random()>Math.pow(bias, templimit+latent-pool[index].getHeat())) || (!hot && Math.random()>Math.pow(bias, pool[index].getHeat()-templimit)))
            {
                index++;
                index%=pool.length;
            }
            pool[index].mark();
        }
    }
    
    public void expellFrom(int count)
    {
        if(count>pool.length)
        {
            count = pool.length;
        }
        mark(count);
        HOH[] newpool = new HOH[pool.length - count];
        int tbr = count;
        for(int i=0; i<pool.length; i++)
        {
            if(!pool[i].isMarked())
            {
                newpool[i-count+tbr]=pool[i];
            }else
            {
                tbr--;
            }
        }
        pool=newpool;
    }
    
    public void expellFrom(int count, double bias, double templimit)
    {
        if(count>pool.length)
        {
            count = pool.length;
        }
        mark(count, bias, templimit, 0, false);
        HOH[] newpool = new HOH[pool.length - count];
        int tbr = count;
        for(int i=0; i<pool.length; i++)
        {
            if(!pool[i].isMarked())
            {
                newpool[i-count+tbr]=pool[i];
            }else
            {
                tbr--;
            }
        }
        pool=newpool;
    }
    
    public double getUnitTemp(int i, double thresh, double latent)
    {
        return pool[i].getTemp(thresh, latent);
    }
    
    public double getUnitHeat(int i)
    {
        return pool[i].getHeat();
    }
    
    public int getVolume()
    {
        return pool.length;
    }
    
    public double getHeat()
    {
        double count=0;
        for(int i=0; i<pool.length; i++)
        {
            count+=pool[i].getHeat();
        }
        return count;
    }
    
}
