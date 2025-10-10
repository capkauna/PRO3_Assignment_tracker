package slaughterhouse.assignment.tracker.entities;

import java.util.ArrayList;

public class HalfAnimalProduct extends Product
{
  private ArrayList<Part> parts;

  public HalfAnimalProduct()
  {
    //super
    this.parts = new ArrayList<>();
  }

  public void addPart(Part part)
  {
    if (!isPartTypeUnique(part))
    {
      throw new IllegalArgumentException("This type of part was already added");
    }
    parts.add(part);
  }

  //making sure there is only one part of each type
  public boolean isPartTypeUnique(Part part)
  {
    for (Part p : parts)
    {
      if (part.getType() == p.getType())
      {
        return false;
      }
    }
    return true;
  }

  public ArrayList<Part> getParts()
  {
    return parts;
  }
}
