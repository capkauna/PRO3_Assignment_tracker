package slaughterhouse.assignment.tracker.services;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import slaughterhouse.assignment.tracker.entities.Animal;
import slaughterhouse.assignment.tracker.events.AnimalArrivedEvent;
import slaughterhouse.assignment.tracker.repository.AnimalRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReceptionService
{
//declaring the dependency
  private final AnimalRepository animalRepository;
  private final ApplicationEventPublisher eventPublisher;//special class for publishing events

  //constructor injection
  public ReceptionService(AnimalRepository animalRepository, ApplicationEventPublisher eventPublisher)
  {
    this.animalRepository = animalRepository;
    this.eventPublisher = eventPublisher;
  }

  public Animal registerAnimal(Animal animal)
  {
    //validation here is only necessary if the animal object is created by deserialisation
    //(from another database that might use the empty constructor instead of the builder)
    //since the entity class already has validation annotations on the fields
    if (animal.getWeight() <= 0 || animal.getRegNo() == null || animal.getRegNo().trim().isEmpty()) {
      throw new IllegalArgumentException("Weight must be positive and a valid registration number must be provided.");
    }
    Animal registeredAnimal = animalRepository.save(animal);

    AnimalArrivedEvent event = new AnimalArrivedEvent(registeredAnimal.getId());//creating event for butchering to hear about
    eventPublisher.publishEvent(event);

    return registeredAnimal;
  }

  public Animal findAnimalById(int id)
  {
    return animalRepository.findById(id).orElse(null);
  }

  public List<Animal> showAnimals()
  {
    return animalRepository.findAll();
  }

  public void show()
  {
    System.out.println("Reception Service active");
  }

//
//to remove after making testing classes
//
  public void clearRepository()
  {
    animalRepository.deleteAll();
    animalRepository.resetIdSequence();
  }
}
