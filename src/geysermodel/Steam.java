/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package geysermodel;

/**
 *
 * @author stoumbosd
 */
public class Steam{
    
    private HOH[] pool;
    
    public Steam(int size, double inittemp)
    {
        pool = new HOH[size];
        for(int i=0; i<pool.length; i++)
        {
            pool[i] = new HOH(inittemp, true);
        }
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
    
    public int expellFrom(int count, int queueunit, int maxqueue, int expansion)
    {
        if(count<expansion){return count;}
        int[] queues = countQueue(maxqueue);
        int queuevolume = 0;
        int watertbe = 0;
        int queuesplit = 0;
        for(int i=0; i<queues.length; i++)
        {
            queuevolume = queues[i]*expansion+queueunit;
            if(queuevolume<count)
            {
                count-=queuevolume;
                watertbe+=queueunit;
                if(queues[i]>0){expellFrom(-1, i);}
            }else
            {
                queuesplit=i;
                break;
            }
        }
        double fraction = (double)(count)/queuevolume;
        expellFrom((int)(fraction*queues[queuesplit]), queuesplit);
        count-=expansion*fraction*queues[queuesplit];
        watertbe+=count;
        jumpRise(queuesplit);
        return watertbe;
    }
    
    public int[] countQueue(int maxqueue)
    {
        int[] tally = new int[maxqueue+2];
        for(int i=0; i<pool.length; i++)
        {
            tally[pool[i].getQueue()]++;
        }
        return tally;
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
    
    public void expellFrom(int count, int queue)
    {
        if(count==0){return;}
        if(count<0)
        {
            count = markAll(queue);
        }else
        {
            mark(count, queue);
        }
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
    
    public void mark(int tbm, int queue)
    {
        int index;
        for(int i=0; i<tbm; i++)
        {
            index = (int)(Math.random()*pool.length);
            while(pool[index].isMarked() || pool[index].getQueue()!=queue)
            {
                index++;
                index%=pool.length;
            }
            pool[index].mark();
        }
    }
    
    public int markAll(int queue)
    {
        int tally = 0;
        for(int i=0; i<pool.length; i++)
        {
            if(pool[i].getQueue()==queue)
            {
                pool[i].mark();
                tally++;
            }
        }
        return tally;
    }
    
    public void jumpRise(int jump)
    {
        for(int i=0; i<pool.length; i++)
        {
            pool[i].jumpQueue(jump);
        }
    }
    
    public void rise()
    {
        for(int i=0; i<pool.length; i++)
        {
            pool[i].rise();
        }
    }
    
    public int burst()
    {
        for(int i=0; i<pool.length; i++)
        {
            pool[i].rise();
        }
        int count = countToBurst();
        HOH[] newpool = new HOH[pool.length - count];
        int tbr = count;
        for(int i=0; i<pool.length; i++)
        {
            if(pool[i].getQueue()>=0)
            {
                newpool[i-count+tbr]=pool[i];
            }else
            {
                tbr--;
            }
        }
        pool=newpool;
        return count;
    }
    
    private int countToBurst()
    {
        int tally = 0;
        for(int i=0; i<pool.length; i++)
        {
            if(pool[i].getQueue()<0)
            {
                tally++;
            }
        }
        return tally;
    }
    
    public HOH[] collapse(double thresh)
    {
        int count = countToCollapse(thresh);
        HOH[] newpool = new HOH[pool.length - count];
        HOH[] removedpool = new HOH[count];
        int tbr = count;
        for(int i=0; i<pool.length; i++)
        {
            if(!pool[i].getPhase())
            {
                newpool[i-count+tbr]=pool[i];
            }else
            {
                removedpool[count-tbr]=pool[i];
                tbr--;
            }
        }
        pool=newpool;
        return removedpool;
    }
    
    private int countToCollapse(double thresh)
    {
        int count = 0;
        for(int i=0; i<pool.length; i++)
        {
            if(pool[i].getTemp()<thresh)
            {
                pool[i].collapse();
                count++;
            }
        }
        return count;
    }
    
    public int getVolume()
    {
        return pool.length;
    }
    
    public HOH getUnit(int i)
    {
        return pool[i];
    }
    
}
