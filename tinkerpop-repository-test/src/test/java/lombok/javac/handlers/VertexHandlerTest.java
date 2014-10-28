package lombok.javac.handlers;

import static com.ezand.tinkerpop.repository.utils.GraphUtil.fromElement;

import org.junit.Test;

import com.ezand.tinkerpop.repository.AnimalShelter;
import com.tinkerpop.gremlin.structure.Vertex;
import com.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;

public class VertexHandlerTest {
    @Test
    public void foo() {
        TinkerGraph graph = TinkerGraph.open();
        Vertex vertex = graph.addVertex("name", "My Shelter", "address", "Street 1, City");
        Vertex vertex1 = graph.addVertex("name", "My Shelter", "address", "Street 1, City");

        AnimalShelter animalShelter = fromElement(vertex1, AnimalShelter.class);
        System.out.println(animalShelter);
    }
}
