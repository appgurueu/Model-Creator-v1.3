/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package appguru;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 *
 * @author lars
 */
public class Canvas extends JPanel {

    public int dim;
    public BufferedImage image;
    public BufferedImage preview;
    public BufferedImage image_2;
    public BufferedImage preview_2;
    public BufferedImage bg;
    public ArrayList<Tuple<Point, Point>> punkte;
    public ArrayList<Rect> rects;
    public static final RenderingHints antialiasing = new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
    public BufferedImage depthmap;
    public BufferedImage depth_preview;
    public BufferedImage depthmap_2;
    public BufferedImage depth_preview_2;

    public Canvas() {
        try {
            dim = 152;
            punkte = new ArrayList();
            rects = new ArrayList();
            bg = new BufferedImage(dim, dim, BufferedImage.TYPE_INT_RGB);
            Graphics2D bgg = bg.createGraphics();
            int c = 0;
            Color g1 = new Color(70, 70, 70);
            Color g2 = new Color(140, 140, 140);
            for (int x = 0; x <= dim / 4; x++) {
                for (int y = 0; y <= dim / 4; y++) {
                    bgg.setColor(g2);
                    if (c % 2 == 0) {
                        bgg.setColor(g1);
                    }
                    bgg.fillRect(x * 4, y * 4, 4, 4);
                    c++;
                }
            }
            BufferedImage white = new BufferedImage(1024, 1024, BufferedImage.TYPE_INT_ARGB);
            for (int x = 0; x < white.getWidth(); x++) {
                for (int y = 0; y < white.getHeight(); y++) {
                    white.setRGB(x, y, Color.WHITE.getRGB());
                }
            }
            setImage(white, "depthmap", "depth_preview");
            setImage(white, "depthmap_2", "depth_preview_2");
            setImage(white, "image", "preview");
            setImage(white, "image_2", "preview_2");
            //setImage(new BufferedImage(80, 80, BufferedImage.TYPE_INT_ARGB));
        } catch (Exception ex) {
            System.err.println(ex);
            JOptionPane.showMessageDialog(new JFrame(),
                    "Error ! Invalid Input !",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);

        }
    }

    public void setImage(BufferedImage img, String targets, String preview_targets) throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Field target_field = this.getClass().getField(targets);
        Field preview_target_field = this.getClass().getField(preview_targets);
        BufferedImage target = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                target.setRGB(x, y, img.getRGB(x, y));
            }
        }
        target_field.set(this, target);
        BufferedImage preview_target = new BufferedImage(dim, dim, BufferedImage.TYPE_INT_ARGB);
        AffineTransform scale = new AffineTransform();
        float relx;
        float rely;
        relx = (float) dim / img.getWidth();
        rely = relx;
        scale.scale(relx, rely);
        AffineTransformOp apply_scale = new AffineTransformOp(scale, antialiasing);
        apply_scale.filter(img, preview_target);
        preview_target_field.set(this, preview_target);
    }

    public void drawImage(Graphics2D g, BufferedImage img, int x, int y) {
        g.drawImage(bg, x + 2, y + 2, this);
        if (img != null) {
            g.drawImage(img, x + 2, y + 2, this);
        }
        g.setStroke(new BasicStroke(2));
        g.setColor(new Color(155, 155, 255));
        g.drawRect(x + 1, y + 1, dim + 2, dim + 2);
    }

    @Override
    public void paintComponent(Graphics g2) {
        Graphics2D g = (Graphics2D) g2;
        drawImage(g, preview, 0, 0);
        drawImage(g, depth_preview, dim + 1, 0);
        drawImage(g, preview_2, 0, dim + 1);
        drawImage(g, depth_preview_2, dim + 1, dim + 1);
    }
}

