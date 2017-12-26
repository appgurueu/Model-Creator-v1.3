/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package appguru;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * @author lars
 */
class Mesh {

    public static byte NODEBOX_MODE = 1;
    public float[] nodeboxes;
    public float[] uvs;
    public float[] normals;
    public float[] vertices;
    public int[] indices;
    public int[] uvindices;
    public int[] normalindices;
    public static final float[] NORMALS = new float[]{0, -1, 0,
        0, 1, 0,
        -1, 0, 0,
        1, 0, 0};

    public Mesh() {

    }

    public Mesh(ArrayList<Rect> rects, int[] dim, float[] normal, float z) {
        float dimx = dim[0] / 2.0f;
        float dimy = dim[1] / 2.0f;
        int size = 4 * rects.size();
        int[] indices2 = {1, 2, 3,
            1, 3, 4};
        int[] ni = {1, 1, 1,
            1, 1, 1};
        if (NODEBOX_MODE != 0) {
            nodeboxes = new float[rects.size() * 6];
        }
        vertices = new float[3 * size];
        uvs = new float[2 * size];
        normals = new float[3];
        System.arraycopy(normal, 0, normals, 0, 3);
        indices = new int[6 * rects.size()];
        uvindices = new int[6 * rects.size()];
        normalindices = new int[6 * rects.size()];
        for (int i = 0; i < normalindices.length; i++) {
            normalindices[i] = 1;
        }
        float nwx;
        float nwy;
        float ewx;
        float ewy;
        float mwx;
        float mwy;
        float pwx;
        float pwy;
        int nbi;
        int position;
        float[] vertices2;
        float[] texcoords2;
        for (int r = 0; r < rects.size(); r++) {
            Rect current = rects.get(r);
            nwx = 1.0f - (current.x / (float) dim[0]);
            nwy = 1.0f - (current.y / (float) dim[1]);
            ewx = 1.0f - ((current.x + current.w) / (float) dim[0]);
            ewy = 1.0f - ((current.y + current.h) / (float) dim[1]);
            mwx = dimx - current.x;
            mwy = dimy - current.y;
            pwx = dimx - (current.x + current.w);
            pwy = dimy - (current.y + current.h);
            if (NODEBOX_MODE != 0) {
                nbi = r * 6;
                nodeboxes[nbi] = Math.min(mwx, pwx);
                nodeboxes[nbi + 1] = Math.min(mwy, pwy);
                nodeboxes[nbi + 2] = Math.min(0, z);
                nodeboxes[nbi + 3] = Math.max(pwx, mwx);
                nodeboxes[nbi + 4] = Math.max(pwy, mwy);
                nodeboxes[nbi + 5] = Math.max(z, 0);
                if (NODEBOX_MODE == 2) {
                    nodeboxes[nbi + 2] = z;
                    nodeboxes[nbi + 5] = z;
                }
            }
            position = r * 4;
            vertices2 = new float[]{
                mwx, pwy, z,
                mwx, mwy, z,
                pwx, mwy, z,
                pwx, pwy, z};
            texcoords2 = new float[]{
                nwx, ewy,
                nwx, nwy,
                ewx, nwy,
                ewx, ewy};
            System.arraycopy(vertices2, 0, vertices, position * 3, vertices2.length);
            System.arraycopy(texcoords2, 0, uvs, position * 2, texcoords2.length);
            System.arraycopy(indices2, 0, indices, r * 6, 6);
            System.arraycopy(indices2, 0, uvindices, r * 6, 6);
            for (int i = 0; i < 6; i++) {
                indices2[i] += 4;
            }
        }
    }

    /*public Mesh(ArrayList<Tuple<Point, Point>> rects, int[] dim) {
        float dimx = dim[0] / 2.0f;
        float dimy = dim[1] / 2.0f;
        int size = 4 * rects.size();
        int[] indices2 = {1, 2, 3,
            2, 3, 4};
        vertices = new float[3 * size];
        uvs = new float[2 * size];
        normals = new float[3 * size];
        indices = new int[6 * rects.size()];
        uvindices = new int[6 * rects.size()];
        normalindices = new int[6 * rects.size()];
        int normal;
        for (int r = 0; r < rects.size(); r++) {
            Tuple<Point, Point> current = rects.get(r);
            float nwx = 1.0f - (current.v1.x + ((current.v1.x % 2) * 2) / ((float) dim[0] * 2));
            float nwy = 1.0f - (current.v1.y + ((current.v1.y % 2) * 2) / ((float) dim[1] * 2));
            float ewx = 1.0f - (current.v2.x + ((current.v2.x % 2) * 2) / ((float) dim[0] * 2));
            float ewy = 1.0f - (current.v2.y + ((current.v2.y % 2) * 2) / ((float) dim[1] * 2));
            float mwx = dimx - (current.v1.x / 2 + current.v1.x % 2);
            float mwy = dimy - (current.v1.y / 2 + current.v1.y % 2);
            float pwx = dimx - (current.v2.x / 2 + current.v2.x % 2);
            float pwy = dimy - (current.v2.y / 2 + current.v2.y % 2);
            int position = r * 4;
            float[] vertices2 = {
                mwx, mwy, 0.5f,
                pwx, pwy, 0.5f,
                mwx, mwy, -0.5f,
                pwx, pwy, -0.5f};

            float[] texcoords2 = {
                ewx, ewy,
                ewx, nwx,
                nwx, nwy,
                ewx, nwy};
            if (current.v1.x != current.v2.x) {
                if (current.v2.y % 2 == 1) {
                    normal = 1;
                } else {
                    normal = 2;
                }

            } else if (current.v2.x % 2 == 1) {
                normal = 3;
            } else {
                normal = 4;
            }
            System.arraycopy(vertices2, 0, vertices, position * 3, vertices2.length);
            System.arraycopy(texcoords2, 0, uvs, position * 2, texcoords2.length);
            System.arraycopy(indices2, 0, indices, r * 6, 6);
            System.arraycopy(indices2, 0, uvindices, r * 6, 6);
            for (int i = 0; i < 6; i++) {
                normalindices[r * 6 + i] = normal;
            }
            for (int i = 0; i < 6; i++) {
                indices2[i] += 4;
            }
        }
    }*/
    public static Mesh border(HashMap<Tuple<Float, Float>, ArrayList<Tuple<Point, Point>>> input, int[] dim, float scale) {
        ArrayList<Mesh> result = new ArrayList();
        for (Map.Entry<Tuple<Float, Float>, ArrayList<Tuple<Point, Point>>> e : input.entrySet()) {
            Mesh m = new Mesh();
            ArrayList<Tuple<Point, Point>> rects = e.getValue();
            float dimx = dim[0] / 2.0f;
            float dimy = dim[1] / 2.0f;
            int size = 4 * rects.size();
            int[] INDICES = {1, 2, 3,
                2, 3, 4};
            if (NODEBOX_MODE == 2) {
                m.nodeboxes = new float[rects.size() * 6];
            }
            m.vertices = new float[3 * size];
            m.uvs = new float[2 * size];
            m.normals = new float[12];
            System.arraycopy(NORMALS, 0, m.normals, 0, 12);
            m.indices = new int[6 * rects.size()];
            m.uvindices = new int[6 * rects.size()];
            m.normalindices = new int[6 * rects.size()];
            int normal, nbi, position;
            float nwx, nwy, ewx, ewy, mwx, mwy, pwx, pwy, zmin, zmax;
            float[] vertices, texcoords;
            for (int r = 0; r < rects.size(); r++) {
                Tuple<Point, Point> current = rects.get(r);
                nwx = 1.0f - (current.v1.x / 2 / ((float) dim[0]));
                nwy = 1.0f - (current.v1.y / 2 / ((float) dim[1]));
                ewx = 1.0f - ((current.v2.x / 2 + 1) / ((float) dim[0]));
                ewy = 1.0f - ((current.v2.y / 2 + 1) / ((float) dim[1]));
                mwx = dimx - (current.v1.x / 2 + current.v1.x % 2);
                mwy = dimy - (current.v1.y / 2 + current.v1.y % 2);
                pwx = dimx - (current.v2.x / 2 + current.v2.x % 2);
                pwy = dimy - (current.v2.y / 2 + current.v2.y % 2);
                zmin = e.getKey().v1 * scale;
                zmax = e.getKey().v2 * scale;
                if (NODEBOX_MODE == 2) {
                    nbi = r * 6;
                    m.nodeboxes[nbi] = Math.min(mwx, pwx);
                    m.nodeboxes[nbi + 1] = Math.min(mwy, pwy);
                    m.nodeboxes[nbi + 2] = Math.min(zmin, zmax);
                    m.nodeboxes[nbi + 3] = Math.max(mwx, pwx);
                    m.nodeboxes[nbi + 4] = Math.max(pwy, mwy);
                    m.nodeboxes[nbi + 5] = Math.max(zmax, zmin);
                }
                position = r * 4;
                vertices = new float[]{
                    mwx, mwy, e.getKey().v1 * scale,
                    pwx, pwy, e.getKey().v1 * scale,
                    mwx, mwy, e.getKey().v2 * scale,
                    pwx, pwy, e.getKey().v2 * scale};
                if (current.v1.x == current.v2.x) {
                    texcoords = new float[]{
                        nwx, nwy,
                        ewx, ewy,
                        ewx, nwy,
                        nwx, ewy
                    };
                } else {
                    texcoords = new float[]{
                        nwx, nwy,
                        ewx, nwy,
                        nwx, ewy,
                        ewx, ewy
                    };
                }
                if (current.v1.x != current.v2.x) {
                    if (current.v2.y % 2 == 1) {
                        normal = 1;
                    } else {
                        normal = 2;
                    }

                } else if (current.v2.x % 2 == 1) {
                    normal = 3;
                } else {
                    normal = 4;
                }
                System.arraycopy(vertices, 0, m.vertices, position * 3, 12);
                System.arraycopy(INDICES, 0, m.uvindices, r * 6, 6);
                System.arraycopy(texcoords, 0, m.uvs, position * 2, 8);
                System.arraycopy(INDICES, 0, m.indices, r * 6, 6);
                for (int i = 0; i < 6; i++) {
                    m.normalindices[r * 6 + i] = normal;
                }
                for (int i = 0; i < 6; i++) {
                    INDICES[i] += 4;
                }
            }
            result.add(m);
        }

        return Mesh.concatenate(result);
    }

    public void invertNormals() {
        for (int i = 0; i < normals.length; i++) {
            normals[i] = -normals[i];
        }
    }

    public void align(byte swap) {
        for (int i = 0; i < vertices.length / 3; i++) {
            float puffer = vertices[i * 3 + swap];
            vertices[i * 3 + swap] = vertices[i * 3 + 2];
            vertices[i * 3 + 2] = puffer;
        }
        if (nodeboxes != null) {
            for (int i = 0; i < nodeboxes.length / 3; i++) {
                float puffer = nodeboxes[i * 3 + swap];
                nodeboxes[i * 3 + swap] = nodeboxes[i * 3 + 2];
                nodeboxes[i * 3 + 2] = puffer;
            }
        }
        for (int i = 0; i < normals.length / 3; i++) {
            float puffer = normals[i * 3 + swap];
            normals[i * 3 + swap] = normals[i * 3 + 2];
            normals[i * 3 + 2] = puffer;
        }
    }

    public void transformUV(float scalex, float scaley, float translatex, float translatey) {
        for (int i = 0; i < uvs.length / 2; i++) {
            uvs[i] = uvs[i] * scalex + translatex;
            uvs[i + 1] = uvs[i + 1] * scaley + translatey;
        }
    }

    public static Mesh concatenate(ArrayList<Mesh> meshes) {
        Mesh result = new Mesh();
        int t_alloc = 0;
        int n_alloc = 0;
        int v_alloc = 0;
        int i_alloc = 0;
        int box_alloc = 0;
        for (Mesh m : meshes) {
            t_alloc += m.uvs.length;
            n_alloc += m.normals.length;
            v_alloc += m.vertices.length;
            i_alloc += m.indices.length;
            if (m.nodeboxes != null) {
                box_alloc += m.nodeboxes.length;
            }
        }
        result.uvs = new float[t_alloc];
        result.normals = new float[n_alloc];
        result.vertices = new float[v_alloc];
        result.indices = new int[i_alloc];
        result.uvindices = new int[i_alloc];
        result.normalindices = new int[i_alloc];
        result.nodeboxes = new float[box_alloc];
        t_alloc = 0;
        n_alloc = 0;
        v_alloc = 0;
        i_alloc = 0;
        box_alloc = 0;
        for (Mesh m : meshes) {
            System.arraycopy(m.uvs, 0, result.uvs, t_alloc, m.uvs.length);
            System.arraycopy(m.normals, 0, result.normals, n_alloc, m.normals.length);
            System.arraycopy(m.vertices, 0, result.vertices, v_alloc, m.vertices.length);
            if (m.nodeboxes != null) {
                System.arraycopy(m.nodeboxes, 0, result.nodeboxes, box_alloc, m.nodeboxes.length);
                box_alloc+=m.nodeboxes.length;
            }
            int ipos = v_alloc / 3;
            int npos = n_alloc / 3;
            for (int i = i_alloc; i < i_alloc + m.indices.length; i++) {
                result.indices[i] = m.indices[i - i_alloc] + ipos;
                result.uvindices[i] = m.uvindices[i - i_alloc] + ipos;
                result.normalindices[i] = m.normalindices[i - i_alloc] + npos;
            }
            t_alloc += m.uvs.length;
            n_alloc += m.normals.length;
            v_alloc += m.vertices.length;
            i_alloc += m.indices.length;
        }
        return result;
    }

    @Override
    public String toString() {
        String s = "# Created with Model Creator v1.2.1 © Lars Müller @appguru.eu";
        for (int i = 0; i < vertices.length / 3; i++) {
            int pos = i * 3;
            s += "\nv " + Float.toString(vertices[pos]) + " " + Float.toString(vertices[pos + 1]) + " " + Float.toString(vertices[pos + 2]);
        }
        for (int i = 0; i < uvs.length / 2; i++) {
            int pos = i * 2;
            s += "\nvt " + Float.toString(uvs[pos]) + " " + Float.toString(uvs[pos + 1]);
        }
        for (int i = 0; i < normals.length / 3; i++) {
            int pos = i * 3;
            s += "\nvn " + Float.toString(normals[pos]) + " " + Float.toString(normals[pos + 1]) + " " + Float.toString(normals[pos + 2]);
        }
        //s+="\ns 1";
        for (int i = 0; i < indices.length / 3; i++) {
            int pos = i * 3;
            String s1 = Integer.toString(indices[pos]);
            String s2 = Integer.toString(indices[pos + 1]);
            String s3 = Integer.toString(indices[pos + 2]);
            String us1 = Integer.toString(uvindices[pos]);
            String us2 = Integer.toString(uvindices[pos + 1]);
            String us3 = Integer.toString(uvindices[pos + 2]);
            String ns1 = Integer.toString(normalindices[pos]);
            String ns2 = Integer.toString(normalindices[pos + 1]);
            String ns3 = Integer.toString(normalindices[pos + 2]);
            String w = "\nf " + s1 + "/" + us1 + "/" + ns1 + " " + s2 + "/" + us2 + "/" + ns2 + " " + s3 + "/" + us3 + "/" + ns3;
            s += w;
        }
        return s;
    }

    public static Mesh invert(Mesh m, boolean x, boolean y, boolean z) {
        Mesh savemesh = new Mesh();
        savemesh.uvs = new float[m.uvs.length];
        savemesh.normals = new float[m.normals.length];
        savemesh.vertices = new float[m.vertices.length];
        savemesh.indices = new int[m.indices.length];
        savemesh.uvindices = new int[m.indices.length];
        savemesh.normalindices = new int[m.indices.length];
        if (savemesh.nodeboxes != null) {
            savemesh.nodeboxes = new float[m.nodeboxes.length];
            System.arraycopy(m.nodeboxes, 0, savemesh.nodeboxes, 0, m.nodeboxes.length);
            for (int i = 0; i < m.nodeboxes.length / 3; i++) {
                if (x) {
                    savemesh.nodeboxes[i * 3] = -m.nodeboxes[i * 3];
                }
                if (y) {
                    savemesh.nodeboxes[i * 3 + 1] = -m.nodeboxes[i * 3 + 1];
                }
                if (z) {
                    savemesh.nodeboxes[i * 3 + 2] = -m.nodeboxes[i * 3 + 2];
                }
            }
        }
        System.arraycopy(m.uvs, 0, savemesh.uvs, 0, m.uvs.length);
        System.arraycopy(m.normals, 0, savemesh.normals, 0, m.normals.length);
        System.arraycopy(m.vertices, 0, savemesh.vertices, 0, m.vertices.length);
        System.arraycopy(m.indices, 0, savemesh.indices, 0, m.indices.length);
        System.arraycopy(m.uvindices, 0, savemesh.uvindices, 0, m.indices.length);
        System.arraycopy(m.normalindices, 0, savemesh.normalindices, 0, m.indices.length);
        for (int i = 0; i < m.vertices.length / 3; i++) {
            if (x) {
                savemesh.vertices[i * 3] = -m.vertices[i * 3];
            }
            if (y) {
                savemesh.vertices[i * 3 + 1] = -m.vertices[i * 3 + 1];
            }
            if (z) {
                savemesh.vertices[i * 3 + 2] = -m.vertices[i * 3 + 2];
            }
        }
        for (int i = 0; i < m.normals.length / 3; i++) {
            if (x) {
                savemesh.normals[i * 3] = -m.normals[i * 3];
            }
            if (y) {
                savemesh.normals[i * 3 + 1] = -m.normals[i * 3 + 1];
            }
            if (z) {
                savemesh.normals[i * 3 + 2] = -m.normals[i * 3 + 2];
            }
        }
        return savemesh;
    }

    public static boolean[] equals(float[] f1, float[] f2, int si1, int si2, int elements) {
        boolean[] result = new boolean[elements];
        for (int i = 0; i < elements; i++) {
            result[i] = f1[si1 + i] == f2[si2 + i];
        }
        return result;
    }

    public String nodeboxesString() {
        String s = "-- Created with Model Creator v1.3 © Lars Müller @appguru.eu\nMODEL_NODEBOX={";
        int in;
        for (int i = 0; i < nodeboxes.length / 6; i++) {
            in = i * 6;
            s += "\n\t{";
            s += String.format("%s, %s, %s, %s, %s, %s", Float.toString(nodeboxes[in]), Float.toString(nodeboxes[in + 1]), Float.toString(nodeboxes[in + 2]), Float.toString(nodeboxes[in + 3]), Float.toString(nodeboxes[in + 4]), Float.toString(nodeboxes[in + 5]));
            s += "}";
        }
        s+="\n}";
        return s;
    }

    public static Mesh optimize(Mesh m) {
        HashMap<Triple<Float, Float, Float>, Integer> vertices_indices = new HashMap();
        HashMap<Tuple<Float, Float>, Integer> uvs_indices = new HashMap();
        HashMap<Triple<Float, Float, Float>, Integer> normals_indices = new HashMap();
        Mesh k = new Mesh();
        if (m.nodeboxes != null) {
            k.nodeboxes = new float[m.nodeboxes.length];
            System.arraycopy(m.nodeboxes, 0, k.nodeboxes, 0, m.nodeboxes.length);
            int im = 0;
            int ik;
            for (int d = 0; d < 3; d++) { //3 Iterations
                for (int n = 0; n < k.nodeboxes.length / 6; n++) {
                    im = n * 6;
                    if (k.nodeboxes[im] == 0 && k.nodeboxes[im + 1] == 0 && k.nodeboxes[im + 2] == 0 && k.nodeboxes[im + 3] == 0 && k.nodeboxes[im + 4] == 0 && k.nodeboxes[im + 5] == 0) {
                        continue;
                    }
                    for (int mn = n + 1; mn < k.nodeboxes.length / 6; mn++) {
                        ik = mn * 6;
                        if (k.nodeboxes[ik] == 0 && k.nodeboxes[ik + 1] == 0 && k.nodeboxes[ik + 2] == 0 && k.nodeboxes[ik + 3] == 0 && k.nodeboxes[ik + 4] == 0 && k.nodeboxes[ik + 5] == 0) {
                            continue;
                        }
                        boolean[] equals = equals(k.nodeboxes, k.nodeboxes, im, ik, 6);
                        boolean delete = true;
                        if (equals[1] && equals[2] && equals[4] && equals[5]) {
                            k.nodeboxes[im + 0] = Math.min(k.nodeboxes[im + 0], k.nodeboxes[ik + 0]);
                            k.nodeboxes[im + 3] = Math.min(k.nodeboxes[im + 3], k.nodeboxes[ik + 3]);
                        } else if (equals[2] && equals[3] && equals[0] && equals[5]) {
                            k.nodeboxes[im + 1] = Math.min(k.nodeboxes[im + 1], k.nodeboxes[ik + 1]);
                            k.nodeboxes[im + 4] = Math.min(k.nodeboxes[im + 4], k.nodeboxes[ik + 4]);
                        } else if (equals[1] && equals[4] && equals[3] && equals[0]) {
                            k.nodeboxes[im + 2] = Math.min(k.nodeboxes[im + 2], k.nodeboxes[ik + 2]);
                            k.nodeboxes[im + 5] = Math.min(k.nodeboxes[im + 5], k.nodeboxes[ik + 5]);
                        } else {
                            delete = false;
                        }
                        if (delete) {
                            for (int i = ik; i < 6; i++) {
                                k.nodeboxes[i] = 0;
                            }
                        }
                    }
                }
            }
        }
        k.indices = new int[m.indices.length];
        k.uvindices = new int[m.uvindices.length];
        k.normalindices = new int[m.normalindices.length];
        int ipos = 0;
        int vi=1;
        for (int index : m.indices) {
            int pos = index - 1;
            pos *= 3;
            Triple<Float, Float, Float> there = new Triple<Float, Float, Float>(m.vertices[pos], m.vertices[pos + 1], m.vertices[pos + 2]);
            Integer i = vertices_indices.get(there);
            if (i == null) {
                vertices_indices.put(there, vi);
                i=vi;
                vi++;
            }
            k.indices[ipos] = i;
            ipos++;
        }
        ipos = 0;
        vi = 1;
        for (int index : m.normalindices) {
            int pos = index - 1;
            pos *= 3;
            Triple<Float, Float, Float> there = new Triple<Float, Float, Float>(m.normals[pos], m.normals[pos + 1], m.normals[pos + 2]);
            Integer i = normals_indices.get(there);
            if (i == null) {
                normals_indices.put(there, vi);
                i=vi;
                vi++;
            }
            k.normalindices[ipos] = i;
            ipos++;
        }
        ipos = 0;
        vi = 1;
        for (int index : m.uvindices) {
            int pos = index - 1;
            pos *= 2;
            Tuple<Float, Float> there = new Tuple<Float, Float>(m.uvs[pos], m.uvs[pos + 1]);
            Integer i = uvs_indices.get(there);
            if (i == null) {
                uvs_indices.put(there, vi);
                i=vi;
                vi++;
            }
            k.uvindices[ipos] = i;
            ipos++;
        }
        k.normals = new float[normals_indices.size() * 3];
        k.vertices = new float[vertices_indices.size() * 3];
        k.uvs = new float[uvs_indices.size() * 2];
        for (Entry<Triple<Float, Float, Float>, Integer> e : vertices_indices.entrySet()) {
            int pos = (e.getValue() - 1) * 3;
            k.vertices[pos] = e.getKey().v1;
            k.vertices[pos + 1] = e.getKey().v2;
            k.vertices[pos + 2] = e.getKey().v3;
        }
        for (Entry<Triple<Float, Float, Float>, Integer> e : normals_indices.entrySet()) {
            int pos = (e.getValue() - 1) * 3;
            k.normals[pos] = e.getKey().v1;
            k.normals[pos + 1] = e.getKey().v2;
            k.normals[pos + 2] = e.getKey().v3;
        }
        for (Entry<Tuple<Float, Float>, Integer> e : uvs_indices.entrySet()) {
            int pos = (e.getValue() - 1) * 2;
            k.uvs[pos] = e.getKey().v1;
            k.uvs[pos + 1] = e.getKey().v2;
        }
        return k;
    }
}
