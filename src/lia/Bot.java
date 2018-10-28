package lia;

public interface Bot {
    void processGameEnvironment(GameEnvironment gameEnvironment);
    void processGameState(GameState gameState, Api response);
}
