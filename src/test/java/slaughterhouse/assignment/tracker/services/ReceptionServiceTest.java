package slaughterhouse.assignment.tracker.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import slaughterhouse.assignment.tracker.entities.Animal;
import slaughterhouse.assignment.tracker.events.AnimalArrivedEvent;
import slaughterhouse.assignment.tracker.repository.AnimalRepository;

import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// Activates the Mockito testing framework for JUnit 5
@ExtendWith(MockitoExtension.class)
class ReceptionServiceTest {

  // 1. Mock the dependencies (Repositories and Publisher)
  @Mock
  private AnimalRepository animalRepository;

  @Mock
  private ApplicationEventPublisher eventPublisher;

  // 2. Inject the Mocks into the Service instance being tested
  @InjectMocks
  private ReceptionService receptionService;

  private final int ANIMAL_ID = 1;
  private final String REG_NO = "BEEF-007";
  private final double VALID_WEIGHT = 450.0;

  // =================================================================
  //                   TESTS FOR registerAnimal()
  // =================================================================

  @Test
  void registerAnimal_SavesAnimalAndPublishesEvent_Success() {
    // Arrange
    Animal newAnimal = new Animal(VALID_WEIGHT, REG_NO);
    // Create the expected saved animal (with the ID assigned by the DB)
    Animal registeredAnimal = new Animal(VALID_WEIGHT, REG_NO);

    // Define Mock Behavior: When the service calls save(newAnimal), return the registeredAnimal
    when(animalRepository.save(newAnimal)).thenReturn(registeredAnimal);

    // Act
    Animal result = receptionService.registerAnimal(newAnimal);

    // Assert
    // 1. Verify the result is correct
    assertNotNull(result);
    assertEquals(ANIMAL_ID, result.getId(), "The registered animal must have the generated ID.");

    // 2. Verify Repository Interaction (Persistence)
    verify(animalRepository, times(1)).save(newAnimal);

    // 3. Verify Event Interaction (Decoupling)
    // Verify that the AnimalArrivedEvent was published exactly once with the correct ID
    verify(eventPublisher, times(1)).publishEvent(any(AnimalArrivedEvent.class));

    // Optional: Verify the event was published with the specific ID (Stronger Assertion)
    AnimalArrivedEvent expectedEvent = new AnimalArrivedEvent(ANIMAL_ID);
    verify(eventPublisher, times(1)).publishEvent(eq(expectedEvent));
  }

  // --- Validation Tests ---

  @Test
  void registerAnimal_ThrowsException_WhenWeightIsZero() {
    // Arrange
    Animal zeroWeightAnimal = new Animal(0.0, REG_NO);

    // Act & Assert
    assertThrows(IllegalArgumentException.class, () -> {
      receptionService.registerAnimal(zeroWeightAnimal);
    }, "Should throw exception for zero weight.");

    // Verify that no database save or event was published
    verify(animalRepository, never()).save(any(Animal.class));
    verify(eventPublisher, never()).publishEvent(any());
  }

  @Test
  void registerAnimal_ThrowsException_WhenRegNoIsEmpty() {
    // Arrange
    Animal emptyRegNoAnimal = new Animal(VALID_WEIGHT, "");

    // Act & Assert
    assertThrows(IllegalArgumentException.class, () -> {
      receptionService.registerAnimal(emptyRegNoAnimal);
    }, "Should throw exception for empty registration number.");
  }

  // =================================================================
  //                  TESTS FOR findAnimalById()
  // =================================================================

  @Test
  void findAnimalById_ReturnsAnimal_WhenFound() {
    // Arrange
    Animal foundAnimal = new Animal(VALID_WEIGHT, REG_NO);
    when(animalRepository.findById(ANIMAL_ID)).thenReturn(Optional.of(foundAnimal));

    // Act
    Animal result = receptionService.findAnimalById(ANIMAL_ID);

    // Assert
    assertNotNull(result);
    assertEquals(ANIMAL_ID, result.getId());
  }

  @Test
  void findAnimalById_ReturnsNull_WhenNotFound() {
    // Arrange
    when(animalRepository.findById(ANIMAL_ID)).thenReturn(Optional.empty());

    // Act
    Animal result = receptionService.findAnimalById(ANIMAL_ID);

    // Assert
    assertNull(result);
  }
}