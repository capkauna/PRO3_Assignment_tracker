package slaughterhouse.assignment.tracker.client;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import slaughterhouse.assignment.tracker.dtos.AnimalInfoResponseDTO;
import slaughterhouse.assignment.tracker.dtos.AnimalRegistrationRequestDTO;

import java.time.LocalDate;
import java.util.List;

@Component
public class AnimalRegistrationClient {

    private final RestClient restClient;

    // RestClient bean is injected from RestClientConfig
    public AnimalRegistrationClient(RestClient restClient) {
        this.restClient = restClient;
    }

    // POST register a new animal
    public AnimalInfoResponseDTO registerAnimal(AnimalRegistrationRequestDTO request) {
        return restClient.post()
                .uri("/animals")  // baseUrl + /animals = https://localhost:8443/api/animals
                .body(request)
                .retrieve()
                .body(AnimalInfoResponseDTO.class);
    }

    // GET animal by ID
    public AnimalInfoResponseDTO getAnimalById(int id) {
        return restClient.get()
                .uri("/animals/{id}", id)
                .retrieve()
                .body(AnimalInfoResponseDTO.class);
    }

    // GET all animals
    public List<AnimalInfoResponseDTO> getAllAnimals() {
        return restClient.get()
                .uri("/animals")
                .retrieve()
                .body(new ParameterizedTypeReference<List<AnimalInfoResponseDTO>>() {});
    }

    // GET animals by date
    public List<AnimalInfoResponseDTO> getAnimalsByDate(LocalDate date) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/animals")
                        .queryParam("date", date.toString())
                        .build())
                .retrieve()
                .body(new ParameterizedTypeReference<List<AnimalInfoResponseDTO>>() {});
    }

    // GET animals by origin
    public List<AnimalInfoResponseDTO> getAnimalsByOrigin(String origin) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/animals")
                        .queryParam("origin", origin)
                        .build())
                .retrieve()
                .body(new ParameterizedTypeReference<List<AnimalInfoResponseDTO>>() {});
    }

    // 404 handling using onStatus
    public AnimalInfoResponseDTO getAnimalByIdStrict(int id) {
        return restClient.get()
                .uri("/animals/{id}", id)
                .retrieve()
                .onStatus(status -> status.value() == 404, (request, response) -> {
                    throw new RuntimeException("Animal not found: " + id);
                })
                .body(AnimalInfoResponseDTO.class);
    }
}
