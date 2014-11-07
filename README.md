__OMG it's an OGM for TinkerPop 3!__

Gizmo
=====

Gizmo is an Object Graph Modelling (OGM) framework for TinkerPop 3 using annotations to bootstrap your POJOs to TinkerPop 3.

Usage
=====
```
@Vertex
public class AnimalShelter {
  private String name;
  
  @Relationship(label = "owns", direction = Direction.IN)
  private Person owner;
  
  // Getters and setters
}

@Vertex
public class Animal {
  @Id
  private Long id;
  
  @Relationship(label = "inhabits")
  private AnimalShelter shelter;
  
  // Getters and setters
}

@Vertex
public class Person {
  @Relationship(label = "owns")
  private AnimalShelter shelter;
  
  // Getters and setters
}
```

```
public class AnimalShelterRepository extends GizmoRepository<AnimalShelter, Long> {
  public AnimalShelterRepository(GizmoGraph graph) {
    super(graph);
  }
}

public class AnimalRepository extends GizmoRepository<Animal, Long> {
  public AnimalRepository(GizmoGraph graph) {
    super(graph);
  }
}

public class PersonRepository extends GizmoRepository<Person, Long> {
  public PersonRepository(GizmoGraph graph) {
    super(graph);
  }
}
```

```
// Find
Set<Animal> animals = animalRepository.find();
Animal animal = animalRepository.find(123L);
Set<Animal> animals = animalRepository.find("name", "Fido");

// Delete
animalRepository.delete(123L, 321L);
animalRepository.delete(animal);

// Save
Animal animal = animalRepository.save(animal);
Set<Animal> animals = animalRepository.save(animal, someOtherAnimal);

// Update
Animal animalRepository.update(animal);
Set<Animal> animals = animalRepository.update(animal, someOtherAnimal);

// Count
long count = animalRepository.count();
long count = animalRepository.count("name);
long count = animalRepository.count("name", "Fido");
```
