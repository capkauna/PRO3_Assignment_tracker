package slaughterhouse.assignment.tracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import slaughterhouse.assignment.tracker.entities.Animal;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository public interface AnimalRepository extends JpaRepository<Animal, Integer>
{

  Optional<Animal> findByRegNo(String species);
  List<Animal> findByOrigin(String origin);
  List<Animal> findByRegistrationDate(LocalDate date);

  //for testing resetting the id counter

  // PostgreSQL command to reset the ID sequence
  @Modifying // Indicates this query will modify the database state
  @Transactional // Ensures the operation runs within a database transaction
  @Query(value = "ALTER SEQUENCE animal_id_seq RESTART WITH 1", nativeQuery = true)
  void resetIdSequence();

  // 'animal_id_seq' is the default name PostgreSQL/Hibernate uses
  // for a sequence on an auto-generated 'id' field in the 'animal' table.
}
