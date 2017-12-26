/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package appguru;

import java.util.Objects;

/**
 *
 * @author lars
 */
class Triple<T, K, J> {
    public T v1;
    public K v2;
    public J v3;
    public Triple(T value1, K value2, J value3) {
        this.v1=value1;
        this.v2=value2;
        this.v3=value3;
    }

    @Override
    public int hashCode() {
        return (int)Math.cos((double)(v1.hashCode())/((Double)((double)v2.hashCode()/Math.PI)).hashCode()*v2.hashCode()*System.currentTimeMillis())*Integer.MAX_VALUE;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Triple<?, ?, ?> other = (Triple<?, ?, ?>) obj;
        if (!Objects.equals(this.v1, other.v1)) {
            return false;
        }
        if (!Objects.equals(this.v2, other.v2)) {
            return false;
        }
        return Objects.equals(this.v3, other.v3);
    }
    
    @Override
    public String toString() {
        return "("+v1.toString()+"|"+v2.toString()+"|"+v3.toString()+")";
    }
}
