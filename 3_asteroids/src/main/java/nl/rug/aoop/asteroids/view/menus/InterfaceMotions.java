package nl.rug.aoop.asteroids.view.menus;

public interface InterfaceMotions {
    default void loop(Runnable r, int a) {
        for (int i = a; i!=0; i--){
            r.run();
        }
    }
}
