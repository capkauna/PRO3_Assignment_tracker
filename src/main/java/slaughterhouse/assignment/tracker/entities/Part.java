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
  private boolean isPackaged; //to handle the case where a tray still has parts but a part or more were packed (removed from it)
  private Integer productId; //to make everything perfectly traceable

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
    this.isPackaged = false; //false because it's created in the butchering service
    this.productId = null;//since it becomes part of a product only at the end of the processing
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

  public boolean isPackaged()
  {
    return isPackaged;
  }

  public void setPackaged(boolean packaged)
  {
    isPackaged = packaged;
  }

  public Integer getProductId()
  {
    return productId;
  }

  public void setProductId(Integer productId)
  {
    this.productId = productId;
  }
}
