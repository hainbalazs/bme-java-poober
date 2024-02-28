package hu.bme.aut.stepsysterv.PooBer.messagequeue;

public enum Action {
    BANNED("banned"),
    UNBANNED("unbanned");

    public final String label;

    Action(String label) {
        this.label = label;
    }
}
