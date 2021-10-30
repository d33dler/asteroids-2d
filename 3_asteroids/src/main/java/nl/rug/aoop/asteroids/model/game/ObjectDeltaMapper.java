package nl.rug.aoop.asteroids.model.game;

import nl.rug.aoop.asteroids.model.gameobjects.GameObject;
import nl.rug.aoop.asteroids.model.gameobjects.asteroid.Asteroid;
import nl.rug.aoop.asteroids.network.data.deltas_changes.Tuple;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ObjectDeltaMapper implements Runnable {

    public List<Tuple.T2<String, List<double[]>>> mappedObjects = new ArrayList<>();
    public volatile boolean cycleDone = true;
    private GameResources resources;
    public ObjectDeltaMapper(GameResources resources) {
        this.resources = resources;
    }

    @Override
    public void run() {
        remapAll();
    }

    private synchronized void remapAll() {
        while (resources.runProcesses) {
            cycleDone = false;
            mappedObjects.clear();
            remap(Asteroid.OBJECT_ID, resources.asteroids);
            cycleDone = true;
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void remap(String objId, Collection<? extends GameObject> collection) {
        List<double[]> mappedCollection = new ArrayList<>();
        for (GameObject object : collection) {
            mappedCollection.add(object.getObjParameters());
        }
        mappedObjects.add(new Tuple.T2<>(objId, mappedCollection));
    }

    public synchronized void wakeup() {
        this.notify();
    }
}
