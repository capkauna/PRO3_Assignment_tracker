package slaughterhouse.assignment.tracker.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

// This class uses validation annotations to ensure the incoming data is correct
// before it reaches the service.
public class AnimalRegistrationRequestDTO
{
  @Positive(message = "Weight must be a positive number")
  private double weight;

  @NotBlank(message = "Registration number cannot be empty")
  private String regNo;

  @NotBlank(message = "Origin cannot be empty")
  private String origin;

  @NotNull(message = "Registration date cannot be null")
  @PastOrPresent(message = "Registration date cannot be in the future")
  private LocalDate date;

  // Getters and Setters are required for Spring to map the JSON
  // from the request body into this object.

  public double getWeight() {
    return weight;
  }

  public void setWeight(double weight) {
    this.weight = weight;
  }

  public String getRegNo() {
    return regNo;
  }

  public void setRegNo(String regNo) {
    this.regNo = regNo;
  }

  public String getOrigin() {
    return origin;
  }

  public void setOrigin(String origin) {
    this.origin = origin;
  }

  public LocalDate getDate() {
    return date;
  }

  public void setDate(LocalDate date) {
    this.date = date;
  }
}
