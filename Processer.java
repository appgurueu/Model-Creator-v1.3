/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package appguru;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author lars
 */
public class Processer {

    public static BufferedImage nand(BufferedImage one, BufferedImage two) {
        int maxw = Math.max(one.getWidth(), two.getWidth());
        int maxh = Math.max(one.getHeight(), two.getHeight());
        BufferedImage result = new BufferedImage(maxw, maxh, BufferedImage.TYPE_INT_ARGB);
        boolean set = false;
        for (int x = 0; x < one.getWidth(); x++) {
            for (int y = 0; y < one.getHeight(); y++) {
                if (x >= two.getWidth() || y >= two.getHeight()) {
                    if (new Color(one.getRGB(x, y), true).getAlpha() != 0) {
                        set = true;
                        result.setRGB(x, y, new Color(0, 0, 0, 1).getRGB());
                    }
                    continue;
                }
                boolean one_equals_zero = new Color(one.getRGB(x, y), true).getAlpha() == 0;
                boolean two_equals_zero = new Color(two.getRGB(x, y), true).getAlpha() == 0;
                if (!one_equals_zero && two_equals_zero) {
                    set = true;
                    result.setRGB(x, y, new Color(0, 0, 0, 1).getRGB());
                }
            }
        }
        if (!set) {
            return null;
        } else {
            return result;
        }
    }

    public static ArrayList<Tuple<Float, BufferedImage>> sections(BufferedImage img, BufferedImage dmap) {
        HashMap<Float, ArrayList<Point>> regions = new HashMap();
        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                Color color = new Color(img.getRGB(x, y), true);
                if (color.getAlpha() != 0) {
                    float depth = new Color(dmap.getRGB(x, y)).getBlue() / 255.0f;
                    if (regions.get(depth) == null) {
                        ArrayList<Point> ps = new ArrayList();
                        ps.add(new Point(x, y));
                        regions.put(depth, ps);
                    } else {
                        ArrayList<Point> t = regions.get(depth);
                        t.add(new Point(x, y));
                        regions.put(depth, t);
                    }
                }
            }
        }
        ArrayList<Tuple<Float, BufferedImage>> result = new ArrayList();
        for (Map.Entry<Float, ArrayList<Point>> e : regions.entrySet()) {
            BufferedImage r = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
            //BufferedImage r = new BufferedImage(e.getValue().v2[1].x - e.getValue().v2[0].x+2, e.getValue().v2[1].y - e.getValue().v2[0].y+2, BufferedImage.TYPE_INT_ARGB);
            int transp = new Color(0, 0, 0, 255).getRGB();
            for (Point p : e.getValue()) {
                r.setRGB(p.x/*- e.getValue().v2[0].x + 1*/, p.y/*- e.getValue().v2[0].y + 1*/, transp);
            }
            result.add(new Tuple(e.getKey(), r));
        }
        return result;
    }

    public static ArrayList<Tuple<Point, Point>> neighbors(ArrayList<Tuple<Point, Point>> borders, Tuple<Point, Point> p) {
        ArrayList<Tuple<Point, Point>> r = new ArrayList();
        Point dir = new Point(p.v1.x - p.v2.x, p.v1.y - p.v2.y);
        int px = p.v1.x / 2;
        int py = p.v1.y / 2;
        for (Tuple<Point, Point> q : borders) {
            int qx = q.v1.x / 2;
            int qy = q.v1.y / 2;
            if ((qx == px + dir.x && qy == py + dir.y) || (qx == px - dir.x && qy == py - dir.y)) {
                Point qdir = new Point(q.v1.x - q.v2.x, q.v1.y - q.v2.y);
                if (qdir.x == dir.x && qdir.y == dir.y && p.v1.x % 2 == q.v1.x % 2 && p.v1.y % 2 == q.v1.y % 2) {
                    r.add(q);
                }
            }
        }
        return r;
    }

    public static HashMap<Tuple<Float, Float>, ArrayList<Tuple<Point, Point>>> border(ArrayList<Point> grenze, BufferedImage rimg, BufferedImage img, BufferedImage depthmap, boolean priority) {
        ArrayList<Point> check = new ArrayList();
        check.add(new Point(- 1, 0));
        check.add(new Point(0, - 1));
        check.add(new Point(1, 0));
        check.add(new Point(0, 1));
        HashMap<Tuple<Float, Float>, ArrayList<Tuple<Point, Point>>> map = new HashMap();
        //ArrayList<Tuple<Point, Point>> result = new ArrayList();
        //ArrayList<Tuple<Float, Float>> heights = new ArrayList();
        for (Point p : grenze) {
            Color c = new Color(depthmap.getRGB(p.x, p.y), true);
            for (Point q : check) {
                Color c2 = new Color(0, 0, 0, 0);
                Color depth = new Color(0, 0, 0, 0);
                boolean is_no_neighbor;
                if (!(p.x + q.x < 0
                        || p.x + q.x > img.getWidth() - 1 || p.y + q.y < 0 || p.y + q.y > img.getHeight() - 1)) {
                    c2 = new Color(img.getRGB(p.x + q.x, p.y + q.y), true);
                    depth = new Color(depthmap.getRGB(p.x + q.x, p.y + q.y));
                    is_no_neighbor = new Color(rimg.getRGB(p.x + q.x, p.y + q.y), true).getAlpha() == 0;
                } else {
                    is_no_neighbor = true;
                }
                if (c2.getAlpha() == 0) {
                    boolean breakdown = false;
                    float s = 0;
                    //if (!is_no_neighbor) {
                    s = depth.getRed() / 255.0f;
                    //}
                    Tuple<Float, Float> height = new Tuple(s, c.getRed() / 255.0f);
                    if (height.v1 == height.v2) {
                        breakdown = true;
                    }
                    //if (is_no_neighbor) {
                    if (height.v1 > height.v2) {
                        breakdown = true;
                    }
                    //} else if (height.v2 > height.v1) {
                    //  breakdown = true;
                    //}
                    if (!breakdown) {
                        ArrayList<Tuple<Point, Point>> register = map.get(height);
                        if (register == null) {
                            register = new ArrayList();
                        }
                        if (q.x < 0) {
                            register.add(new Tuple(new Point(p.x * 2, p.y * 2), new Point(p.x * 2, p.y * 2 + 1)));
                        } else if (q.x > 0) {
                            register.add(new Tuple(new Point(p.x * 2 + 1, p.y * 2), new Point(p.x * 2 + 1, p.y * 2 + 1)));
                        } else if (q.y < 0) {
                            register.add(new Tuple(new Point(p.x * 2, p.y * 2), new Point(p.x * 2 + 1, p.y * 2)));
                        } else {
                            register.add(new Tuple(new Point(p.x * 2, p.y * 2 + 1), new Point(p.x * 2 + 1, p.y * 2 + 1)));
                        }
                        map.put(height, register);
                    }
                }
            }
        }
        HashMap<Tuple<Point, Point>, Boolean> verplant;
        ArrayList<Tuple<Point, Point>> newCheck;
        ArrayList<Tuple<Point, Point>> toCheck;
        Tuple<Point, Point> best = null;
        ArrayList<Tuple<Point, Point>> ergebnis = new ArrayList();
        for (Map.Entry<Tuple<Float, Float>, ArrayList<Tuple<Point, Point>>> e : map.entrySet()) {
            verplant = new HashMap();
            ArrayList<Tuple<Point, Point>> result = e.getValue();
            for (Tuple<Point, Point> t : result) {
                if (verplant.get(t) == null) {
                    best = t;
                    toCheck = new ArrayList();
                    toCheck.add(t);
                    verplant.put(t, Boolean.FALSE);
                    while (true) {
                        if (toCheck.isEmpty()) {
                            break;
                        }
                        newCheck = new ArrayList();
                        for (Tuple<Point, Point> t2 : toCheck) {
                            ArrayList<Tuple<Point, Point>> n = neighbors(result, t2);
                            for (Tuple<Point, Point> t3 : n) {
                                if (verplant.get(t3) == null) {
                                    if (t3.v1.x < best.v1.x) {
                                        best.v1.x = t3.v1.x;
                                    }
                                    if (t3.v2.x > best.v2.x) {
                                        best.v2.x = t3.v2.x;
                                    }
                                    if (t3.v1.y < best.v1.y) {
                                        best.v1.y = t3.v1.y;
                                    }
                                    if (t3.v2.y > best.v2.y) {
                                        best.v2.y = t3.v2.y;
                                    }
                                    newCheck.add(t3);
                                    verplant.put(t3, Boolean.FALSE);
                                }
                            }
                        }
                        toCheck = new ArrayList();
                        toCheck.addAll(newCheck);
                    }
                    ergebnis.add(best);
                }
            }
            map.put(e.getKey(), ergebnis);
        }
        return map;
    }

    public static ArrayList<Rect> ultimate_field(BufferedImage img) {
        ArrayList<Rect> result = new ArrayList();
        BufferedImage clone = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int x = 0; x < clone.getWidth(); x++) {
            for (int y = 0; y < clone.getHeight(); y++) {
                clone.setRGB(x, y, img.getRGB(x, y));
            }
        }
        while (true) {
            Rect best_rect = new Rect(0, 0, 0, 0);
            int flaeche = 0;
            ArrayList<Rect> rects = field(clone);
            for (Rect r : rects) {
                int f = r.h * r.w;
                if (f > flaeche) {
                    best_rect = new Rect(r.x, r.y, r.w, r.h);
                    flaeche = f;
                }
            }
            if (best_rect.w == 0) {
                break;
            }
            for (int x = best_rect.x; x < best_rect.w + best_rect.x; x++) {
                for (int y = best_rect.y; y < best_rect.h + best_rect.y; y++) {
                    clone.setRGB(x, y, new Color(0, 0, 0, 0).getRGB());
                }
            }
            result.add(best_rect);
        }
        return result;
    }

    public static ArrayList<Rect> field(BufferedImage img) {
        ArrayList<Rect> result = new ArrayList();
        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                Rect rect = new Rect(x, y, 1, 1);
                if (!rect.ok(img)) {
                    continue;
                }
                boolean xc = true;
                boolean yc = true;
                boolean xw = true;
                boolean yw = true;
                while (true) {

                    Rect newrect = new Rect(rect.x, rect.y, rect.w, rect.h);
                    if (xc) {
                        Rect newrect2 = new Rect(newrect.x, newrect.y, newrect.w, newrect.h);
                        newrect2.x--;
                        newrect2.w++;
                        if (!newrect2.ok(img)) {
                            xc = false;
                        } else {
                            newrect = new Rect(newrect2.x, newrect2.y, newrect2.w, newrect2.h);
                        }
                    }
                    if (yc) {
                        Rect newrect2 = new Rect(newrect.x, newrect.y, newrect.w, newrect.h);
                        newrect2.y--;
                        newrect2.h++;
                        if (!newrect2.ok(img)) {
                            yc = false;
                        } else {
                            newrect = new Rect(newrect2.x, newrect2.y, newrect2.w, newrect2.h);
                        }
                    }
                    if (xw) {
                        Rect newrect2 = new Rect(newrect.x, newrect.y, newrect.w, newrect.h);
                        newrect2.w++;
                        if (!newrect2.ok(img)) {
                            xw = false;
                        } else {
                            newrect = new Rect(newrect2.x, newrect2.y, newrect2.w, newrect2.h);
                        }
                    }
                    if (yw) {
                        Rect newrect2 = new Rect(newrect.x, newrect.y, newrect.w, newrect.h);
                        newrect2.h++;
                        if (!newrect2.ok(img)) {
                            yw = false;
                        } else {
                            newrect = new Rect(newrect2.x, newrect2.y, newrect2.w, newrect2.h);
                        }
                    }
                    if (!xc && !yc && !xw && !yw) {
                        break;
                    } else {
                        rect = new Rect(newrect.x, newrect.y, newrect.w, newrect.h);
                    }
                }
                result.add(rect);
            }
        }
        return result;
    }

    public static ArrayList<Point> border(BufferedImage img) {
        ArrayList<Point> r = new ArrayList();
        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                Color c = new Color(img.getRGB(x, y), true);
                if (c.getAlpha() != 0) {
                    if (x == 0 || y == 0 || x == img.getWidth() - 1 || y == img.getHeight() - 1) {
                        r.add(new Point(x, y));
                        continue;
                    }
                    ArrayList<Point> check = new ArrayList();
                    if (x != 0) {
                        check.add(new Point(x - 1, y));
                    }
                    if (y != 0) {
                        check.add(new Point(x, y - 1));
                    }
                    if (x != img.getWidth() - 1) {
                        check.add(new Point(x + 1, y));
                    }
                    if (y != img.getHeight() - 1) {
                        check.add(new Point(x, y + 1));
                    }
                    for (Point p : check) {
                        Color c2 = new Color(img.getRGB(p.x, p.y), true);
                        if (c2.getAlpha() == 0) {
                            r.add(new Point(x, y));
                            break;
                        }
                    }
                }
            }
        }
        return r;
    }

    public static ArrayList<Point> borderplus(BufferedImage img) {
        ArrayList<Point> r = new ArrayList();
        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                Color c = new Color(img.getRGB(x, y), true);
                if (c.getAlpha() != 0) {
                    if (x == 0 || y == 0 || x == img.getWidth() - 1 || y == img.getHeight() - 1) {
                        r.add(new Point(x, y));
                        continue;
                    }
                    ArrayList<Point> check = new ArrayList();
                    if (x != 0) {
                        check.add(new Point(x - 1, y));
                    }
                    if (y != 0) {
                        check.add(new Point(x, y - 1));
                    }
                    if (x != img.getWidth() - 1) {
                        check.add(new Point(x + 1, y));
                    }
                    if (y != img.getHeight() - 1) {
                        check.add(new Point(x, y + 1));
                    }
                    for (Point p : check) {
                        Color c2 = new Color(img.getRGB(p.x, p.y), true);
                        if (c2.getAlpha() == 0) {
                            r.add(new Point(p.x, p.y));
                            break;
                        }
                    }
                }
            }
        }
        return r;
    }
}