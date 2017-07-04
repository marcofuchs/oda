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
 * Dient als Verwaltungsklasse für eine Liste von Crossings, die sowohl als
 * PriorityQueue, als auch als HashMap benutzt werden kann.
 *
 * @author marco
 */
public class CrossingMapQueue {
    PriorityQueue<Crossing> queue;
    Map<Integer, Crossing> map;

    /**
     * Erzeugt eine neue Instanz einer MapQueue
     */
    public CrossingMapQueue() {
        map = new HashMap<>();

        Comparator<Crossing> comparator = new CrossingComparator();
        queue = new PriorityQueue<>(10, comparator);
    }

    /**
     * Entfernt das erste Element und gibt es zurück
     *
     * @return
     */
    public Crossing poll() {
        Crossing cr = queue.poll();
        map.remove(cr.getId());

        return cr;
    }

    /**
     * Gibt das erste Element zurück, ohne es zu entfernen
     *
     * @return
     */
    public Crossing peek() {
        return queue.peek();
    }

    /**
     * Gibt das Crossing mit der angegebenen ID zurück
     *
     * @param id die ID des zu suchenden Crossings
     * @return
     */
    public Crossing get(int id) {
        return map.get(id);
    }

    /**
     * Fügt ein neues Crossing hinzu
     *
     * @param newCr das hinzuzufügende Crossing
     */
    public void add(Crossing newCr) {
        map.put(newCr.getId(), newCr);
        queue.add(newCr);
    }

    /**
     * Entfernt das Crossing aus der Liste
     *
     * @param removeCr das zu entfernende Crossing
     */
    public void remove(Crossing removeCr) {
        map.remove(removeCr.getId());
        queue.remove(removeCr);
    }

    /**
     * Prüft, ob die Liste leer ist
     *
     * @return
     */
    public boolean isEmpty() {
        return queue.isEmpty();
    }

    /**
     * Prüft, ob das Crossing mit der angegebenen ID enthalten ist
     *
     * @param id die ID des zu suchenden Crossings
     * @return
     */
    public boolean contains(int id) {
        return map.containsKey(id);
    }
}
