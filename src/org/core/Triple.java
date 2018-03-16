/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.core;

/**
 *
 * Class to store a tuple of three values.
 * 
 * @author agvico
 */
public final class Triple<K, V, E> {
    public K key;
    public V value;
    public E extra;
    
    public Triple(K key, V value, E extra){
        this.key = key;
        this.value = value;
        this.extra = extra;
    }
    
    @Override
    public String toString(){
        return "(" + key.toString() + ", " + value.toString() + ", " + extra.toString() + ")";
    }
}
