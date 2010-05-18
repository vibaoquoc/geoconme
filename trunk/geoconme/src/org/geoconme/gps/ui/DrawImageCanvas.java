package org.geoconme.gps.ui;

import java.io.IOException;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public class DrawImageCanvas extends Canvas {
    static Image image;

    int count;

    public void paint(Graphics g) {
        int width = getWidth();
        int height = getHeight();

        // Fill the background using black
        g.setColor(0);
        g.fillRect(0, 0, width, height);

        // Load an image from the MIDlet resources
        if (image == null) {
            try {
                image = Image.createImage(getClass().getResourceAsStream("/geoconme.png"));
            } catch (IOException ex) {
                g.setColor(0xffffff);
                g.drawString("Failed to load image!", 0, 0, Graphics.TOP | Graphics.LEFT);
                ex.printStackTrace();
                return;
            }
        }

        
            // Draw it in the center
            g.drawImage(image, width/2, height/2, Graphics.VCENTER | Graphics.HCENTER);
       
        
    }
}