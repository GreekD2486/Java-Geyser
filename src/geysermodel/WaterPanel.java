/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package geysermodel;
import javax.swing.*;
import java.awt.*;
import java.lang.Math.*;

/**
 *
 * @author stoumbosd
 */
public class WaterPanel extends JPanel {
    
    private Water wpool;
    private Steam spool;
    private int conduitcap;
    private int groundcap;
    private double maxrefill;
    private double groundtemp;
    private double thresh;
    private double oldthresh;
    private double latent;
    private double mixrate;
    private double steammixrate;
    private double heatin;
    private int steamspeed;
    private int burstpersteam;
    private int steamexpansion;
    private double threshsens;
    private double burstsens;
    private double refillsens;
    private double heatbias;
    private double wexpellbias;
    private int[] histogram;
    private int[] oldhist;
    private double bucketwidth;
    private int histwidth;
    private int totalvolume;
    private int belowgroundwater;
    private int waterexpelled;
    private int waterburst;
    private int step;
    
    public WaterPanel()
    {
        conduitcap = 50000;
        groundcap = 49500;
        groundtemp = 60;
        maxrefill = 500;
        thresh = 200;
        latent = 400;
        mixrate = 8;//percent of water that shares heat with other water
        steammixrate = 3200;//percent of steam that shares heat with water
        heatin = 1000;
        steamspeed = 500;
        burstpersteam = 20;
        steamexpansion = 30;
        threshsens = 0.001;
        burstsens = 1.01;//1 or more, lower means more water ejected
        refillsens = 0.1;//0 to 1, logarithmic to linear
        heatbias = 0.999;//0 to 1, biased to random
        wexpellbias = 0.98;//0 to 1, biased to random
        totalvolume = groundcap;
        belowgroundwater = 0;
        wpool = new Water(5*groundcap/6, groundtemp+120);
        spool = new Steam(0,0);
        histwidth = 5;
        histogram = new int[680/histwidth+1];
        bucketwidth = (thresh-groundtemp+groundcap*threshsens+latent)/(680/histwidth);
        oldhist = new int[680/histwidth+1];
    }
    
    public void step()
    {
        totalvolume = wpool.getVolume()+spool.getVolume(steamexpansion);
        if(totalvolume>conduitcap)
        {
            waterexpelled = totalvolume-conduitcap;
            wpool.expellFrom(spool.expellFrom(waterexpelled, steamspeed, conduitcap/steamspeed, steamexpansion));
            totalvolume=conduitcap;
        }else{waterexpelled = 0;}
        waterburst = (int)(spool.burst()*burstpersteam/Math.pow(burstsens, conduitcap-totalvolume));
        wpool.expellFrom(waterburst, wexpellbias, groundtemp);
        belowgroundwater = groundcap-wpool.getVolume();
        if(belowgroundwater>0)
        {
            wpool.addTo((int)((1-refillsens)*(maxrefill*(1+Math.log10(0.9*(double)(belowgroundwater/groundcap)+0.1)))+refillsens*maxrefill*belowgroundwater/groundcap), groundtemp);
        }
        wpool.mix(mixrate, thresh, latent);
        wpool.mix(spool, steammixrate, thresh, latent);
        oldthresh=thresh;
        thresh = 200+(wpool.getVolume()*threshsens);
        wpool.heat(heatin, heatbias, thresh, latent);
        spool.addTo(wpool.boil(thresh, latent, wpool.getVolume()/steamspeed));
        wpool.addTo(spool.collapse(thresh));//doesn't need to consider latent heat
        
        //System.out.println(wpool.getVolume());
        System.out.println(waterexpelled+waterburst);
        
        step++;
    }
    
    public void updateHistogram()
    {
        for(int i=0; i<histogram.length; i++)
        {
            oldhist[i]=histogram[i];
            histogram[i]=0;
        }
        int j;
        /*for(int i=0; i<wpool.getVolume(); i++)
        {
            j = (int)groundtemp;
            while(wpool.getTemp(i)>=(j+1))
            {
                j++;
            }
            histogram[(int)(j-groundtemp)]++;
        }*/
        
        for(int i=0; i<wpool.getVolume(); i++)
        {
            j = 0;
            while(wpool.getUnitHeat(i)>=groundtemp+bucketwidth*(j+1))
            {
                j++;
            }
            histogram[j]++;
        }
    }
    
    public double getAverageTempBar()
    {
        return ((wpool.getHeat()+spool.getHeat())/(wpool.getVolume()+spool.getUnitCount())-groundtemp)*(histwidth/bucketwidth);
    }
    
    public int getWaterVolume()
    {
        return wpool.getVolume();
    }
    
    public int getSteamVolume()
    {
        return spool.getVolume(steamexpansion);
    }
    
    public int getWaterExpelled()
    {
        return waterexpelled;
    }
    
    public int getWaterBurst()
    {
        return waterburst;
    }
    
    public int getConduitVolume()
    {
        return conduitcap;
    }
    
    public void drawHistogram()
    {
        /*getGraphics().clearRect((int)((oldthresh-groundtemp)*histwidth+10), 1, 699-(int)((oldthresh-groundtemp)*histwidth+10), 449);
        for(int i=0; i<histogram.length; i++)
        {
            if(histogram[i]>=oldhist[i])
            {
                getGraphics().fillRect((i*histwidth)+10, 449-(histogram[i]/6), histwidth, (histogram[i]/6));
            }else
            {
                getGraphics().clearRect((i*histwidth)+10, 1, histwidth, 449-(histogram[i]/6));
            }
        }
        getGraphics().drawRect((int)((thresh-groundtemp)*histwidth+10), 0, 1, 500);*/
        
        getGraphics().clearRect((int)((oldthresh-groundtemp)*histwidth/bucketwidth+10), 1, 1 /*699-(int)((oldthresh-groundtemp)*histwidth/bucketwidth+10)*/, 449);
        getGraphics().clearRect((int)((oldthresh+latent-groundtemp)*histwidth/bucketwidth+10), 1, 1, 449);
        for(int i=0; i<histogram.length; i++)
        {
            if(histogram[i]>=oldhist[i])
            {
                getGraphics().fillRect((i*histwidth)+10, 449-(histogram[i]*2/(3*histwidth)), histwidth, (histogram[i]*2/(3*histwidth)));
            }else
            {
                getGraphics().clearRect((i*histwidth)+10, 1, histwidth, 449-(histogram[i]*2/(3*histwidth)));
            }
        }
        getGraphics().drawRect((int)((thresh-groundtemp)*histwidth/bucketwidth+10), 0, 1, 498);
        getGraphics().drawRect((int)((thresh+latent-groundtemp)*histwidth/bucketwidth+10), 0, 1, 498);
    }
    
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        
        for(int i=0; i<histogram.length; i++)
        {
            g.drawRect((i*4)+10, 449-(histogram[i]/2), 4, (histogram[i]/2));
        }
    }
    
}
