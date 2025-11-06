package slaughterhouse.assignment.tracker.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import slaughterhouse.assignment.tracker.dtos.AnimalInfoResponseDTO;
import slaughterhouse.assignment.tracker.dtos.AnimalRegistrationRequestDTO;
import slaughterhouse.assignment.tracker.services.ReceptionService;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/animals") // Base path for all methods in this controller
public class AnimalRegistrationController {

  private final ReceptionService receptionService;

  @Autowired
  public AnimalRegistrationController(ReceptionService receptionService) {
    this.receptionService = receptionService;
  }

  @PostMapping
  public ResponseEntity<AnimalInfoResponseDTO> registerAnimal(@Valid @RequestBody AnimalRegistrationRequestDTO request) {
    // The @Valid annotation automatically checks the DTO (e.g., @Positive, @NotBlank)

    // The service does the work and returns the response DTO
    AnimalInfoResponseDTO registeredAnimal = receptionService.registerAnimal(request);

    // Return 201 Created status with the new DTO in the body
    return new ResponseEntity<>(registeredAnimal, HttpStatus.CREATED);
  }


  @GetMapping("/{id}")
  public ResponseEntity<AnimalInfoResponseDTO> getAnimalById(@PathVariable int id) {
    AnimalInfoResponseDTO animal = receptionService.findAnimalById(id);

    if (animal != null) {
      return ResponseEntity.ok(animal); // 200 OK
    } else {
      return ResponseEntity.notFound().build(); // 404 Not Found
    }
  }

  /**
   * Gets a list of animals, with optional filtering by date or origin.
   * Handles GET requests to:
   * - /api/animals
   * - /api/animals?date=2025-11-06
   * - /api/animals?origin=Test Farm
   *
   * @param date (Optional) The registration date to filter by.
   * @param origin (Optional) The origin farm to filter by.
   * @return A list of animals matching the filter (200 OK).
   */
  @GetMapping
  public ResponseEntity<List<AnimalInfoResponseDTO>> getAnimals(
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
      @RequestParam(required = false) String origin) {

    List<AnimalInfoResponseDTO> animals;

    if (date != null) {
      // Find by date
      animals = receptionService.findAnimalsByDate(date);
    } else if (origin != null) {
      // Find by origin
      animals = receptionService.findAnimalsByOrigin(origin);
    } else {
      // Get all
      animals = receptionService.showAnimals();
    }

    return ResponseEntity.ok(animals); // 200 OK
  }

  /**
   * Exception handler for validation errors from @Valid.
   * This catches the MethodArgumentNotValidException and converts it into a
   * clean JSON error message with a 400 Bad Request status.
   */
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public Map<String, List<String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
    Map<String, List<String>> errors = new HashMap<>();

    List<String> errorMessages = ex.getBindingResult()
        .getFieldErrors()
        .stream()
        .map(error -> error.getField() + ": " + error.getDefaultMessage()) // e.g., "weight: Weight must be a positive number"
        .collect(Collectors.toList());

    errors.put("errors", errorMessages);
    return errors;
  }

  /**
   * Exception handler for duplicate registration numbers.
   * Catches the specific error from the service and returns a 409 Conflict.
   */
  @ResponseStatus(HttpStatus.CONFLICT)
  @ExceptionHandler(IllegalArgumentException.class)
  public Map<String, String> handleIllegalArgumentException(IllegalArgumentException ex) {
    Map<String, String> error = new HashMap<>();
    error.put("error", ex.getMessage());
    return error;
  }
}

