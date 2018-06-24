package lia;

import java.util.ArrayList;

/**
 * Used for building a response message that is later
 * sent to the game engine.
 **/
public class Api {

    private long uid;

    private ArrayList<ThrustSpeedEvent> thrustSpeedEvents;
    private ArrayList<RotationEvent> rotationEvents;
    private ArrayList<ShootEvent> shootEvents;

    protected Api() {
        thrustSpeedEvents = new ArrayList<>();
        rotationEvents = new ArrayList<>();
        shootEvents = new ArrayList<>();
    }

    protected void setUid(long uid) {
        this.uid = uid;
    }

    /** Change thrust speed of a player */
    public void setThrustSpeed(int playerId, ThrustSpeed speed) {
        thrustSpeedEvents.add(
                new ThrustSpeedEvent(EventType.SET_THRUST_SPEED, playerId, speed)
        );
    }

    /** Change rotation speed of a player */
    public void setRotationSpeed(int playerId, Rotation rotation) {
        rotationEvents.add(
                new RotationEvent(EventType.SET_ROTATION, playerId, rotation)
        );
    }

    /** Make a player shoot */
    public void shoot(int playerId) {
        shootEvents.add(
                new ShootEvent(EventType.SHOOT, playerId)
        );
    }

    protected String toJson() {
        ThrustSpeedEvent[] thrust = new ThrustSpeedEvent[thrustSpeedEvents.size()];
        thrust = thrustSpeedEvents.toArray(thrust);

        RotationEvent[] rotation = new RotationEvent[rotationEvents.size()];
        rotation = rotationEvents.toArray(rotation);

        ShootEvent[] shoot = new ShootEvent[shootEvents.size()];
        shoot = shootEvents.toArray(shoot);

        Response response =  new Response(
                uid,
                MessageType.RESPONSE,
                thrust,
                rotation,
                shoot
        );
        return Response.Companion.toJson(response);
    }
}