package cross.platform.test.suite.constant;

public enum Direction {
    UP("up"),
    DOWN("down"),
    LEFT("left"),
    RIGHT("right");

    public final String label;

    private Direction(String label) {
        this.label = label;
    }

    public Direction getOpposite() {
        switch(this) {
            case UP:
                return DOWN;
            case DOWN:
                return UP;
            case LEFT:
                return RIGHT;
            case RIGHT:
                return LEFT;
            default:
                throw new IllegalArgumentException(String.format("Invalid direction: %s", this.toString()));
        }
    }
}