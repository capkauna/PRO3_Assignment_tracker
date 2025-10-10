package slaughterhouse.assignment.tracker.entities;

public enum PartType
{
  Type1("Type 1"),
  Type2( "Type 2"),
  Type3( "Type 3"),
  Type4( "Type 4"),
  Type5( "Type 5");

  PartType(String name)
  {
  }

  public String toString()
  {
    return this.name();
  }
}
