package nl.rug.aoop.asteroids.model.gameobjects.spaceship;

import lombok.SneakyThrows;
import nl.rug.aoop.asteroids.control.PlayerKeyListener;
import nl.rug.aoop.asteroids.model.gameobjects.GameObject;
import nl.rug.aoop.asteroids.model.gameobjects.KeyInput;
import nl.rug.aoop.asteroids.util.ReflectionUtils;
import nl.rug.aoop.asteroids.view.AsteroidsFrame;
import lombok.Getter;
import lombok.Setter;
import nl.rug.aoop.asteroids.view.viewmodels.GameObjectViewModel;
import nl.rug.aoop.asteroids.view.viewmodels.SpaceshipViewModel;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.lang.reflect.Field;
import java.util.*;
import java.util.List;

/**
 * This class represents a player's ship. Like all other game objects, it has a location and velocity, but additionally,
 * the spaceship has a weapon that can be used to shoot bullets to destroy asteroids. The spaceship also slows down over
 * time. You may think this is unrealistic, but imagine for a moment that this spaceship has reaction control thrusters
 * allowing it all 6 degrees of freedom, and that it has an inertial dampening system, like any modern starfighter
 * would.
 * <p>
 * Furthermore, the spaceship has a limited energy supply which is regenerated slowly over time by onboard solar panels.
 * Accelerating, turning, and shooting the weapon all drain some of this energy. If there's not enough energy remaining
 * to perform some action, the spaceship will simply remain idle until it has recharged its batteries.
 */
public class Spaceship extends GameObject {

    public final static String OBJECT_ID = "spaceship";

    /**
     * The maximum speed that the spaceship is allowed to reach before extra acceleration will not do anything.
     */
    public static final double MAXIMUM_SPEED = 20.0;

    /**
     * The coefficient to multiply the ship's velocity by every tick, so that it slows down.
     */
    public static final double VELOCITY_DAMPENING_COEFFICIENT = 0.99;

    /**
     * The rate at which the spaceship will speed up, per axis, per tick.
     */
    public static final double ACCELERATION_PER_TICK = 0.4;

    /**
     * The amount in radians that the spaceship rotates per tick, if the player is rotating it.
     */
    public static final double ROTATION_PER_TICK = 0.04 * Math.PI;

    /**
     * The number of game ticks that must pass after firing the ship's weapon before it is able to fire again.
     */
    public static final int WEAPON_COOLDOWN_TICKS = 5;

    /**
     * The amount of energy used by firing the weapon.
     */
    public static final double WEAPON_ENERGY_COST = 10.0;

    /**
     * The amount of energy used by using the thruster to accelerate forward.
     */
    public static final double ACCELERATION_ENERGY_COST = 5.0;

    /**
     * The amount of energy used by the reaction control thrusters to change the orientation of the ship.
     */
    public static final double TURNING_ENERGY_COST = 3.0;

    /**
     * The total amount of energy that can be stored on the ship.
     */
    public static final double ENERGY_CAPACITY = 256.0;

    /**
     * How much energy the ship generates each tick.
     */
    public static final double ENERGY_GENERATION = 3.0;

    /**
     * Collision radius of the ship
     */
    public static final int SHIP_SIZE = 20;

    /**
     * Number of ticks this object is immune to collision
     */
    public static final int IMMUNITY_TICKS = 10;

    /**
     * Direction the spaceship is pointed in.
     */
    @Getter
    @Setter
    private double direction;

    /**
     * Amount of game ticks left, until the spaceship can fire again.
     */
    private int weaponCooldownRemaining;

    /**
     * The amount of energy stored in the ship's batteries.
     */
    private double energy;

    /**
     * Score of the player. I.e. amount of destroyed asteroids.
     */
    @Getter
    private int score;

    /**
     * Indicates whether the fire button is pressed.
     */
    @Setter
    @Getter
    @KeyInput(id = 32)
    private boolean isFiring;

    /**
     * Indicates whether the accelerate button is pressed.
     */
    @Setter
    @KeyInput(id = 87)
    private boolean accelerateKeyPressed;

    /**
     * Indicates whether the turn right button is pressed.
     */
    @Setter
    @KeyInput(id = 68)
    private boolean turnRightKeyPressed;

    /**
     * Indicates whether the turn left button is pressed.
     */
    @Setter
    @KeyInput(id = 65)
    private boolean turnLeftKeyPressed;

    @Getter
    private HashSet<Integer> keyEventSet = new HashSet<>();

    private final HashMap<Integer, Field> inputFields = ReflectionUtils.getKeyInputFields(this.getClass());
    @Getter
    @Setter
    private PlayerKeyListener keyListener;

    private boolean online = false;
    /**
     * Constructs a new spaceship with default values. It starts in the middle of the window, facing directly upwards,
     * with no velocity.
     */
    public Spaceship() {
        super(AsteroidsFrame.WINDOW_SIZE.width / 2, AsteroidsFrame.WINDOW_SIZE.height / 2,
                0, 0, SHIP_SIZE);
        reset();
    }

    public Spaceship(String nick) {
        this();
        setNickId(nick);
    }
    public Spaceship(String nick, boolean online) {
        this();
        setNickId(nick);
        this.online = online;
    }

    public Spaceship(double locationX, double locationY, double velocityX, double velocityY) {
        super(locationX, locationY, velocityX, velocityY, SHIP_SIZE);
    }

    /**
     * Resets all parameters to default values, so a new game can be started.
     */
    public void reset() {
        getLocation().x = AsteroidsFrame.WINDOW_SIZE.width / 2;
        getLocation().y = AsteroidsFrame.WINDOW_SIZE.height / 2;
        getVelocity().x = 0;
        getVelocity().y = 0;
        direction = 0;
        isFiring = false;
        accelerateKeyPressed = false;
        turnLeftKeyPressed = false;
        turnRightKeyPressed = false;
        destroyed = false;
        weaponCooldownRemaining = 0;
        score = 0;
        energy = ENERGY_CAPACITY;
    }

    /**
     * Defines how the spaceship moves. This includes rotating the ship if the user is pressing the key to turn the
     * ship, or accelerating the ship, or firing the weapon.
     */
    @Override
    public void nextStep() {
        super.nextStep();
        attemptToTurn();
        attemptToAccelerate();
        dampenVelocity();
        restWeapon();
        rechargeEnergy();
    }

    @SneakyThrows
    public synchronized void setKeyEventSet(HashSet<Integer> keyEventSet) {
        this.keyEventSet = keyEventSet;
        for (Map.Entry<Integer, Field> entry : inputFields.entrySet()) {
            Integer integer = entry.getKey();
            Field field = entry.getValue();
            if (keyEventSet.contains(integer)) {
                field.set(this, true);
            } else {
                field.set(this, false);
            }
        }
    }

    /**
     * Recharges the ship's energy during a game tick. The energy is renewable, in case you were wondering.
     */
    private void rechargeEnergy() {
        energy += ENERGY_GENERATION;
        energy = Math.min(energy, ENERGY_CAPACITY);
    }

    /**
     * 'Rests' the ship's weapon, if necessary. This essentially just cools down the weapon each game tick until it can
     * be fired again.
     */
    private void restWeapon() {
        if (weaponCooldownRemaining != 0) {
            weaponCooldownRemaining--;
        }
    }

    /**
     * Dampens the ship's velocity, i.e. slows it down slightly, so that you don't drift endlessly across the screen.
     */
    private void dampenVelocity() {
        getVelocity().x *= VELOCITY_DAMPENING_COEFFICIENT;
        getVelocity().y *= VELOCITY_DAMPENING_COEFFICIENT;
    }

    /**
     * Attempts to accelerate the spaceship. If all of the criteria for accelerating the ship are met, then it will
     * accelerate. For a ship to be able to accelerate, the user must be pressing the key to do so, and the ship must
     * have enough energy, and finally, the ship must not exceed its maximum set speed.
     */
    private void attemptToAccelerate() {
        if (accelerateKeyPressed && ((energy >= ACCELERATION_ENERGY_COST && getSpeed() < MAXIMUM_SPEED) || online)){
           accelerate();
           if(online) {
               System.out.println("ONLINE OBJ IS ACCELERATING");
           }
        }
    }

    private void accelerate() {
        getVelocity().x += Math.sin(direction) * ACCELERATION_PER_TICK;
        // Note that we subtract here, because the y-axis on the screen is flipped, compared to normal math.
        getVelocity().y -= Math.cos(direction) * ACCELERATION_PER_TICK;
        energy -= ACCELERATION_ENERGY_COST;
    }

    /**
     * Attempts to turn the spaceship. If all of the criteria for turning the ship are met, then it will rotate.
     * For a ship to be able to rotate, the user must be pressing the key to turn it either left or right, and the ship
     * must have enough energy to rotate.
     */
    private void attemptToTurn() {
        if (energy >= TURNING_ENERGY_COST) {
            boolean didTurn = false;
            if (turnLeftKeyPressed) {
                direction -= ROTATION_PER_TICK;
                didTurn = true;
            }
            if (turnRightKeyPressed) {
                direction += ROTATION_PER_TICK;
                didTurn = true;
            }
            if (didTurn) {
                energy -= TURNING_ENERGY_COST;
            }
        }
    }

    /**
     * @return The number of steps, or game ticks, for which this object is immune from collisions.
     */
    @Override
    protected int getDefaultStepsUntilCollisionPossible() {
        return IMMUNITY_TICKS;
    }

    @Override
    public double[] getObjParameters() {
        return new double[]{getLocation().x, getLocation().y, getVelocity().x, getVelocity().y, getDirection()};
    }

    @Override
    public GameObjectViewModel<? extends GameObject> getViewModel(GameObject object) {
        return new SpaceshipViewModel((Spaceship) object);
    }

    @Override
    public String getObjectId() {
        return OBJECT_ID;
    }

    /**
     * @return The percentage of energy that is available on the ship, out of the total capacity.
     */
    public double getEnergyPercentage() {
        return 100 * energy / ENERGY_CAPACITY;
    }

    /**
     * @return True if the spaceship may fire a bullet. A spaceship is allowed to fire if its weapon is done cooling
     * down, and it has enough energy, and the user is pressing the button to fire the weapon.
     */
    public boolean canFireWeapon() {
        return isFiring
                && weaponCooldownRemaining == 0
                && energy >= WEAPON_ENERGY_COST;
    }

    /**
     * @return true if acceleration button is pressed, false otherwise.
     */
    public boolean isAccelerating() {
        return accelerateKeyPressed;
    }

    /**
     * Sets the fire tick counter to its starting value, to begin a new countdown until the weapon can be used again.
     */
    public void setFired() {
        weaponCooldownRemaining = WEAPON_COOLDOWN_TICKS;
        energy -= WEAPON_ENERGY_COST;
    }

    /**
     * Increments score field.
     */
    public void increaseScore() {
        score++;
    }

    @Override
    public GameObject clone() {
        Spaceship sh = new Spaceship(getLocation().x, getLocation().y, getVelocity().x, getVelocity().y);
        sh.setDirection(direction);
        sh.setAccelerateKeyPressed(accelerateKeyPressed);
        sh.setFiring(isFiring);
        return sh;
    }

    public void updatePosition(double x, double y) {
        getLocation().x = x;
        getLocation().y = y;
    }

    public void updateVelocity(double x, double y) {
        getVelocity().x = x;
        getVelocity().y = y;
    }
}
