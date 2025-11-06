package slaughterhouse.assignment.tracker.mappers;

import slaughterhouse.assignment.tracker.dtos.AnimalInfoResponseDTO;
import slaughterhouse.assignment.tracker.dtos.AnimalRegistrationRequestDTO;
import slaughterhouse.assignment.tracker.entities.Animal;

import java.util.List;
import java.util.stream.Collectors;

//Single Responsibility Principle
//Dedicated class for mapping between the DTOs and the origin entity
public class AnimalMapper
{

  public static AnimalInfoResponseDTO toDTO(Animal animal)
  {
    if (animal == null)
    {
      return null;
    }
    return new AnimalInfoResponseDTO(animal.getId(), animal.getWeight(),
        animal.getRegNo(), animal.getRegistrationDate(), animal.getOrigin(),
        animal.isButchered());
  }

  public static List<AnimalInfoResponseDTO> toDTOList(List<Animal> animals)
  {
    return animals.stream().map(AnimalMapper::toDTO)
        .collect(Collectors.toList());
  }

  //
  public static Animal toEntity(AnimalRegistrationRequestDTO request)
  {
    if (request == null)
    {
      return null;
    }

    return new Animal(request.getWeight(), request.getRegNo(),
        request.getDate(), request.getOrigin());
  }
}
