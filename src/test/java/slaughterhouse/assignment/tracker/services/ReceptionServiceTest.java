package slaughterhouse.assignment.tracker.services;

import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;
import slaughterhouse.assignment.tracker.entities.Animal;
import slaughterhouse.assignment.tracker.repository.AnimalRepository;

import static org.junit.jupiter.api.Assertions.*;

class ReceptionServiceTest
{

  //to be added
  private ReceptionService receptionService;
  private AnimalRepository animalRepository;
  private ApplicationEventPublisher eventPublisher;
  private Animal animal;

  @org.junit.jupiter.api.BeforeEach void setUp()
  {
    animalRepository = null;
    receptionService = new ReceptionService(null, null);
  }

  @org.junit.jupiter.api.AfterEach void tearDown()
  {
    receptionService = null;
  }

  @Test void registerAnimal()
  {
    assertNotNull(receptionService);
  }

  @Test void findAnimalById()
  {
  }

  @Test void showAnimals()
  {
  }

  @Test void show()
  {
  }

  @Test void clearRepository()
  {
  }
}