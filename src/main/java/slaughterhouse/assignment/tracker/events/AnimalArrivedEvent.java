package slaughterhouse.assignment.tracker.events;

public class AnimalArrivedEvent
{
  private final int animalId;

  public AnimalArrivedEvent(int animalId) {
    this.animalId = animalId;
  }

  public int getAnimalId() {
    return animalId;
  }
}
