package slaughterhouse.assignment.tracker.entities;

import jakarta.persistence.*;

@Entity
public class Part
{
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;
  private int animalId;
  @Enumerated(EnumType.STRING) // <-- Tells JPA to save the enum name ('Type1')
  private PartType type;
  private double weight;
  private Integer trayId;

  //JPA requirement: Public or protected no-argument constructor
  //protected reserves this constructor for JPA only
  protected Part()
  {
  }

  public Part(PartType type, int animalId, double weight)
  {
    this.type = type;
    this.animalId = animalId;
    this.weight = weight;
    this.trayId = null;
  }

  public int getId()
  {
    return id;
  }

  public void setId(int id)
  {
    this.id = id;
  }

  public PartType getType()
  {
    return type;
  }

  public void setType(PartType type)
  {
    this.type = type;
  }

  public double getWeight()
  {
    return weight;
  }

  public void setWeight(double weight)
  {
    this.weight = weight;
  }

  public int getAnimalId()
  {
    return animalId;
  }

  public void setAnimalId(int animalId)
  {
    this.animalId = animalId;
  }

  public Integer getTrayId()
  {
    return trayId;
  }

  public void setTrayId(Integer trayId)
  {
    this.trayId = trayId;
  }
}
