package slaughterhouse.assignment.tracker.services;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import slaughterhouse.assignment.tracker.dtos.AnimalInfoResponseDTO;
import slaughterhouse.assignment.tracker.dtos.AnimalRegistrationRequestDTO;
import slaughterhouse.assignment.tracker.entities.Animal;
import slaughterhouse.assignment.tracker.events.AnimalArrivedEvent;
import slaughterhouse.assignment.tracker.mappers.AnimalMapper;
import slaughterhouse.assignment.tracker.repository.AnimalRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

  //making sure the event is published
  @Transactional public AnimalInfoResponseDTO registerAnimal(
      AnimalRegistrationRequestDTO request)
  {
    // Check if animal with this regNo already exists
    Optional<Animal> existing = animalRepository.findByRegNo(request.getRegNo());
    if (existing.isPresent())
    {
      throw new IllegalArgumentException(
          "Animal with registration number " + request.getRegNo() + " already exists.");
    }

    // Convert DTO to Animal entity
    Animal animal = new Animal(request.getWeight(), request.getRegNo(),
        request.getDate(), request.getOrigin());
    Animal registeredAnimal = animalRepository.save(animal);

    AnimalArrivedEvent event = new AnimalArrivedEvent(registeredAnimal.getId());//creating event for butchering to hear about
    eventPublisher.publishEvent(event);

    return AnimalMapper.toDTO(registeredAnimal);
  }

  public AnimalInfoResponseDTO findAnimalById(int id)
  {
    return AnimalMapper.toDTO(animalRepository.findById(id).orElse(null));
  }

  public List<AnimalInfoResponseDTO> showAnimals()
  {
    return AnimalMapper.toDTOList(animalRepository.findAll());
  }

  public void show()
  {
    System.out.println("Reception Service active");
  }

  //rest-friendly methods

   public List<AnimalInfoResponseDTO> findAnimalsByDate(LocalDate date) {
    return AnimalMapper.toDTOList(animalRepository.findByRegistrationDate(date));
  }

  public List<AnimalInfoResponseDTO> findAnimalsByOrigin(String origin) {
    return AnimalMapper.toDTOList(animalRepository.findByOrigin(origin));
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
