package slaughterhouse.assignment.tracker;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync; // NEW IMPORT

@SpringBootApplication
@EnableJpaRepositories // Ensure JPA repositories are scanned
@EnableAsync // REQUIRED to process @Async event listeners
public class TrackerApplication
{
  public static void main(String[] args) {
    SpringApplication.run(TrackerApplication.class, args);
  }
}





/*
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.Async;
import slaughterhouse.assignment.tracker.entities.Animal;
import slaughterhouse.assignment.tracker.services.ReceptionService;

import java.util.List;
@Async
@SpringBootApplication public class TrackerApplication
{



  public static void main(String[] args)
  {
    ConfigurableApplicationContext context = SpringApplication.run(TrackerApplication.class, args);


    //in console for testing: java -jar tracker.jar --spring.profiles.active=test
    //or in run/debug configuration Active Profiles: test
    //Receptionservice testing
    try {
      ReceptionService receptionService = context.getBean(ReceptionService.class);
      receptionService.show();

      System.out.println("\n--- Testing Registration Logic ---");

      Animal animal1 = new Animal(400.0, "BEEF-001");
      Animal animal2 = new Animal(400.0, "BEEF-002");
      System.out.println("Attempting to register: " + animal1.getRegNo());

      //register in db
      Animal registeredAnimal1 = receptionService.registerAnimal(animal1);
      Animal registeredAnimal2 = receptionService.registerAnimal(animal2);

      System.out.println("✅ Successfully registered Animals "
          + registeredAnimal1.getRegNo() +" and " +registeredAnimal2.getRegNo()
          + " with ID: "
          + registeredAnimal1.getId() + " and " + registeredAnimal2.getId());

      //find animal by id
      Animal foundAnimal1 = receptionService.findAnimalById(registeredAnimal1.getId());

      System.out.println("✅ Animal " + foundAnimal1.getRegNo() + " found with ID: " + foundAnimal1.getId());

      System.out.println("\n--- 📋 Current Animal Inventory ---");
      List<Animal> allAnimals = receptionService.showAnimals();

      if (allAnimals.isEmpty()) {
        System.out.println("Inventory is empty.");
      } else {
        // Print a readable header
        System.out.println("| ID:   | RegNo:     | Weight:      |");
        System.out.println("|------ |------------|--------------|");

        // Print each animal using the new toString() method
        allAnimals.forEach(System.out::println);
      }

      //clear the repository every now and then after testing here
//      receptionService.clearRepository();
//      System.out.println("✅ In-memory repository cleared.");

    } catch (Exception e) {
      // Catch exceptions
      System.err.println("❌ An error occurred during service execution: " + e.getMessage());
    } finally {
      // Close the context to shut down Spring and the database connection pool.
      System.out.println("\n--- Shutting Down Spring Context ---");
      context.close();
      // Alternatively: System.exit(SpringApplication.exit(context));
    }


  }

}

 */
