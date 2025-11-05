package slaughterhouse.assignment.tracker;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

// Added webEnvironment to use a random port and avoid "Address already in use"
// so different tests can run in parallel
// FIX 1: Add "properties" to force gRPC to a random port
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class TrackerApplicationTests
{

  @Test void contextLoads()
  {
  }

}
