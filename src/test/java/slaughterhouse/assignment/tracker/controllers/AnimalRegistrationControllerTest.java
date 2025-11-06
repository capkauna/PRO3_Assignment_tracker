package slaughterhouse.assignment.tracker.controllers;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
// --- 1. IMPORT THIS ---
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import slaughterhouse.assignment.tracker.dtos.AnimalInfoResponseDTO;
import slaughterhouse.assignment.tracker.dtos.AnimalRegistrationRequestDTO;
import slaughterhouse.assignment.tracker.repository.AnimalRepository;
import slaughterhouse.assignment.tracker.repository.PartRepository;
import slaughterhouse.assignment.tracker.repository.ProductRepository;
import slaughterhouse.assignment.tracker.repository.TrayRepository;
import slaughterhouse.assignment.tracker.services.ButcheringService;
import slaughterhouse.assignment.tracker.services.PackagingService;
import slaughterhouse.assignment.tracker.services.ReceptionService;
import slaughterhouse.assignment.tracker.services.TraceabilityServiceGrpcImpl;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// This is the "exclusion" fix. It tells @WebMvcTest NOT to load
// the gRPC services, which stops it from trying to load the database.
@EnableAutoConfiguration(exclude = {
    net.devh.boot.grpc.server.autoconfigure.GrpcServerAutoConfiguration.class,
    net.devh.boot.grpc.server.autoconfigure.GrpcServerFactoryAutoConfiguration.class,
    net.devh.boot.grpc.server.autoconfigure.GrpcServerSecurityAutoConfiguration.class,
    net.devh.boot.grpc.server.autoconfigure.GrpcServerMetricAutoConfiguration.class,
    //net.devh.boot.grpc.server.autoconfigure.GrpcServerHealthAutoConfiguration.class,
   // net.devh.boot.grpc.server.autoconfigure.GrpcServerTracerAutoConfiguration.class,
  //  net.devh.boot.grpc.server.autoconfigure.GrpcServerNetSecurityAutoConfiguration.class
})



/**
 * Unit test for the AnimalRegistrationController.
 *
 * @WebMvcTest loads only the web layer (not the full application)
 * and auto-configures MockMvc.
 */
@WebMvcTest(AnimalRegistrationController.class)
public class AnimalRegistrationControllerTest
{
  @Autowired
  private MockMvc mockMvc; // The main tool for sending fake HTTP requests

  @Autowired
  private ObjectMapper objectMapper; // For converting Java objects to JSON strings

  // We must mock EVERY service bean, because @WebMvcTest does not
  // provide the database dependencies they need.

  @MockBean
  private ReceptionService receptionService; // This is the one we actually use

  // --- MOCKS FOR ALL OTHER SERVICES ---
  @MockBean
  private ButcheringService butcheringService;

  @MockBean
  private PackagingService packagingService;

  @MockBean
  private TraceabilityServiceGrpcImpl traceabilityServiceGrpcImpl;

  // --- MOCKS FOR ALL REPOSITORIES ---
  // These are required because @EnableJpaRepositories in your main app
  // is still trying to create them.
  @MockBean
  private AnimalRepository animalRepository;
  @MockBean
  private PartRepository partRepository;
  @MockBean
  private TrayRepository trayRepository;
  @MockBean
  private ProductRepository productRepository;

  @Test void registerAnimal_Success() throws Exception
  {
    // 1. Arrange
    // Create the DTO that we will send as JSON
    AnimalRegistrationRequestDTO request = new AnimalRegistrationRequestDTO();
    request.setWeight(150.0);
    request.setRegNo("REG-500");
    request.setOrigin("Test Farm");
    request.setDate(LocalDate.now());

    // Create the DTO that we expect the service to return
    AnimalInfoResponseDTO returnedDTO = new AnimalInfoResponseDTO();
    returnedDTO.setAnimalid(1); // Simulate a generated ID
    returnedDTO.setWeight(150.0);
    returnedDTO.setRegNo("REG-500");
    returnedDTO.setOrigin("Test Farm");
    returnedDTO.setRegistrationDate(LocalDate.now());
    returnedDTO.setButchered(false);

    // Tell the mock service what to do
    given(receptionService.registerAnimal(any(AnimalRegistrationRequestDTO.class))).willReturn(returnedDTO);

    // 2. Act & 3. Assert
    mockMvc.perform(post("/api/animals").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated()) // Expect 201 Created
        .andExpect(jsonPath("$.animalid").value(1))
        .andExpect(jsonPath("$.regNo").value("REG-500"))
        .andExpect(jsonPath("$.origin").value("Test Farm"));
  }

  @Test void registerAnimal_InvalidRequest_ReturnsBadRequest() throws Exception
  {
    // 1. Arrange
    // Create a DTO with invalid data (weight is missing/0)
    AnimalRegistrationRequestDTO invalidRequest = new AnimalRegistrationRequestDTO();
    invalidRequest.setRegNo("REG-123");
    invalidRequest.setOrigin("Test Farm");
    invalidRequest.setDate(LocalDate.now());
    // We left weight as 0, which violates @Positive

    // 2. Act & 3. Assert
    mockMvc.perform(post("/api/animals").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidRequest)))
        .andExpect(status().isBadRequest()) // Expect 400 Bad Request
        // Check if the error message from the validation handler is present
        .andExpect(jsonPath("$.errors[0]").value(
            "weight: Weight must be a positive number"));
  }

  @Test void getAnimalById_Success() throws Exception
  {
    // 1. Arrange
    // Create the DTO that we expect the service to return
    AnimalInfoResponseDTO animalDTO = new AnimalInfoResponseDTO();
    animalDTO.setAnimalid(1);
    animalDTO.setRegNo("FOUND-ME");
    animalDTO.setOrigin("Get Farm");
    animalDTO.setRegistrationDate(LocalDate.now());

    given(receptionService.findAnimalById(1)).willReturn(animalDTO);

    // 2. Act & 3. Assert
    mockMvc.perform(get("/api/animals/1")).andExpect(status().isOk())
        .andExpect(jsonPath("$.regNo").value("FOUND-ME"))
        .andExpect(jsonPath("$.animalid").value(1));
  }

  @Test void getAnimalById_NotFound() throws Exception
  {
    // 1. Arrange
    given(receptionService.findAnimalById(99)).willReturn(null);

    // 2. Act & 3. Assert
    mockMvc.perform(get("/api/animals/99"))
        .andExpect(status().isNotFound()); // Expect 404 Not Found
  }

  @Test void getAnimals_ByDate() throws Exception
  {
    // 1. Arrange
    LocalDate date = LocalDate.of(2025, 11, 6);
    // Create the DTO that we expect the service to return
    AnimalInfoResponseDTO animalDTO = new AnimalInfoResponseDTO();
    animalDTO.setAnimalid(1);
    animalDTO.setRegNo("DATE-ANIMAL");
    animalDTO.setOrigin("Date Farm");
    animalDTO.setRegistrationDate(date);

    given(receptionService.findAnimalsByDate(date)).willReturn(List.of(animalDTO));

    // 2. Act & 3. Assert
    mockMvc.perform(get("/api/animals?date=2025-11-06"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].regNo").value("DATE-ANIMAL"));
  }

  @Test void getAnimals_ByOrigin() throws Exception
  {
    // 1. Arrange
    String origin = "Origin Farm";
    // Create the DTO that we expect the service to return
    AnimalInfoResponseDTO animalDTO = new AnimalInfoResponseDTO();
    animalDTO.setAnimalid(1);
    animalDTO.setRegNo("ORIGIN-ANIMAL");
    animalDTO.setOrigin(origin);
    animalDTO.setRegistrationDate(LocalDate.now());

    given(receptionService.findAnimalsByOrigin(origin)).willReturn(List.of(animalDTO));

    // 2. Act & 3. Assert
    mockMvc.perform(get("/api/animals?origin=Origin Farm"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].origin").value("Origin Farm"));
  }
}

