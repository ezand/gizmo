__OMG it's an OGM for TinkerPop 3!__

Gizmo
=====

Gizmo is an Object Graph Modelling (OGM) framework for TinkerPop 3 using annotations to weave your POJOs to 
the TinkerPop 3 functionality.

Usage
=====
```
@Vertex
public class PetShop {
  private String name;
  
  // Getters and setters
}
```

```
public class PetShopRepository extends GizmoRepository<PetShop, Long> {
  public PetShopRepository(GizmoGraph graph) {
    super(graph);
  }
}
```