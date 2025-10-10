package slaughterhouse.assignment.tracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import slaughterhouse.assignment.tracker.entities.PartType;
import slaughterhouse.assignment.tracker.entities.Tray;

import java.util.List;

public interface TrayRepository extends JpaRepository<Tray, Integer>
{
  public Tray findById(int id);
  List<Tray> findByType(PartType type);
  List<Tray> findByAnimalId(int animalId);
  List<Tray> findbyIsPackaged(boolean isPackaged);
  List<Tray> findbyIsFull(boolean isFull);
  List<Tray> findByTypeAndIsFull(PartType type, boolean isFull);
  List<Tray> findByTypeAndIsFullIsFalse(PartType type);//spring understands IsFullIsFalse as a boolean
  List<Tray> findByTypeAndIsFullIsFalseAndIsPackagedIsFalse(PartType type);
}
