package slaughterhouse.assignment.tracker.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class Product
{
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  // JPA requirement: protected no-argument constructor
  protected Product()
  {
  }

  // Getter for ID is necessary for the repository to work
  public int getId()
  {
    return id;
  }
}
