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
