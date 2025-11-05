package slaughterhouse.assignment.tracker.services;

import jakarta.transaction.Transactional;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import slaughterhouse.assignment.tracker.entities.HalfAnimalProduct;
import slaughterhouse.assignment.tracker.entities.Part;
import slaughterhouse.assignment.tracker.entities.PartType;
import slaughterhouse.assignment.tracker.entities.SameTypeProduct;
import slaughterhouse.assignment.tracker.entities.Tray;
import slaughterhouse.assignment.tracker.events.TrayFullEvent;
import slaughterhouse.assignment.tracker.repository.PartRepository;
import slaughterhouse.assignment.tracker.repository.ProductRepository;
import slaughterhouse.assignment.tracker.repository.TrayRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PackagingService
{
  private final TrayRepository trayRepository;
  private final PartRepository partRepository;
  private final ProductRepository productRepository;

  public PackagingService(TrayRepository trayRepository,
      PartRepository partRepository,
      ProductRepository productRepository)
  {
    this.trayRepository = trayRepository;
    this.partRepository = partRepository;
    this.productRepository = productRepository;
  }

  @Async
  @EventListener
  @Transactional
  public void handleTrayFullEvent(TrayFullEvent event)
  {
    packageReadyTrays(event.getTrayId());
  }

  /**
   * Main entry point for packaging logic, triggered by a TrayFullEvent.
   */
  @Transactional
  public void packageReadyTrays(int trayId)
  {
    Optional<Tray> trayOpt = trayRepository.findById(trayId);
    if (trayOpt.isEmpty()) {
      System.err.println("Packaging Station: Tray ID " + trayId + " not found. Skipping.");
      return;
    }
    Tray tray = trayOpt.get();

    // Double-check state: only process if full and not packaged (avoids race conditions)
    if (!tray.isFull() || tray.isPackaged()) {
      System.out.println("Packaging Station: Tray " + trayId + " is not ready or already packaged. Skipping.");
      return;
    }

    try {
      // 1. Prioritize SameTypeProduct packaging (consumes the whole tray)
      packageSameTypeTray(tray);

      // 2. After packaging a whole tray, check if we can form any HalfAnimalProducts
      attemptHalfAnimalPackaging();

    } catch (Exception e) {
      System.err.println("Packaging failed for Tray " + trayId + ": " + e.getMessage());
    }
  }


  /**
   * Processes a single full tray by turning it into a SameTypeProduct.
   * This logic consumes all parts in the tray and marks them (and the tray) as packaged.
   */
  private void packageSameTypeTray(Tray tray)
  {
    // 1. Get all parts associated with this tray
    List<Part> parts = partRepository.findByTrayId(tray.getId());
    List<Part> partsToSave = new ArrayList<>(); // To track parts that need status update

    if (parts.isEmpty()) {
      System.err.println("Tray " + tray.getId() + " is full but contains no parts. Cannot package.");
      return;
    }

    // 2. Create the SameTypeProduct
    SameTypeProduct product = new SameTypeProduct(tray.getType());

    // 3. Add all parts to the product AND mark them as packaged
    for (Part part : parts) {
      product.addPart(part);
      part.setPackaged(true); // <--- MARK THE PART AS PACKAGED
      part.setProductId(product.getId());
      partsToSave.add(part);
    }

    // 4. Register the new product and save the updated parts
    productRepository.save(product);
    partRepository.saveAll(partsToSave); // Persist the packaged status of all parts
    System.out.println("Packaging Station: Created SameTypeProduct ID " + product.getId() + " from Tray " + tray.getId());

    // 5. Mark the tray as packaged (This is correct since ALL parts have been consumed)
    tray.setPackaged(true);
    trayRepository.save(tray);
    System.out.println("Packaging Station: Tray " + tray.getId() + " marked as packaged.");
  }


  /**
   * Logic for creating a HalfAnimalProduct. It requires one un-packaged part of every
   * PartType across all available parts, consuming only the parts it uses.
   */
  private void attemptHalfAnimalPackaging()
  {
    // 1. Find all parts that are assigned to any tray AND have NOT been packaged yet.
    // This uses the repository method created to find truly available parts.
    List<Part> availableParts = partRepository.findByTrayIdIsNotNullAndIsPackagedIsFalse();

    // 2. Group parts by type to efficiently check availability and pick one
    Map<PartType, List<Part>> partsByType = availableParts.stream()
        .collect(Collectors.groupingBy(Part::getType));

    // 3. Check if we have at least one part for EVERY PartType defined
    boolean isSetComplete = true;
    for (PartType type : PartType.values()) {
      if (partsByType.getOrDefault(type, List.of()).isEmpty()) {
        isSetComplete = false;
        break;
      }
    }

    if (isSetComplete) {
      System.out.println("Packaging Station: Found a complete set for HalfAnimalProduct!");

      // 4. Create the HalfAnimalProduct
      HalfAnimalProduct halfAnimalProduct = new HalfAnimalProduct();
      List<Part> partsToMarkPackaged = new ArrayList<>();

      // 5. Collect the selected parts (one of each type) and mark them as packaged
      for (PartType type : PartType.values()) {
        // Take the first available part of this type
        Part partToUse = partsByType.get(type).get(0);

        // Add part to product
        halfAnimalProduct.addPart(partToUse);

        // IMPORTANT: Mark only this specific PART as packaged
        partToUse.setPackaged(true);
        partToUse.setProductId(halfAnimalProduct.getId());
        partsToMarkPackaged.add(partToUse);
      }

      // 6. Register the new HalfAnimalProduct and save the updated parts
      productRepository.save(halfAnimalProduct);
      partRepository.saveAll(partsToMarkPackaged); // <--- Save the parts with new status
      System.out.println("Packaging Station: Successfully created HalfAnimalProduct ID " + halfAnimalProduct.getId());

      // Recursive call: Immediately check if another half-animal can be made from the remaining parts
      attemptHalfAnimalPackaging();

    } else {
      System.out.println("Packaging Station: Insufficient parts (not all types available) for HalfAnimalProduct. Waiting...");
    }
  }
}
