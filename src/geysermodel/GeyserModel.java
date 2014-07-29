/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package geysermodel;
import java.lang.Math.*;

/**
 *
 * @author stoumbosd
 */
public class GeyserModel {

    /**
     * @param args the command line arguments
     */
    
    
    
    
    public static void main(String[] args)
    {
        
        double[] rands = new double[1000000];
        for(int i=0; i<rands.length; i++)
        {
            rands[i]=9*Math.pow(Math.random(),2);
        }
        int[] histo = new int[10];
        int j;
        for(int i=0; i<rands.length; i++)
        {
            j = 0;
            while(rands[i]>((double)j+0.1))
            {
                j++;
            }
            histo[j]++;
        }
        for(int i=0; i<histo.length; i++)
        {
            System.out.println(histo[i]);
        }
    }
    
    public static boolean command()
    {
        return true;
    }
    
    
}
