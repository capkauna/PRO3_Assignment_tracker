package slaughterhouse.assignment.tracker.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean; // <-- NEW IMPORT
import org.springframework.context.ApplicationEventPublisher; // No longer a mock
import slaughterhouse.assignment.tracker.entities.Animal;
import slaughterhouse.assignment.tracker.events.AnimalArrivedEvent;
import slaughterhouse.assignment.tracker.repository.AnimalRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.timeout; // <-- NEW IMPORT
import static org.mockito.Mockito.verify;

/**
 * FIXED: This is now a @SpringBootTest.
 * It loads the full application context and uses the H2 in-memory database,
 * which fixes all "expected: <1> but was: <0>" ID errors.
 */
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = { "grpc.server.port=-1" } // Keep this to ensure this test gets a random port
)
    // @DirtiesContext is no longer needed
class ReceptionServiceTest
{

  @Autowired
  private ReceptionService receptionService;

  @Autowired
  private AnimalRepository animalRepository;

  // FIX: We are no longer mocking the publisher.
  // @MockBean
  // private ApplicationEventPublisher eventPublisher;

  // FIX: Instead, we create a "Spy" on the LISTENER (ButcheringService)
  // to verify it receives the event.
  @SpyBean
  private ButcheringService butcheringService;


  @Test
  void registerAnimal_SavesAnimalAndPublishesEvent_Success()
  {
    // 1. Arrange
    // We must create a valid Animal object first, as required by the service
    Animal animalToRegister = new Animal(120.5, "REG-123");

    // 2. Act
    // Call the service to register the animal
    Animal registeredAnimal = receptionService.registerAnimal(animalToRegister);

    // 3. Assert
    // Check that the animal from the service has a real, non-zero ID
    assertNotNull(registeredAnimal);
    assertTrue(registeredAnimal.getId() > 0, "The registered animal must have the generated ID.");
    assertEquals(120.5, registeredAnimal.getWeight());
    assertEquals("REG-123", registeredAnimal.getRegNo()); // <-- FIX: Corrected typo from "REG-1G23"

    // Verify it also exists in the database
    Optional<Animal> foundInDb = animalRepository.findById(registeredAnimal.getId());
    assertTrue(foundInDb.isPresent(), "Animal was not saved to the database.");
    assertEquals("REG-123", foundInDb.get().getRegNo());

    // FIX: Verify that the ButcheringService's event handler was called.
    // We use timeout(2000) to wait 2 seconds for the @Async method to run.
    verify(butcheringService, timeout(2000))
        .handleAnimalArrival(any(AnimalArrivedEvent.class));
  }

  @Test
  void findAnimalById_ReturnsAnimal_WhenFound()
  {
    // 1. Arrange
    // Save an animal directly to the H2 database
    Animal savedAnimal = animalRepository.save(new Animal(100.0, "FIND-ME"));
    int animalId = savedAnimal.getId();

    // Ensure it got a real ID
    assertTrue(animalId > 0, "Prerequisite: Saved animal must have a generated ID.");

    // 2. Act
    Animal foundAnimal = receptionService.findAnimalById(animalId);

    // 3. Assert
    assertNotNull(foundAnimal);
    assertEquals(animalId, foundAnimal.getId());
    assertEquals("FIND-ME", foundAnimal.getRegNo());
  }

  @Test
  void registerAnimal_ThrowsException_WhenWeightIsZero()
  {
    // Act & Assert
    // We assert that the Animal constructor (which contains the validation)
    // throws the correct exception.
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      new Animal(0.0, "REG-456");
    });

    assertEquals("Weight must be greater than 0", exception.getMessage());
  }

  @Test
  void registerAnimal_ThrowsException_WhenRegNoIsEmpty()
  {
    // Act & Assert
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      new Animal(100.0, "");
    });

    assertEquals("Registration number cannot be empty", exception.getMessage());
  }

  @Test
  void registerAnimal_ThrowsException_WhenRegNoIsNull()
  {
    // Act & Assert
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      new Animal(100.0, null);
    });

    assertEquals("Registration number cannot be empty", exception.getMessage());
  }
}

