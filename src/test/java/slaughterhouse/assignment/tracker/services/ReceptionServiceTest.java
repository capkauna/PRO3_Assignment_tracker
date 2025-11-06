package slaughterhouse.assignment.tracker.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.annotation.DirtiesContext;
import slaughterhouse.assignment.tracker.dtos.AnimalInfoResponseDTO;
import slaughterhouse.assignment.tracker.dtos.AnimalRegistrationRequestDTO;
import slaughterhouse.assignment.tracker.entities.Animal;
import slaughterhouse.assignment.tracker.events.AnimalArrivedEvent;
import slaughterhouse.assignment.tracker.repository.AnimalRepository;

import java.time.LocalDate; // <-- NEW IMPORT
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

/**
 * FIXED: This is now a @SpringBootTest.
 * It loads the full application context and uses the H2 in-memory database,
 * which fixes all "expected: <1> but was: <0>" ID errors.
 */
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = { "grpc.server.port=-1" } // ensuring this test gets a random port
)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ReceptionServiceTest
{

  @Autowired
  private ReceptionService receptionService;

  @Autowired
  private AnimalRepository animalRepository;

  @SpyBean
  private ButcheringService butcheringService;

  // This ensures the database is clean before each @Test
  @BeforeEach
  void setUp() {
    animalRepository.deleteAll();
    // Note: resetIdSequence() might be H2-specific or require a custom query.
    // For test isolation, deleteAll() is usually sufficient.
  }


  @Test
  void registerAnimal_SavesAnimalAndPublishesEvent_Success()
  {
    // 1. Arrange
    // Create the DTO, just like the controller test
    AnimalRegistrationRequestDTO request = new AnimalRegistrationRequestDTO();
    request.setWeight(120.5);
    request.setRegNo("REG-123");
    request.setOrigin("Test Farm");
    request.setDate(LocalDate.now());

    // 2. Act
    // Call the service to register the animal
    AnimalInfoResponseDTO registeredAnimalDTO = receptionService.registerAnimal(request);

    // 3. Assert
    // Check that the animal from the service has a real, non-zero ID
    assertNotNull(registeredAnimalDTO);
    assertTrue(registeredAnimalDTO.getAnimalid() > 0, "The registered animal must have the generated ID.");
    assertEquals(120.5, registeredAnimalDTO.getWeight());
    assertEquals("REG-123", registeredAnimalDTO.getRegNo());
    assertEquals("Test Farm", registeredAnimalDTO.getOrigin());

    // Verify it also exists in the database
    Optional<Animal> foundInDb = animalRepository.findById(registeredAnimalDTO.getAnimalid());
    assertTrue(foundInDb.isPresent(), "Animal was not saved to the database.");
    assertEquals("REG-123", foundInDb.get().getRegNo());

    // Verify that the ButcheringService's event handler was called.
    // We use timeout(2000) to wait 2 seconds for the @Async method to run.
    verify(butcheringService, timeout(2000))
        .handleAnimalArrival(any(AnimalArrivedEvent.class));
  }

  // --- NEW TEST ---
  @Test
  void registerAnimal_ThrowsException_WhenRegNoAlreadyExists() {
    // 1. Arrange
    // Manually save an animal to the DB first
    AnimalRegistrationRequestDTO request1 = new AnimalRegistrationRequestDTO();
    request1.setWeight(100.0);
    request1.setRegNo("DUPE-REG");
    request1.setOrigin("Farm A");
    request1.setDate(LocalDate.now());

    receptionService.registerAnimal(request1); // This one should succeed

    // Create a new request with the same regNo
    AnimalRegistrationRequestDTO request2 = new AnimalRegistrationRequestDTO();
    request2.setWeight(200.0);
    request2.setRegNo("DUPE-REG");
    request2.setOrigin("Farm B");
    request2.setDate(LocalDate.now());

    // 2. Act & 3. Assert
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      receptionService.registerAnimal(request2); // This one should fail
    });

    assertEquals("Animal with registration number DUPE-REG already exists.", exception.getMessage());
  }

  @Test
  void findAnimalById_ReturnsAnimal_WhenFound()
  {
    // 1. Arrange
    // Use the service to register an animal
    AnimalRegistrationRequestDTO request = new AnimalRegistrationRequestDTO();
    request.setWeight(100.0);
    request.setRegNo("FIND-ME");
    request.setOrigin("Find Farm");
    request.setDate(LocalDate.now());
    AnimalInfoResponseDTO savedAnimal = receptionService.registerAnimal(request);

    int animalId = savedAnimal.getAnimalid();
    assertTrue(animalId > 0, "Prerequisite: Saved animal must have a generated ID.");

    // 2. Act
    AnimalInfoResponseDTO foundAnimal = receptionService.findAnimalById(animalId);

    // 3. Assert
    assertNotNull(foundAnimal);
    assertEquals(animalId, foundAnimal.getAnimalid());
    assertEquals("FIND-ME", foundAnimal.getRegNo());
  }

  //  REMOVED old validation tests
  // The tests for weight and regNo are removed because the
  // service no longer contains this logic. It is now handled by
  // DTO validation (in the controller) and the Animal entity's constructor.
}

