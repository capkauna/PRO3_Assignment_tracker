package slaughterhouse.assignment.tracker.entities;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class Animal
{
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY) // DB handles ID generation
  private int id;
  private double weight;
  @Column(unique = true, nullable = false)
  private String regNo;

  private LocalDate registrationDate;
  private String origin;
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

  //for creating animals with the new attributes toolDate and origin
  public Animal(double weight, String regNo, LocalDate registrationDate, String origin)
  {
    this(weight, regNo);//this calls the other constructor, allows the new attributes to be set later too and reduces boilerplate code (the checks already in place, for example)
    this.registrationDate = registrationDate;
    this.origin = origin;
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

  public LocalDate getRegistrationDate()
  {
    return registrationDate;
  }

  public void setRegistrationDate(LocalDate registrationDate)
  {
    this.registrationDate = registrationDate;
  }

  public String getOrigin()
  {
    return origin;
  }

  public void setOrigin(String origin)
  {
    this.origin = origin;
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




  /**
   * FIX: Implemented the toString() method to match the format
   * expected by the AnimalTest.
   */
  @Override
  public String toString()
  {
    return "Animal{" +
        "id=" + id +
        ", weight=" + weight +
        ", regNo=" + regNo +
        ", registrationDate=" + registrationDate +
        ", origin=" + origin +
        ", isButchered=" + isButchered +
        '}';
  }
}
