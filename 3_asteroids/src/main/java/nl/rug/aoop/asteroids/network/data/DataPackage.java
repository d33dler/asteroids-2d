package nl.rug.aoop.asteroids.network.data;

import lombok.Getter;

import java.io.Serializable;

public class DataPackage implements Serializable {
    @Getter
    private final String id;
    @Getter
    private final long timestamp;
    /**
     * Payload of the request.
     */
    private final Serializable body;

    /**
     * Creates a new request with a payload.
     *
     * @param type The unique identifier of this request.
     * @param body The payload of this request.
     */
    public DataPackage(long time, String type, Serializable body) {
        timestamp = time;
        this.id = type;
        this.body = body;
    }

    /**
     * Creates a new request with an empty payload.
     *
     * @param type The unique identifier of this request.
     */
    public DataPackage(long time, String type) {
        timestamp = time;
        this.id = type;
        this.body = null;
    }

    /**
     * Retrieves the payload of this request. Uses generics to prevent you from having to explicitly cast the payload
     * each time. The id of the payload is inferred by the compiler from the LHS of the assignment.
     *
     * @param <T> The id of the payload.
     * @return The payload of this request.
     */
    @SuppressWarnings("unchecked")
    public <T> T getBody() {
        try {
            return (T) body;
        } catch (ClassCastException e) {
            e.printStackTrace();
            return null;
        }
    }

    public long getLatency() {
        return System.currentTimeMillis() - timestamp;
    }
    public boolean isAcceptedLatency(long bound) {
        return getLatency() <= bound;
    }
}
