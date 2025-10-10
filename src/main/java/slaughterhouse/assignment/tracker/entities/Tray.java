package slaughterhouse.assignment.tracker.entities;

import jakarta.persistence.*;

import java.util.ArrayList;

@Entity
public class Tray
{
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;
  private double maxWeightCapacity;
  private double currentWeight;
  @Enumerated(EnumType.STRING) // <-- Tells JPA to save the enum name ('Type1')
  private PartType type;
  //private ArrayList<Part> parts;
  //redundant once I added the trayId to the Part entity
  private boolean isPackaged;
  public boolean isFull;

  protected Tray()
  {}

  public Tray(PartType type, double maxWeightCapacity)
  {
    //this.id = id;
    //id handled by JPA
    this.type = type;
    this.maxWeightCapacity = maxWeightCapacity;
    this.currentWeight = 0;
    this.isPackaged = false;
    this.isFull = false;
  }

  //logic

  public void addPart(Part part)
  {
    checkPartType(part.getType());
    addPartWeight(part.getWeight());
  }

  public boolean hasCapacity(double partweight) //capacity check for the service to use
  {
    return this.currentWeight + partweight <= this.maxWeightCapacity;
  }

  //some checks

  private void addPartWeight(double partweight)
  {
    if (this.currentWeight + partweight > this.maxWeightCapacity)
    {
      throw new IllegalStateException("Tray is full");
    }
    this.currentWeight += partweight;

    if (this.currentWeight >= this.maxWeightCapacity) {
      this.isFull = true;
    }
  }

  private void checkPartType(PartType type)
  {
    if (this.type != type)
    {
      throw new IllegalArgumentException("Wrong type part for this tray");
    }

  }

  //Setters and getters
  public int getId()
  {
    return id;
  }

  public void setId(int id)
  {
    this.id = id;
  }

  public double getMaxWeightCapacity()
  {
    return maxWeightCapacity;
  }

  public void setMaxWeightCapacity(double maxWeightCapacity)
  {
    this.maxWeightCapacity = maxWeightCapacity;
  }

  public double getCurrentWeight()
  {
    return currentWeight;
  }

  //no setter for currentWeight since it's managed by addPart

  public PartType getType()
  {
    return type;
  }

  public void setType(PartType type)
  {
    this.type = type;
  }

  public boolean isPackaged()
  {
    return isPackaged;
  }

  public void setPackaged(boolean isPackaged)
  {
    this.isPackaged = isPackaged;
  }

  public boolean isFull()
    {
    return isFull;
    }
}
