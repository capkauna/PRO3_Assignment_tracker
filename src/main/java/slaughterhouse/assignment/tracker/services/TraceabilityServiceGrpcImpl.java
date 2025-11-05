package slaughterhouse.assignment.tracker.services;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.stereotype.Service;
import slaughterhouse.assignment.grpc.TraceabilityServiceGrpc;
import slaughterhouse.assignment.grpc.ProductId;
import slaughterhouse.assignment.grpc.AnimalLookup;
import slaughterhouse.assignment.grpc.AnimalRegNo;
import slaughterhouse.assignment.grpc.AnimalRegNoList;
import slaughterhouse.assignment.grpc.ProductIdentifier;
import slaughterhouse.assignment.grpc.ProductIdentifierList;

import slaughterhouse.assignment.tracker.entities.Animal;
import slaughterhouse.assignment.tracker.entities.Part;
import slaughterhouse.assignment.tracker.entities.Product;
import slaughterhouse.assignment.tracker.repository.AnimalRepository;
import slaughterhouse.assignment.tracker.repository.PartRepository;
import slaughterhouse.assignment.tracker.repository.ProductRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@GrpcService // Marks this class as a gRPC service for the Spring Boot starter
public class TraceabilityServiceGrpcImpl extends TraceabilityServiceGrpc.TraceabilityServiceImplBase
{
  private final AnimalRepository animalRepository;
  private final PartRepository partRepository;
  private final ProductRepository productRepository;

  public TraceabilityServiceGrpcImpl(AnimalRepository animalRepository,
      PartRepository partRepository,
      ProductRepository productRepository)
  {
    this.animalRepository = animalRepository;
    this.partRepository = partRepository;
    this.productRepository = productRepository;
  }

  /**
   * R1: Retrieves registration numbers for all animals that contributed to a product.
   * Logic: Query Parts by productId -> Get distinct Animal IDs -> Get RegNos.
   */
  @Override
  public void getAnimalRegNosByProduct(ProductId request, StreamObserver<AnimalRegNoList> responseObserver)
  {
    int productId = request.getId();

    // 1. Find all parts that belong to this final product
    List<Part> parts = partRepository.findByProductId(productId);

    if (parts.isEmpty()) {
      // If no parts are found, return an empty list gracefully
      AnimalRegNoList response = AnimalRegNoList.newBuilder().build();
      responseObserver.onNext(response);
      responseObserver.onCompleted();
      return;
    }

    // 2. Get distinct Animal IDs from these parts
    Set<Integer> animalIds = parts.stream()
        .map(Part::getAnimalId)
        .collect(Collectors.toSet());

    // 3. Look up Animal RegNos for the distinct IDs
    List<AnimalRegNo> regNos = animalRepository.findAllById(animalIds).stream()
        .map(animal -> AnimalRegNo.newBuilder().setRegNo(animal.getRegNo()).build())
        .collect(Collectors.toList());

    // 4. Build and send response
    AnimalRegNoList response = AnimalRegNoList.newBuilder().addAllRegNos(regNos).build();

    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  /**
   * R2: Retrieves all products an animal has been involved in (by ID or RegNo).
   * Logic: Resolve Animal ID -> Query Parts by Animal ID -> Get distinct Product IDs -> Get Product Type.
   */
  @Override
  public void getProductsByAnimal(AnimalLookup request, StreamObserver<ProductIdentifierList> responseObserver)
  {
    int animalId;

    // 1. Resolve Animal ID (handling both ID and RegNo lookup)
    if (request.hasId()) {
      animalId = request.getId();
    } else if (request.hasRegNo()) {
      // Look up Animal by RegNo
      Animal animal = animalRepository.findByRegNo(request.getRegNo())
          .orElse(null); //orElse is only available for Object<Animal> searches

      if (animal == null) {
        responseObserver.onError(new IllegalArgumentException("Animal with registration number " + request.getRegNo() + " not found."));
        return;
      }
      animalId = animal.getId();
    } else {
      responseObserver.onError(new IllegalArgumentException("Missing animal identifier (ID or RegNo)."));
      return;
    }

    // 2. Find all parts that originated from this animal
    List<Part> parts = partRepository.findByAnimalId(animalId);

    // 3. Get distinct Product IDs from these parts that have been packaged
    Set<Integer> packagedProductIds = parts.stream()
        .filter(part -> part.getProductId() != null) // Only include parts used in a final product
        .map(Part::getProductId)
        .collect(Collectors.toSet());

    // 4. Look up the Product entities to determine their type
    List<ProductIdentifier> products = productRepository.findAllById(packagedProductIds).stream()
        .map(product -> ProductIdentifier.newBuilder()
            .setProductId(product.getId())
            // product.getClass().getSimpleName() gives us "SameTypeProduct" or "HalfAnimalProduct"
            .setProductType(product.getClass().getSimpleName())
            .build())
        .collect(Collectors.toList());


    // 5. Build and send response
    ProductIdentifierList response = ProductIdentifierList.newBuilder().addAllProducts(products).build();

    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }
}
