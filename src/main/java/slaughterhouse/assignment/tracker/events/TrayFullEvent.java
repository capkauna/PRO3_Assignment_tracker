package slaughterhouse.assignment.tracker.events;

public class TrayFullEvent
{
  private final int trayId;

  public TrayFullEvent(int trayId)
  {
    this.trayId = trayId;
  }

  public int getTrayId()
  {
    return trayId;
  }
}