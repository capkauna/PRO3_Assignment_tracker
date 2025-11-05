package slaughterhouse.assignment.tracker.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;
import java.util.ArrayList;
import java.util.List;

@Entity
public class HalfAnimalProduct extends Product
{
  // A List of Parts belonging to this product.
  // We use @Transient to indicate that JPA should NOT automatically manage this relationship,
  // as the Part's FK is currently trayId, not productId. The service layer manages the relationship.
  // For now, we will use a simple List and rely on the Service layer/Tray IDs for traceability.
  @Transient
  private List<Part> parts;
//since there isn't a specific constructor needed here, this will be public instead of protected
  public HalfAnimalProduct()
  {
    this.parts = new ArrayList<>();
  }

  public void addPart(Part part)
  {
    if (!isPartTypeUnique(part))
    {
      throw new IllegalArgumentException("This type of part was already added to the Half Animal Product.");
    }
    // relying on the service logic and the Part's trayId for final linkage.
    getParts().add(part);
  }

  // making sure there is only one part of each type
  public boolean isPartTypeUnique(Part part)
  {
    for (Part p : getParts())
    {
      if (part.getType() == p.getType())
      {
        return false;
      }
    }
    return true;
  }

  // Lazy initiating the parts list for safety if the default constructor is used by JPA
  public List<Part> getParts()
  {
    if (this.parts == null) {
      this.parts = new ArrayList<>();
    }
    return parts;
  }
}
