package slaughterhouse.assignment.tracker.dtos;

import java.time.LocalDate;

public class AnimalInfoResponseDTO
{
  private int animalid;
  private double weight;
  private String regNo;
  private LocalDate registrationDate;
  private String origin;
  private boolean isButchered;

  //empty constructor for other frameworks
  public AnimalInfoResponseDTO()
  {
  }

  public AnimalInfoResponseDTO(int id, double weight, String regNo, LocalDate registrationDate, String origin,
      boolean butchered)
  {
    this.animalid = id;
    this.weight = weight;
    this.regNo = regNo;
    this.registrationDate = registrationDate;
    this.origin = origin;
    this.isButchered = butchered;
  }

  //getters and setters are required for Spring to map the JSON
  //for the response into this object.
  //but also for services and controllers to use,so the Animal class doesn't need to be exposed directly

  public int getAnimalid()
  {
    return animalid;
  }

  public void setAnimalid(int animalid)
  {
    this.animalid = animalid;
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

  public boolean isButchered()
  {
    return isButchered;
  }

  public void setButchered(boolean butchered)
  {
    isButchered = butchered;
  }
}
