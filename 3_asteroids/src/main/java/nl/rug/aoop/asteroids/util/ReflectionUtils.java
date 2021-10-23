package nl.rug.aoop.asteroids.util;

import com.objectdb.o.HMP;
import lombok.extern.java.Log;
import nl.rug.aoop.asteroids.control.menu_commands.MenuCommands;
import nl.rug.aoop.asteroids.model.gameobjects.KeyInput;
import nl.rug.aoop.asteroids.model.obj_factory.FactoryCommand;
import nl.rug.aoop.asteroids.model.obj_factory.ObjectCommand;
import nl.rug.aoop.asteroids.network.data.NetworkParam;
import nl.rug.aoop.asteroids.network.data.deltas_changes.Tuple;
import nl.rug.aoop.asteroids.control.ViewController;
import org.reflections.Reflections;

import javax.swing.*;
import java.lang.reflect.Field;
import java.sql.Ref;
import java.util.*;

@Log
public class ReflectionUtils {

    public static List<Tuple.T2<String, Integer>> getNetworkParams(Object c) {
        List<Tuple.T2<String, Integer>> map = new ArrayList<>();
        Set<Field> fields = new Reflections(c.getClass()).getFieldsAnnotatedWith(NetworkParam.class);
        for (Field f : fields) {
            NetworkParam param = f.getAnnotation(NetworkParam.class);
            try {
                map.add(new Tuple.T2<>(param.id(), f.getInt(f)));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return map;
    }

    public static HashMap<Integer, Field> getKeyInputFields(Class<?> c) {
        HashMap<Integer, Field> map = new HashMap<>();
        Field[] fields = c.getDeclaredFields();
        for (Field f : fields) {
            if(f.isAnnotationPresent(KeyInput.class)){
                KeyInput param = f.getAnnotation(KeyInput.class);
                map.put(param.id(), f);
            }

        }
        return map;
    }

    public static HashMap<String, FactoryCommand> getFactoryCommands(String pkg) {
        HashMap<String, FactoryCommand> map = new HashMap<>();
        Set<Class<?>> commands = new Reflections(pkg).getTypesAnnotatedWith(ObjectCommand.class);
        for (Class<?> c : commands) {
            try {
                if (FactoryCommand.class.isAssignableFrom(c)) {
                    FactoryCommand command = (FactoryCommand) c.getDeclaredConstructor().newInstance();
                    ObjectCommand key = c.getAnnotation(ObjectCommand.class);
                    if (key != null) {
                        map.put(key.id(), command);
                    }
                }
            } catch (Exception e) {
                log.warning("Failed to map object factory commands. Aborting");
            }
        }
        return map;
    }

    public static List<AbstractAction> getMenuCommands(ViewController manager, String pkg) {
        List<AbstractAction> map = new ArrayList<>();
        Set<Class<?>> commands = new Reflections(pkg).getTypesAnnotatedWith(MenuCommands.class);
        for (Class<?> c : commands) {
            try {
                if (AbstractAction.class.isAssignableFrom(c)) {
                    AbstractAction command = (AbstractAction) c.getDeclaredConstructor(ViewController.class).newInstance(manager);
                    MenuCommands key = c.getAnnotation(MenuCommands.class);
                    if (key != null) {
                        map.add(command);
                    }
                }
            } catch (Exception e) {
                log.warning("Failed to menu commands. Aborting");
            }
        }
        return map;
    }
}
