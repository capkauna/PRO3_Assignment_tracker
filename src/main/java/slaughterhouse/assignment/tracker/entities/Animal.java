package slaughterhouse.assignment.tracker.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Animal
{
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY) // DB handles ID generation
  private int id;
  private double weight;
  private String regNo;
  private boolean isButchered;

  //JPA requirement: Public or protected no-argument constructor
  //protected reserves this constructor for JPA only
  protected Animal()
  {
  }

  //for creating animals with weight and regNo but no id since that is the db responsibility
  public Animal(double weight, String regNo)
  {
    hasWeight(weight);
    hasRegNo(regNo);
    this.weight = weight;
    this.regNo = regNo;
    this.isButchered = false;
  }

  public int getId()
  {
    return id;
  }

  public void setId(int id)
  {
    this.id = id;
  }

  public double getWeight()
  {
    return weight;
  }

  public void setWeight(double weight)
  {
    this.weight = weight;
  }

  public String getRegNo()
  {
    return regNo;
  }

  public void setRegNo(String regNo)
  {
    this.regNo = regNo;
  }
  public boolean isButchered()
  {
    return isButchered;
  }
  public void setButchered(boolean isButchered)
  {
    this.isButchered = isButchered;
  }

  //marker

  public void markAsButchered()
  {
    if (this.isButchered)
    {
      throw new IllegalStateException("This animal was already butchered");
    }
    this.isButchered = true;
  }

  //validators

  private void hasWeight(double weight)
  {
    if (weight <= 0)
    {
      throw new IllegalArgumentException("Weight must be greater than 0");
    }
  }

  private void hasRegNo(String s)
  {
    if (s == null || s.trim().isEmpty())
      {
      throw new IllegalArgumentException("Registration number cannot be empty");
      }
  }



  @Override
  public String toString() {
    return String.format(
        "| ID: %-5d | RegNo: %-10s | Weight: %-7.2f kg |",
        id, regNo, weight
    );
  }
}
