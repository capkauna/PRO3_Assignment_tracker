package slaughterhouse.assignment.tracker.services;

import jakarta.transaction.Transactional;
import org.springframework.context.event.EventListener;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import slaughterhouse.assignment.tracker.entities.Animal;
import slaughterhouse.assignment.tracker.entities.Part;
import slaughterhouse.assignment.tracker.entities.PartType;
import slaughterhouse.assignment.tracker.entities.Tray;
import slaughterhouse.assignment.tracker.events.AnimalArrivedEvent;
import slaughterhouse.assignment.tracker.events.TrayFullEvent;
import slaughterhouse.assignment.tracker.repository.AnimalRepository;
import slaughterhouse.assignment.tracker.repository.PartRepository;
import slaughterhouse.assignment.tracker.repository.TrayRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class ButcheringService
{
  private AnimalRepository animalRepository;
  private PartRepository partRepository;
  private TrayRepository trayRepository;
  private final ApplicationEventPublisher eventPublisher;

  public ButcheringService(AnimalRepository animalRepository,
      PartRepository partRepository,
      TrayRepository trayRepository, ApplicationEventPublisher eventPublisher) {
    this.animalRepository = animalRepository;
    this.partRepository = partRepository;
    this.trayRepository = trayRepository;
    this.eventPublisher = eventPublisher;
  }

  @Async
  @EventListener
  @Transactional
  public void handleAnimalArrival(AnimalArrivedEvent event)
  {
    int animalId = event.getAnimalId();
    System.out.println("Animal " + animalId + " can be butchered");
    //butchering logic here
    try {
      butcherAndTrayAnimal(animalId);
    } catch (IllegalArgumentException e) {
      System.err.println("Butchering Failed for Animal " + animalId + ": " + e.getMessage());
    }
  }


  @Transactional
  public void butcherAndTrayAnimal(int animalId)
  {
    Animal animal = animalRepository.findById(animalId)
        .orElseThrow(() -> new IllegalArgumentException("Animal with id " + animalId + " not found"));
    //findbyId is preferred over getById because it throws an exception if the entity is not found
    //generate and save parts
    List<Part> newParts = generateParts(animalId, animal.getWeight());
    partRepository.saveAll(newParts);
    //mark animal as butchered
    animal.markAsButchered();
    animalRepository.save(animal);
    System.out.println("Animal " + animalId + " was butchered");

    //assign parts to trays

    assignPartsToTrays(newParts);


  }



  public List<Part> generateParts(int animalId, double totalWeight)
  {
    List<Part> allParts = new ArrayList<>();
    double weightPerPart = totalWeight / (PartType.values().length * 2);
    //since the butchering is automated and this is not a real case where someone would weigh anything,
    // the weight of each part is equally distributed

    for (PartType type : PartType.values()) {
      // Add to the collective list (allParts.add)
      allParts.add(new Part(type, animalId, weightPerPart));
      allParts.add(new Part(type, animalId, weightPerPart));
    }

    return allParts;
  }

  private void assignPartsToTrays(List<Part> newParts) {

    // Group parts by type so we can process them efficiently (one type at a time)
    // This allows us to keep reusing the same tray until it's full.
    newParts.stream()
        .collect(java.util.stream.Collectors.groupingBy(Part::getType))
        .forEach(this::trayPartsOfType);

    System.out.println("✅ All new parts successfully assigned to trays.");
  }

  /**
   * Assigns a batch of parts of a single type to available or new trays.
   */
  private void trayPartsOfType(PartType partType, List<Part> parts) {

    // find existing, available trays of this type
    List<Tray> availableTrays = trayRepository.findByTypeAndIsFullIsFalseAndIsPackagedIsFalse(partType);

    // Iterator safely manages the list of available trays
    java.util.Iterator<Tray> trayIterator = availableTrays.iterator();

    // The current tray we are filling
    Tray currentTray = trayIterator.hasNext() ? trayIterator.next() : null;

    // assuming all trays have a capacity of 1000.0 kg
    final double NEW_TRAY_CAPACITY = 1000.0;

    for (Part part : parts) {

      boolean partTrayed = false;

      // Loop attempts to find a suitable tray (current or next)
      while (!partTrayed) {

        // A. If no tray is available, create a brand new one
        if (currentTray == null) {
          currentTray = createNewTray(partType, NEW_TRAY_CAPACITY);
          // System.out.println("   [Traying] Created new Tray ID " + currentTray.getId() + " for type " + partType);
        }

        // B. If a tray already exists, check capacity using the entity method
        if (currentTray.hasCapacity(part.getWeight())) {

          // update the tray's state (may set isFull=true)
          currentTray.addPart(part); // This validates type and updates weight/isFull.

          // Persistence Logic: Link the Part to its tray and save BOTH entities
          part.setTrayId(currentTray.getId());
          partRepository.save(part);
          trayRepository.save(currentTray);

          partTrayed = true; // Success! Break out of the inner while loop.

        } else {

          //  C. If the current tray is full (or cannot fit), move to the next

          // Mark current tray as officially full (if not already set by addPartWeight)
          // This is important for the next time we query the DB!
          if (!currentTray.isFull()) {
            // Tray must be full if it failed hasCapacity check and the weight logic is correct
            // If it failed the check, it should be marked full before saving.
            currentTray.isFull = true;
            trayRepository.save(currentTray);
            System.out.println("   [Traying] Tray " + currentTray.getId() + " is now full. Moving to next.");

            //tray full event
            eventPublisher.publishEvent(new TrayFullEvent(currentTray.getId()));
          }

          // 2. Move to the next available tray from the initial list or set to null to force creation.
          currentTray = trayIterator.hasNext() ? trayIterator.next() : null;
        }
      }
    }
  }


  /**
   * Helper method to create and immediately persist a new Tray, ensuring it has an ID
   * for the Part foreign key. Since this runs inside the parent @Transactional, it's safe.
   */
  private Tray createNewTray(PartType type, double capacity) {
    Tray newTray = new Tray(type, capacity);
    // Persist immediately so it has a valid database ID.
    return trayRepository.save(newTray);
  }


}
