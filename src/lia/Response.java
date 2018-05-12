package lia;

import static lia.AiApiMessages.*;

/**
 * Used for building a response message that is later
 * sent to the game engine.
 **/
public class Response {

    private AiResponse.Builder responseBuilder;

    protected Response(long uid) {
        responseBuilder = AiResponse.newBuilder();
        responseBuilder.setUid(uid);
    }

    /** Change thrust speed of a player */
    public void setThrustSpeed(int playerId, ThrustSpeed.Enum speed) {
        SetThrustSpeedEvent thrustEvent = SetThrustSpeedEvent.newBuilder()
                .setPlayerId(playerId)
                .setSpeed(speed)
                .build();

        ResponseEvent event = ResponseEvent.newBuilder()
                .setSetThrustSpeed(thrustEvent)
                .build();

        responseBuilder.addEvents(event);
    }

    /** Change rotation speed of a player */
    public void setRotationSpeed(int playerId, Rotation.Enum rotation) {
        SetRotationEvent rotationEvent = SetRotationEvent.newBuilder()
                .setPlayerId(playerId)
                .setRotation(rotation)
                .build();

        ResponseEvent event = ResponseEvent.newBuilder()
                .setSetRotation(rotationEvent)
                .build();

        responseBuilder.addEvents(event);
    }

    /** Make a player shoot */
    public void shoot(int playerId) {
        ShootEvent shootEvent = ShootEvent.newBuilder()
                .setPlayerId(playerId)
                .build();

        ResponseEvent event = ResponseEvent.newBuilder()
                .setShoot(shootEvent)
                .build();

        responseBuilder.addEvents(event);
    }

    protected byte[] toByteArray() {
        return responseBuilder.build().toByteArray();
    }
}