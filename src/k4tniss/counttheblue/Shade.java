/*
 * Copyright (C) 2016 Katniss
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package k4tniss.counttheblue;

/**
 *
 * @author Katniss
 */
public class Shade implements Comparable<Shade> {
    
    public int shade;
    public int count;
    public int color;
    
    public static int R = 0;
    public static int G = 1;
    public static int B = 2;
    public static int A = 3;
    
    
    public Shade(int color, int shade, int count) {
        this.count = count;
        this.shade = shade;
        this.color = color;
    }
    
    public void setCount(int count) {
        this.count = count;
    }
    
    public void incrementCount() {
        this.count += 1;
    }
    
    @Override
    public String toString() {
        return Shade.getColor(color)+" SHADE: ["+shade+"] COUNT: ["+count+"]";
    }
    
    public static String getColor(int color) {
        switch (color) {
            case 0:
                return "RED";
            case 1:
                return "GREEN";
            case 2:
                return "BLUE";
            case 3:
                return "ALPHA";
        }
        return null;
    }

    @Override
    public int compareTo(Shade s) {
        if (this.color > s.color)
            return 1;
        if (this.color < s.color)
            return -1;
        else
        {
            if (this.shade > s.shade)
                return 1;
            if (this.shade < s.shade)
                return -1;
            else
                return 0;
        }
    }
}
