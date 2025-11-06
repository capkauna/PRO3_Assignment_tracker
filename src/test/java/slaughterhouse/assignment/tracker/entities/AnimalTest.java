package slaughterhouse.assignment.tracker.entities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AnimalTest
{
  private Animal animal;

  @org.junit.jupiter.api.BeforeEach void setUp()
  {
    animal = new Animal(100.0, "REG123");
  }

  @org.junit.jupiter.api.AfterEach void tearDown()
  {
    animal = null;
  }

  @Test void testAnimalConstructor()
  {
    assertNotNull(animal);
    assertEquals(100.0, animal.getWeight());
    assertEquals("REG123", animal.getRegNo());
  }
  @Test void getId()
  {
    assertEquals(0, animal.getId());
  }

  @Test void setId()
  {
    animal.setId(10);
    assertEquals(10, animal.getId());
  }

  @Test void getWeight()
  {
    assertEquals(100.0, animal.getWeight());
  }

  @Test void setWeight()
  {
    animal.setWeight(1000.0);
    assertEquals(1000.0, animal.getWeight());
  }

  @Test void getRegNo()
  {
    assertEquals("REG123", animal.getRegNo());
  }

  @Test void setRegNo()
  {
    animal.setRegNo("REG456");
    assertEquals("REG456", animal.getRegNo());
  }

  @Test void isButchered()
  {
    assertFalse(animal.isButchered());
    animal.markAsButchered();
    assertTrue(animal.isButchered());
  }

  @Test void setButchered()
  {
    animal.setButchered(true);
    assertTrue(animal.isButchered());
    animal.setButchered(false);
    assertFalse(animal.isButchered());
  }

  @Test void markAsButchered()
  {
    assertFalse(animal.isButchered());
    animal.markAsButchered();
    assertTrue(animal.isButchered());
    assertThrows(IllegalStateException.class, () -> animal.markAsButchered());
  }

  @Test void testToString()
  {
    assertEquals("Animal{id=0, weight=100.0, regNo=REG123, registrationDate=null, origin=null, isButchered=false}", animal.toString());
    animal.markAsButchered();
    assertEquals("Animal{id=0, weight=100.0, regNo=REG123, registrationDate=null, origin=null, isButchered=true}", animal.toString());
  }
}