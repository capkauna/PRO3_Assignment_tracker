package slaughterhouse.assignment.tracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import slaughterhouse.assignment.tracker.entities.Product;

/**
  * JPA will handle the mapping based on the inheritance strategy defined in Product.java.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Integer>
{

}
