package nl.rug.aoop.asteroids.model;

import nl.rug.aoop.asteroids.network.data.deltas_changes.Tuple;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

public class GameResources {
    List<Tuple.T2<BufferedImage,BufferedImage>> spriteImages;

    public GameResources() {
        init();
    }

    private void init() {
        File folder = new File("images/ship_sprites/");
        File[] spriteFiles = folder.listFiles();
        for(File file : spriteFiles) {
            //file.getPath().
        }
    }
}
