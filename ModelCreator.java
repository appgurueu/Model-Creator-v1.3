/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package appguru;

import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;

/**
 *
 * @author lars
 */
class fs implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        JFileChooser source = (JFileChooser) e.getSource();
        JFrame w = (JFrame) SwingUtilities.getRoot(source);
        w.dispatchEvent(new WindowEvent(w, WindowEvent.WINDOW_CLOSING));
        if (source.getSelectedFile() != null && source.getSelectedFile().exists()) {
            try {
                File visited_dir = source.getSelectedFile().getParentFile();
                if (visited_dir != null) {
                    ModelCreator.last_dir = visited_dir;
                }
                visited_dir = null;
                if (ModelCreator.im.isSelected()) {
                    BufferedImage loaded = ImageIO.read(source.getSelectedFile());
                    if (ModelCreator.reflect.isSelected()) {
                        ModelCreator.canvas.setImage(loaded, "image", "preview");
                        ModelCreator.canvas.setImage(loaded, "image_2", "preview_2");
                    } else if (ModelCreator.one.isSelected()) {
                        ModelCreator.canvas.setImage(loaded, "image", "preview");
                    } else {
                        ModelCreator.canvas.setImage(loaded, "image_2", "preview_2");
                    }
                } else {
                    BufferedImage loaded = ImageIO.read(source.getSelectedFile());
                    if (ModelCreator.reflect.isSelected()) {
                        ModelCreator.canvas.setImage(loaded, "depthmap", "depth_preview");
                        ModelCreator.canvas.setImage(loaded, "depthmap_2", "depth_preview_2");
                    } else if (ModelCreator.one.isSelected()) {
                        ModelCreator.canvas.setImage(loaded, "depthmap", "depth_preview");
                    } else {
                        ModelCreator.canvas.setImage(loaded, "depthmap_2", "depth_preview_2");
                    }
                }
            } catch (Exception ex) {
                System.err.println(ex);
                JOptionPane.showMessageDialog(new JFrame(),
                        "Error ! Invalid Input !",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

}

class fs2 implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        JFileChooser source = (JFileChooser) e.getSource();
        JFrame w = (JFrame) SwingUtilities.getRoot(source);
        w.dispatchEvent(new WindowEvent(w, WindowEvent.WINDOW_CLOSING));
        if (source.getSelectedFile() != null && source.getSelectedFile().exists()) {
            ModelCreator.savedir = source.getSelectedFile();
            try {
                ModelCreator.saveat.setText(source.getSelectedFile().getCanonicalPath());
            } catch (IOException ex) {
                System.err.println(ex);
                JOptionPane.showMessageDialog(new JFrame(),
                        "Error ! Invalid Input !",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

}

class cop implements ActionListener {

    public static String DIRECTION = "Z";
    public static float SCALE = 16f;
    public static boolean INVERT = false;
    public static boolean TEXCOORDS = true;

    @Override
    public void actionPerformed(ActionEvent e) {
        Thread t = new Thread() {
            @Override
            public synchronized void run() {
                try {
                    Mesh.NODEBOX_MODE=0;
                    if (ModelCreator.NODEBOX_MODE.equals("Simple")) {
                        Mesh.NODEBOX_MODE=1;
                    }
                    else if (ModelCreator.NODEBOX_MODE.equals("Accurate")) {
                        Mesh.NODEBOX_MODE=2;
                    }
                    BufferedImage dmap = new BufferedImage(ModelCreator.canvas.depthmap.getWidth(), ModelCreator.canvas.depthmap.getWidth(), BufferedImage.TYPE_INT_ARGB);
                    dmap.createGraphics().drawImage(ModelCreator.canvas.depthmap, 0, 0, null);
                    ArrayList<Point> plus = new ArrayList();
                    BufferedImage dmap_2 = new BufferedImage(ModelCreator.canvas.depthmap_2.getWidth(), ModelCreator.canvas.depthmap_2.getWidth(), BufferedImage.TYPE_INT_ARGB);
                    dmap_2.createGraphics().drawImage(ModelCreator.canvas.depthmap_2, 0, 0, null);
                    ArrayList<Point> plus_2 = new ArrayList();
                    if (ModelCreator.close_the_shape) {
                        plus = Processer.borderplus(ModelCreator.canvas.image);
                        plus_2 = Processer.borderplus(ModelCreator.canvas.image_2);
                    }
                    for (Point p : plus) {
                        dmap.setRGB(p.x, p.y, Color.BLACK.getRGB());
                    }
                    for (Point p : plus_2) {
                        dmap_2.setRGB(p.x, p.y, Color.BLACK.getRGB());
                    }
                    //HelperTool.canvas.punkte.addAll(Processer.border(Processer.border(HelperTool.canvas.imgs), HelperTool.canvas.imgs));
                    ArrayList<Tuple<Float, BufferedImage>> sections = Processer.sections(ModelCreator.canvas.image, dmap);
                    ArrayList<Tuple<Float, BufferedImage>> sections2 = Processer.sections(ModelCreator.canvas.image_2, dmap_2);
                    int total = (int) ((sections.size() + sections2.size()) * 2.0f);
                    int th = 0;
                    ArrayList<Mesh> gosh = new ArrayList();
                    for (Tuple<Float, BufferedImage> i : sections) {
                        th++;
                        ArrayList<Rect> rects = Processer.ultimate_field(i.v2);
                        Mesh m = new Mesh(rects, new int[]{ModelCreator.canvas.image.getWidth(), ModelCreator.canvas.image.getHeight()}, new float[]{0, 0, 1.0f}, i.v1 * SCALE);
                        Mesh m2 = Mesh.border(Processer.border(Processer.border(i.v2), ModelCreator.canvas.image, i.v2, dmap, true), new int[]{ModelCreator.canvas.image.getWidth(), ModelCreator.canvas.image.getHeight()}, SCALE);
                        gosh.add(m);
                        gosh.add(m2);
                        int percent = (int) ((float) th / total * 100.0f);
                        ModelCreator.progress.setString(Integer.toString(percent) + " % - Creating Model");
                        ModelCreator.progress.setValue(percent);
                        System.gc();
                    }
                    BufferedImage plane = Processer.nand(ModelCreator.canvas.image, ModelCreator.canvas.image_2);
                    if (plane != null) {
                        gosh.add(new Mesh(Processer.ultimate_field(plane), new int[]{ModelCreator.canvas.image.getWidth(), ModelCreator.canvas.image.getHeight()}, new float[]{0, 0, -1f}, 0));
                    }
                    ArrayList<Mesh> g = new ArrayList();
                    Mesh meshy=Mesh.concatenate(gosh);
                    if (!ModelCreator.SAME_TEXCOORDS) {
                        if (ModelCreator.AUFRUNDEN) {
                            meshy.transformUV(0.5f, 0.5f, 0.0f, 0.0f);
                        }
                        else {
                            if (ModelCreator.VERTICAL) {
                                meshy.transformUV(0.5f, 1f, 0.0f, 0.0f);
                            }
                            else {
                                meshy.transformUV(1f, 0.5f, 0.0f, 0.0f);
                            }
                        }
                    }
                    g.add(meshy);
                    meshy=null;
                    //Mesh m3 = Mesh.border(Processer.border(Processer.border(ModelCreator.canvas.image), ModelCreator.canvas.image, ModelCreator.canvas.image, ModelCreator.canvas.depthmap, false), new int[]{ModelCreator.canvas.image.getWidth(), ModelCreator.canvas.image.getHeight()}, SCALE);
                    //Mesh m4 = Mesh.invert(Mesh.border(Processer.border(Processer.border(ModelCreator.canvas.image_2), ModelCreator.canvas.image_2, ModelCreator.canvas.image_2, ModelCreator.canvas.depthmap_2, false), new int[]{ModelCreator.canvas.image_2.getWidth(), ModelCreator.canvas.image_2.getHeight()}, SCALE), false, false, true);
                    //g.add(m3);
                    //g.add(m4);
                    gosh = new ArrayList();
                    for (Tuple<Float, BufferedImage> i : sections2) {
                        th++;
                        ArrayList<Rect> rects = Processer.ultimate_field(i.v2);
                        Mesh m = new Mesh(rects, new int[]{ModelCreator.canvas.image_2.getWidth(), ModelCreator.canvas.image_2.getHeight()}, new float[]{0, 0, 1.0f}, i.v1 * SCALE);
                        Mesh m2 = Mesh.border(Processer.border(Processer.border(i.v2), ModelCreator.canvas.image_2, i.v2, dmap_2, true), new int[]{ModelCreator.canvas.image_2.getWidth(), ModelCreator.canvas.image_2.getHeight()}, SCALE);
                        gosh.add(m);
                        gosh.add(m2);
                        int percent = (int) ((float) th / total * 100.0f);
                        ModelCreator.progress.setString(Integer.toString(percent) + " % - Creating Model");
                        if (th == sections.size() + sections2.size() - 1) {
                            ModelCreator.progress.setString(Integer.toString(percent) + " % - Optimizing Model");
                        }
                        ModelCreator.progress.setValue(percent);
                        System.gc();
                    }
                    Mesh buffer=Mesh.invert(Mesh.concatenate(gosh), false, false, true);
                    gosh=new ArrayList();
                    gosh.add(buffer);
                    buffer=null;
                    plane = Processer.nand(ModelCreator.canvas.image_2, ModelCreator.canvas.image);
                    if (plane != null) {
                        gosh.add(new Mesh(Processer.ultimate_field(plane), new int[]{ModelCreator.canvas.image_2.getWidth(), ModelCreator.canvas.image_2.getHeight()}, new float[]{0, 0, 1f}, 0));
                    }
                    meshy=Mesh.concatenate(gosh);
                    if (!ModelCreator.SAME_TEXCOORDS) {
                        if (ModelCreator.AUFRUNDEN) {
                            if (ModelCreator.VERTICAL) {
                                meshy.transformUV(0.5f, 0.5f, 0.0f, 0.5f);
                            }
                            else {
                                meshy.transformUV(0.5f, 0.5f, 0.5f, 0.0f);
                            }
                        }
                        else {
                            if (ModelCreator.VERTICAL) {
                                meshy.transformUV(0.5f, 1f, 0.0f, 0.5f);
                            }
                            else {
                                meshy.transformUV(1f, 0.5f, 0.5f, 0.0f);
                            }
                        }
                    }
                    g.add(meshy);
                    meshy=null;
                    //Mesh m2=new Mesh(Processer.border(Processer.border(HelperTool.canvas.image),HelperTool.canvas.image));
                    /*HelperTool.canvas.rects.addAll(Processer.ultimate_field(HelperTool.canvas.image));
                    Mesh m = new Mesh(HelperTool.canvas.rects, new int[]{HelperTool.canvas.image.getWidth() / 4, HelperTool.canvas.image.getHeight() / 4}, new float[]{0, 0, 1.0f}, 0.5f);
                    Mesh m2 = new Mesh(HelperTool.canvas.rects, new int[]{HelperTool.canvas.image.getWidth() / 4, HelperTool.canvas.image.getHeight() / 4}, new float[]{0, 0, -1.0f}, -0.5f);*/
                    //Mesh m3 = new Mesh(HelperTool.canvas.punkte, new int[]{HelperTool.canvas.image.getWidth() / 4, HelperTool.canvas.image.getHeight() / 4});
                    //gosh.add(m);
                    //gosh.add(m2);
                    //gosh.add(m3);
                    Mesh mesh = Mesh.concatenate(g);
                    mesh = Mesh.optimize(mesh);
                    ModelCreator.progress.setValue(75);
                    ModelCreator.progress.setString("75 % - Saving Model");
                    g = null;
                    System.gc();
                    if (INVERT) {
                        mesh.invertNormals();
                    }
                    if (DIRECTION.equals("Y")) {
                        mesh.align((byte) 1);
                    } else if (DIRECTION.equals("X")) {
                        mesh.align((byte) 0);
                    }
                    File f = new File(ModelCreator.savedir.getCanonicalPath() + File.separator + ModelCreator.savename.getText() + ".obj");
                    f.createNewFile();
                    BufferedWriter w = new BufferedWriter(new FileWriter(f));
                    w.write(mesh.toString());
                    if (!ModelCreator.NODEBOX_MODE.equals("None")) {
                        File f2 = new File(ModelCreator.savedir.getCanonicalPath() + File.separator + ModelCreator.savename.getText() + ".lua");
                        f2.createNewFile();
                        w = new BufferedWriter(new FileWriter(f2));
                        w.write(mesh.nodeboxesString());
                    }
                    ModelCreator.progress.setValue(100);
                    ModelCreator.progress.setString("100 %");
                    w.close();
                    System.gc();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(new JFrame(),
                            "Error ! Invalid Input ! OBJ not created !",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);

                }
            }
        };
        t.start();
        ModelCreator.progress.setString("0 %");
    }

}

class sf implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        JFrame window2 = new JFrame();
        window2.setTitle("Select image");
        window2.setVisible(true);
        window2.setSize(400, 400);
        JFileChooser f = new JFileChooser();
        f.setCurrentDirectory(ModelCreator.last_dir);
        f.addActionListener(new fs());
        window2.add(f);
    }

}

class sf2 implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        JFrame window2 = new JFrame();
        window2.setTitle("Select directory to save in");
        window2.setVisible(true);
        window2.setSize(400, 400);
        JFileChooser f = new JFileChooser();
        f.setCurrentDirectory(new File(""));
        f.addActionListener(new fs2());
        f.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        window2.add(f);
    }

}

class hl implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        JOptionPane.showMessageDialog(new JFrame(),
                "OBJ Creator v1.2 © Lars Müller @appguru.eu\n"
                + "A simple tool to create OBJs\n"
                + "Use it the following : \n"
                + "  - create an image with alphas\n"
                + "  - if requested, create a depthmap(greyscale)\n"
                + "  - do this for both sides, if you wish to do so\n"
                + "Viewing Field - top : frontside | bottom : backside | always : image left, depthmap right\n"
                + "Extras - Opens a popup : \n"
                + "  - Alignment - rotation of the model, swaps coordinates\n"
                + "  - Invert normals - whether model should face outside or inside, inverts the normals\n"
                + "  - Closed - whether the resulting shape should be closed\n"
                + "  - Cancel - cancel, close popup\n"
                + "  - Submit - submit values, closes popup\n"
                + "Scale - Opens a popup : \n"
                + "  - Depthscale - size in depthmap direction, scale of the depth\n"
                + "  - Cancel - cancel, close popup\n"
                + "  - Submit - submit values, closes popup\n"
                + "Select image - select an image\n"
                + "Reflect sides - load image/depthmap for both sides\n"
                + "Image/Depthmap - is an image or depthmap being selected for the side\n"
                + "Frontside/Backside - is front/backside of the model being edited\n"
                + "Save as - savename, \".obj\" is appended automatically\n"
                + "Save in - folder to save OBJ in\n"
                + "Select folder - select the folder to save in\n"
                + "Progress - OBJ creation progress\n"
                + "Help - this help popup\n"
                + "Post-editing is highly recommended, especially for scaling and optimizing the model",
                "Help",
                JOptionPane.INFORMATION_MESSAGE);
    }

}

public class ModelCreator {

    public static boolean SAME_TEXCOORDS = true;
    public static boolean VERTICAL = false;
    public static boolean AUFRUNDEN = false;
    public static String NODEBOX_MODE = "None";
    public static File last_dir = new File("");
    public static Canvas canvas;
    public static JFrame window;
    public static JLabel path;
    public static File savedir;
    public static JPanel pane;
    public static JLabel saveat;
    public static JTextField savename;
    public static JProgressBar progress;
    public static JRadioButton one;
    public static JRadioButton two;
    public static JRadioButton im;
    public static JRadioButton dmap;
    public static JCheckBox reflect;
    public static JCheckBox save_nodeboxes;
    public static boolean close_the_shape = true;

    public static void main(String[] args) throws IOException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        window = new JFrame();
        window.setTitle("OBJ Creator v1.2");
        window.setVisible(true);
        window.setSize(800, 350);
        //JFileChooser f=new JFileChooser();
        //f.addActionListener(l);
        dmap = new JRadioButton("Depthmap");
        im = new JRadioButton("Image");
        im.setSelected(true);
        ButtonGroup imagetype = new ButtonGroup();
        imagetype.add(dmap);
        imagetype.add(im);
        one = new JRadioButton("Frontside");
        two = new JRadioButton("Backside");
        one.setSelected(true);
        ButtonGroup numbertype = new ButtonGroup();
        numbertype.add(one);
        numbertype.add(two);
        JButton select_file = new JButton("Select image");
        select_file.addActionListener(new sf());
        /*JButton select_file2 = new JButton("Select image");
        select_file2.addActionListener(new sf());
        JButton select_dmap1 = new JButton("Select depth image 1");
        select_dmap1.addActionListener(new sf3());
        JButton select_dmap2 = new JButton("Select depth image 2");
        select_dmap2.addActionListener(new sf3());*/
        JButton create_objs = new JButton("Create OBJ");
        create_objs.addActionListener(new cop());
        JButton help = new JButton("Help");
        help.addActionListener(new hl());
        savename = new JTextField("");
        savename.setText("savename");
        JLabel saveas = new JLabel("Save as : ");
        path = new JLabel();
        JLabel saveat1 = new JLabel("Save in : ");
        savedir = new File("");
        saveat = new JLabel(savedir.getCanonicalPath());
        path = new JLabel();
        JButton select_dir = new JButton("Select directory");
        select_dir.addActionListener(new sf2());
        JLabel creator = new JLabel("© Lars Müller @appguru.eu");
        progress = new JProgressBar();
        progress.setString("0 %");
        progress.setStringPainted(true);
        //button.setSize(40,20);
        FlowLayout flow = new FlowLayout();
        flow.setAlignment(FlowLayout.RIGHT);
        pane = new JPanel();
        GridLayout grid = new GridLayout();
        grid.setRows(9);
        grid.setColumns(2);
        JButton direction = new JButton("Extras");
        direction.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame dialog = new JFrame("Extras");
                dialog.setVisible(true);
                dialog.setSize(350, 150);
                dialog.setLayout(new GridLayout(7, 2));
                dialog.setResizable(true);
                JButton submit = new JButton("Submit");
                JComboBox direction_selection = new JComboBox(new String[]{"X", "Y", "Z"});
                direction_selection.setSelectedItem(cop.DIRECTION);
                JCheckBox invert_normals = new JCheckBox("Invert Normals");
                JCheckBox closed_shape = new JCheckBox("Closed Shape");
                closed_shape.setSelected(true);
                invert_normals.setSelected(cop.INVERT);
                JSpinner size_selection = new JSpinner(new SpinnerNumberModel(cop.SCALE, 0f, 1000, 0.1f));
                JCheckBox same_texcoords = new JCheckBox("Same textures");
                JCheckBox aufrunden = new JCheckBox("1:1 texture ratio");
                JRadioButton vertical = new JRadioButton("Vertical");
                JRadioButton horizontal = new JRadioButton("Horizontal");
                ButtonGroup align = new ButtonGroup();
                align.add(vertical);
                align.add(horizontal);
                vertical.setSelected(VERTICAL);
                horizontal.setSelected(!VERTICAL);
                same_texcoords.setSelected(SAME_TEXCOORDS);
                JComboBox nodebox_mode = new JComboBox(new String[]{"None", "Simple", "Accurate"});
                nodebox_mode.setSelectedItem(NODEBOX_MODE);
                submit.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        VERTICAL = vertical.isSelected();
                        NODEBOX_MODE = (String) nodebox_mode.getSelectedItem();
                        cop.SCALE = (float) (double) size_selection.getValue();
                        cop.DIRECTION = (String) direction_selection.getSelectedItem();
                        cop.INVERT = invert_normals.isSelected();
                        close_the_shape = closed_shape.isSelected();
                        SAME_TEXCOORDS = same_texcoords.isSelected();
                        VERTICAL = vertical.isSelected();
                        AUFRUNDEN = aufrunden.isSelected();
                        dialog.dispatchEvent(new WindowEvent(dialog, WindowEvent.WINDOW_CLOSING));
                    }
                });
                JButton cancel = new JButton("Cancel");
                cancel.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        dialog.dispatchEvent(new WindowEvent(dialog, WindowEvent.WINDOW_CLOSING));
                    }
                });
                dialog.add(new JLabel("Alignment : "));
                dialog.add(direction_selection);
                dialog.add(invert_normals);
                dialog.add(closed_shape);
                dialog.add(new JLabel("Depthscale : "));
                dialog.add(size_selection);
                dialog.add(same_texcoords);
                dialog.add(aufrunden);
                dialog.add(vertical);
                dialog.add(horizontal);
                dialog.add(new JLabel("Nodeboxes : "));
                dialog.add(nodebox_mode);
                dialog.add(cancel);
                dialog.add(submit);
            }
        });
        save_nodeboxes = new JCheckBox("Save nodeboxes");
        save_nodeboxes.setSelected(false);
        pane.add(direction);
        pane.add(save_nodeboxes);
        pane.setLayout(grid);
        pane.add(select_file);
        reflect = new JCheckBox("Reflect sides");
        reflect.setSelected(true);
        pane.add(reflect);
        pane.add(im);
        pane.add(dmap);
        pane.add(one);
        pane.add(two);
        /*pane.add(select_file2);
        pane.add(select_dmap1);
        pane.add(select_dmap2);*/
        pane.add(saveas);
        pane.add(savename);
        pane.add(saveat1);
        pane.add(saveat);
        pane.add(create_objs);
        //pane.add(creator);
        pane.add(select_dir);
        pane.add(new JLabel("Progress : "));
        pane.add(progress);
        pane.add(help);
        pane.add(creator);
        //window.add(canvas);
        canvas = new Canvas();
        canvas.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        canvas.setFocusable(true);
        //canvas.setAlignmentX(0);
        //canvas.setAlignmentY(0);
        window.getContentPane().add(canvas);
        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent c) {
                System.out.println("Application exited !");
                System.exit(0);
            }
        });
        //window.add(pane);
        //pane.add(canvas);
        canvas.add(pane);
        canvas.setLayout(flow);
        window.revalidate();
        //window.add(canvas);
        long then = System.currentTimeMillis();
        while (true) {
            if (System.currentTimeMillis() - then > 50) {
                window.repaint();
                //pane.repaint();
                then = System.currentTimeMillis();
                System.gc();
            }
        }
        //window.getContentPane().add(button);
        // TODO code application logic here
    }
}
