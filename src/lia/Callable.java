package lia;

public interface Callable {
    void process(MapData mapData);
    void process(StateUpdate stateUpdate, Api response);
}
