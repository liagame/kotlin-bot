package lia;

import java.util.ArrayList;

/**
 * Used for building a response message that is later
 * sent to the game engine.
 **/
public class Api {

    private long uid;
    private int currentIndex = 0;

    private ArrayList<SpeedEvent> speedEvents;
    private ArrayList<RotationSpeedEvent> rotationSpeedEvents;
    private ArrayList<ShootEvent> shootEvents;
    private ArrayList<NavigationStartEvent> navigationStartEvents;
    private ArrayList<NavigationStopEvent> navigationStopEvents;

    protected Api() {
        speedEvents = new ArrayList<>();
        rotationSpeedEvents = new ArrayList<>();
        shootEvents = new ArrayList<>();
        navigationStartEvents = new ArrayList<>();
        navigationStopEvents = new ArrayList<>();
    }

    private int getNextIndex() {
        return currentIndex++;
    }

    protected void setUid(long uid) {
        this.uid = uid;
    }

    /** Change thrust speed of a unit */
    public void setSpeed(int unitId, Speed speed) {
        speedEvents.add(new SpeedEvent(getNextIndex(), unitId, speed));
    }

    /** Change rotation speed of a unit */
    public void setRotationSpeed(int unitId, RotationSpeed rotation) {
        rotationSpeedEvents.add(new RotationSpeedEvent(getNextIndex(), unitId, rotation));
    }

    /** Make a unit shoot */
    public void shoot(int unitId) {
        shootEvents.add(new ShootEvent(getNextIndex(), unitId));
    }

    /** Start navigation */
    public void navigationStart(int unitId, float x, float y) {
        navigationStartEvents.add(new NavigationStartEvent(getNextIndex(), unitId, x, y));
    }

    /** Stop navigation */
    public void navigationStop(int unitId) {
        navigationStopEvents.add(new NavigationStopEvent(getNextIndex(), unitId));
    }

    protected String toJson() {
        SpeedEvent[] speed = new SpeedEvent[speedEvents.size()];
        RotationSpeedEvent[] rotationSpeed = new RotationSpeedEvent[rotationSpeedEvents.size()];
        ShootEvent[] shoot = new ShootEvent[shootEvents.size()];
        NavigationStartEvent[] navigationStart = new NavigationStartEvent[navigationStartEvents.size()];
        NavigationStopEvent[] navigationStop = new NavigationStopEvent[navigationStopEvents.size()];

        Response response = new Response(
                uid,
                MessageType.RESPONSE,
                speedEvents.toArray(speed),
                rotationSpeedEvents.toArray(rotationSpeed),
                shootEvents.toArray(shoot),
                navigationStartEvents.toArray(navigationStart),
                navigationStopEvents.toArray(navigationStop)
        );
        return Response.Companion.toJson(response);
    }
}