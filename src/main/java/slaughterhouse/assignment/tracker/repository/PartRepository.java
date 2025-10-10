package slaughterhouse.assignment.tracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import slaughterhouse.assignment.tracker.entities.Part;
import slaughterhouse.assignment.tracker.entities.PartType;

import java.util.List;

@Repository public interface PartRepository extends JpaRepository<Part, Integer>
{
  //handled by spring

  List<Part> findByAnimalId(int animalId);
  // search by the Part Type (the Enum itself)
  // Hibernate automatically converts the PartType enum value to the stored String/Ordinal for the query.
  List<Part> findByType(PartType type);
  //find by Trayed status
  List<Part> findByIsTrayed(boolean isTrayed);
}
