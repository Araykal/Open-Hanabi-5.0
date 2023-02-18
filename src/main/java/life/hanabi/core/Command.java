package life.hanabi.core;

public abstract class Command {
    protected String name;
    protected String[] otherNames;

    public Command(String name) {
        this.name = name;
        this.otherNames = otherNames;
    }

    public abstract void execute(String[] args);

    public final String getName() {
        return name;
    }

}
