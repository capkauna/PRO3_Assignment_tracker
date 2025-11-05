package slaughterhouse.assignment.tracker.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Transient;
import java.util.ArrayList;
import java.util.List;

@Entity
public class SameTypeProduct extends Product
{
  @Enumerated(EnumType.STRING)
  private PartType type;

  // @Transient because the parts relationship is managed by the service and the Part's trayId.
  @Transient
  private List<Part> parts;

  // JPA requirement: protected no-argument constructor
  protected SameTypeProduct()
  {
    this.parts = new ArrayList<>();
  }

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
      throw new IllegalArgumentException("Wrong type of part for this SameTypeProduct.");
    }
    getParts().add(part);
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

  // Lazy initialize the parts list for safety if the default constructor is used by JPA
  public List<Part> getParts()
  {
    if (this.parts == null) {
      this.parts = new ArrayList<>();
    }
    return parts;
  }

}
