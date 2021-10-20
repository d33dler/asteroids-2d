package nl.rug.aoop.asteroids.util;

import com.objectdb.o.HMP;
import javassist.tools.reflect.Reflection;
import lombok.extern.java.Log;
import nl.rug.aoop.asteroids.control.menu_commands.MenuCommand;
import nl.rug.aoop.asteroids.control.menu_commands.MenuCommands;
import nl.rug.aoop.asteroids.model.obj_factory.FactoryCommand;
import nl.rug.aoop.asteroids.model.obj_factory.ObjectCommand;
import nl.rug.aoop.asteroids.view.ViewManager;
import org.reflections.Reflections;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

@Log
public class ReflectionUtils {

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

    public static List<AbstractAction> getMenuCommands(ViewManager manager, String pkg) {
        List<AbstractAction> map = new ArrayList<>();
        Set<Class<?>> commands = new Reflections(pkg).getTypesAnnotatedWith(MenuCommands.class);
        for (Class<?> c : commands) {
            try {
                if (AbstractAction.class.isAssignableFrom(c)) {
                    AbstractAction command = (AbstractAction) c.getDeclaredConstructor(ViewManager.class).newInstance(manager);
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
