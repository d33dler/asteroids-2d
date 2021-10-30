package nl.rug.aoop.asteroids.model.game;

import lombok.Getter;
import nl.rug.aoop.asteroids.model.gameobjects.GameObject;
import nl.rug.aoop.asteroids.model.gameobjects.spaceship.Spaceship;
import nl.rug.aoop.asteroids.network.data.deltas_changes.Tuple;

import java.util.*;

/**
 * RendererDeepCloner class is used to create clones that are used by
 * the asteroidsPanel to display graphics.
 * By loading clones we avoid ConcurrentModificationExceptions
 */
public class RendererDeepCloner implements Runnable {

    @Getter
    public Collection<GameObject> clonedObjects = new ArrayList<>();
    public volatile boolean cycleDone = true;
    GameResources resources;

    public RendererDeepCloner(GameResources resources) {
        this.resources = resources;
    }

    @Override
    public synchronized void run() {
        recloneAll();
    }

    /**
     * Cloning cycle method
     */
    private synchronized void recloneAll() {

        while (resources.isRunProcesses()) {
            cycleDone = false;
            clonedObjects.clear();
            reclone(resources.bullets);
            reclone(resources.asteroids);
            reclone(resources.players.values());
            cycleDone = true;
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private synchronized void reclone(Collection<? extends GameObject> c) {
        for (GameObject origin : c) {
            clonedObjects.add(origin.clone());
        }
    }

    public synchronized void wakeup() {
        this.notify();
    }

    /**
     * Second task of the class is to load deltas caches at each game tick to update the game state for the
     * multiplayer modes. It loads player key inputs & vectors + environment objects
     */
    public synchronized void loadCache() {
        HashMap<String,Spaceship> bufferCache = new HashMap<>();
        for (Map.Entry<String, Tuple.T3<String, HashSet<Integer>, double[]>> entry : resources.spaceshipCache.entrySet()) {
            String s = entry.getKey();
            Tuple.T3<String, HashSet<Integer>, double[]> deltas = entry.getValue();
            if (resources.players.containsKey(s)) {
                Spaceship ship = resources.players.get(s);
                ship.setKeyEventSet(deltas.b);
                ship.updateParameters(deltas.c);
                bufferCache.put(s,ship);
            } else {
                if(!resources.destroyedShipsCache.contains(s)){    // Dealing with conflicting delta leaving remnant spaceship
                   bufferCache.put(s,Spaceship.newMultiplayerSpaceship(deltas.a, resources));
                }
            }
        }
        resources.players.clear();
        resources.players.putAll(bufferCache);
        resources.asteroids.addAll(resources.asteroidsCache);
        resources.asteroidsCache.clear();
        resources.spaceshipCache.clear();
    }

}
