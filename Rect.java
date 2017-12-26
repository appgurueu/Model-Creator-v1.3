/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package appguru;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;

/**
 *
 * @author lars
 */
public class Rect {

    public int x, y;
    public int w, h;
    public Point[] p;

    public Rect(int x, int y, int w, int h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        p = new Point[4];
        p[0] = new Point(x, y);
        p[1] = new Point(x + w, y);
        p[2] = new Point(x + w, y + h);
        p[3] = new Point(x, y + h);
    }

    @Override
    public String toString() {
        return String.format("Rechteck : x=%d y=%d w=%d h=%d", x, y, w, h);
    }

    public boolean ok(BufferedImage img) {
        if (x < 0 || y < 0 || x + w > img.getWidth() || y + h > img.getHeight()) {
            return false;
        }
        for (int xw = x; xw < x + w; xw++) {
            for (int yw = y; yw < y + h; yw++) {
                int c = img.getRGB(xw, yw);
                Color omg = new Color(c, true);
                if (omg.getAlpha() == 0) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean cp(Rect r, Point p) {
        return (p.x > r.x && p.x < r.x + r.w && p.y > r.y && p.y < r.y + r.h);
    }

    public static boolean cr(Rect r1, Rect r2) {
        //boolean pc=(cp(r1,r2.p[0]) || cp(r1,r2.p[1]) || cp(r1,r2.p[2]) || cp(r1,r2.p[3]) || (cp(r2,r1.p[0]) || cp(r2,r1.p[1]) || cp(r2,r1.p[2]) || cp(r2,r1.p[3])));
        boolean pc = ((cp(r1, r2.p[0]) || cp(r1, r2.p[1]) || cp(r1, r2.p[2]) || cp(r1, r2.p[3])) || (cp(r2, r1.p[0]) || cp(r2, r1.p[1]) || cp(r2, r1.p[2]) || cp(r2, r1.p[3])));
        boolean cc1 = (((r2.y > r1.y && r2.y < r1.y + r1.h) || (r2.y + r2.h > r1.y && r2.y + r2.h < r1.y + r1.h)) && ((r2.x > r1.x && r2.x < r1.x + r1.w) || (r2.x + r2.w > r1.x && r2.x + r2.w < r1.x + r1.w)));
        boolean cc2 = (((r1.y > r2.y && r1.y < r2.y + r2.h) || (r1.y + r1.h > r2.y && r1.y + r1.h < r2.y + r2.h)) && ((r1.x > r2.x && r1.x < r2.x + r2.w) || (r1.x + r1.w > r2.x && r1.x + r1.w < r2.x + r2.w)));
        boolean ce = (r1.x == r2.x && r1.y == r2.y && r1.w == r2.w && r1.h == r2.h);
        return ((cc1 || cc2) || pc || ce);
    }

    public boolean collidepoint(Point p) {
        return cp(this, p);
    }

    public boolean colliderect(Rect r) {
        return cr(this, r);
    }
}