package slaughterhouse.assignment.tracker.entities;

import java.util.ArrayList;

public class SameTypeProduct extends Product
{
  private PartType type;
  private ArrayList<Part> parts;

  public SameTypeProduct(PartType type)
  {
    this.type = type;
    this.parts = new ArrayList<>();
  }

//some logic
  public void addPart(Part part)
  {
    if (!rightType(part.getType()))
    {
      throw new IllegalArgumentException("Wrong type of part");
    }
    parts.add(part);
  }

//validators
  private boolean rightType(PartType type)
  {
    return this.type == type;
  }


//setters and getters
  public PartType getType()
  {
    return type;
  }

  public void setType(PartType type)
  {
    this.type = type;
  }

  public ArrayList<Part> getParts()
  {
    return parts;
  }

  public void setParts(ArrayList<Part> parts)
  {
    this.parts = parts;
  }
}
