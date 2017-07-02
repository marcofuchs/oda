/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kefu;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

/**
 *
 * @author marco
 */
public class MapQueue {
    PriorityQueue<Crossing> queue;
    Map<Integer, Crossing> map;

    public MapQueue() {
        map = new HashMap<>();

        Comparator<Crossing> comparator = new CrossingComparator();
        queue = new PriorityQueue<>(10, comparator);
    }

    public Crossing poll() {
        Crossing cr = queue.poll();
        map.remove(cr.getId());

        return cr;
    }

    public Crossing peek() {
        return queue.peek();
    }

    public Crossing get(int id) {
        return map.get(id);
    }

    public void add(Crossing newCr) {
        map.put(newCr.getId(), newCr);
        queue.add(newCr);
    }

    public void update(Crossing updatedCr) {
        map.replace(updatedCr.getId(), updatedCr);
        queue.remove(updatedCr);
        queue.add(updatedCr);
    }

    public void remove(Crossing removeCr) {
        map.remove(removeCr.getId());
        queue.remove(removeCr);
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public boolean contains(int id) {
        return map.containsKey(id);
    }
}
