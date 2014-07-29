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
    private int histwidth;
    private int totalvolume;
    private int belowgroundwater;
    private double waterratio;
    private int waterexpelled;
    private int waterburst;
    private int oldwaterunits;
    private int olderwaterunits;
    private int step;
    
    public WaterPanel()
    {
        conduitcap = 50000;
        groundcap = 30000;
        groundtemp = 60;
        maxrefill = 40;
        thresh = 200;
        mixrate = 0;
        steammixrate = 5;
        heatin = 1600;
        steamspeed = 100;
        burstpersteam = 2;
        steamexpansion = 30;
        threshsens = 0.001;
        burstsens = 1.5;//lower means more water ejected
        refillsens = 0.8;//0 to 1 exponential to linear
        heatbias = 0.98;//0 to 1 biased to random
        wexpellbias = 0.90;//0 to 1 biased to random
        totalvolume = groundcap;
        belowgroundwater = 0;
        wpool = new Water(5*groundcap/6, groundtemp+120);
        spool = new Steam(0,0);
        histogram = new int[(int)(200-groundtemp+groundcap*threshsens)+1];
        oldhist = new int[(int)(200-groundtemp+groundcap*threshsens)+1];
        histwidth = 4;
    }
    
    public void step()
    {
        //olderwaterunits=oldwaterunits;
        //oldwaterunits=wpool.getVolume();
        
        totalvolume = wpool.getVolume()+steamexpansion*spool.getVolume();
        //waterratio = (double)(wpool.getVolume())/(wpool.getVolume()+steamexpansion*spool.getVolume());
        if(totalvolume>conduitcap)
        {
            waterexpelled = totalvolume-conduitcap;
            //wpool.expellFrom((int)(waterratio*(totalvolume-conduitcap)), wexpellbias, groundtemp);
            //spool.expellFrom((int)((1-waterratio)*(totalvolume-conduitcap)/steamexpansion));
            wpool.expellFrom(spool.expellFrom(waterexpelled, steamspeed, conduitcap/steamspeed, steamexpansion));
            totalvolume=conduitcap;
        }else{waterexpelled = 0;}
        waterburst = (int)(spool.burst()*burstpersteam/Math.pow(burstsens, conduitcap-totalvolume));
        wpool.expellFrom(waterburst, wexpellbias, groundtemp);
        belowgroundwater = groundcap-wpool.getVolume();
        if(belowgroundwater>0)
        {
            wpool.addTo((int)((1-refillsens)*(maxrefill*(1-Math.pow(2, -1*belowgroundwater)))+refillsens*maxrefill*belowgroundwater/groundcap), groundtemp);
        }
        wpool.mix(mixrate);
        wpool.mix(spool, steammixrate);
        oldthresh=thresh;
        thresh = 200+(wpool.getVolume()*threshsens);
        wpool.heat(heatin, heatbias, thresh);
        spool.addTo(wpool.boil(thresh, wpool.getVolume()/steamspeed));
        wpool.addTo(spool.collapse(thresh));
        //System.out.println(wpool.getVolume());
        System.out.println(waterexpelled+waterburst);
        
        step++;
        /*if(wpool.getVolume()<5000 && oldwaterunits<olderwaterunits && oldwaterunits<wpool.getVolume())
        {
            System.out.println(step);
        }*/
    }
    
    public void updateHistogram()
    {
        for(int i=0; i<histogram.length; i++)
        {
            oldhist[i]=histogram[i];
            histogram[i]=0;
        }
        int j;
        for(int i=0; i<wpool.getVolume(); i++)
        {
            j = (int)groundtemp;
            while(wpool.getTemp(i)>=(j+1))
            {
                j++;
            }
            histogram[(int)(j-groundtemp)]++;
        }
    }
    
    public double getAverageTemp()
    {
        return wpool.getAverageTemp();
    }
    
    public int getWaterUnits()
    {
        return wpool.getVolume();
    }
    
    public int getSteamUnits()
    {
        return spool.getVolume();
    }
    
    public double getWSRatio()
    {
        return waterratio;
    }
    
    public int getWaterExpelled()
    {
        return waterexpelled;
    }
    
    public int getWaterBurst()
    {
        return waterburst;
    }
    
    public void drawHistogram()
    {
        getGraphics().clearRect((int)((oldthresh-groundtemp)*histwidth+10), 1, 699-(int)((oldthresh-groundtemp)*histwidth+10), 449);
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
        getGraphics().drawRect((int)((thresh-groundtemp)*histwidth+10), 0, 1, 500);
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
