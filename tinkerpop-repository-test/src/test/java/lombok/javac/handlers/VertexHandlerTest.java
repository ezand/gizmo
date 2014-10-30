package lombok.javac.handlers;

import static com.ezand.tinkerpop.gizmo.utils.GizmoMapper.map;
import static com.ezand.tinkerpop.gizmo.utils.GizmoUtil.getChanges;
import static com.ezand.tinkerpop.gizmo.utils.GizmoUtil.getId;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.Test;

import com.ezand.tinkerpop.gizmo.helpers.beans.AnimalShelter;
import com.tinkerpop.gremlin.structure.Vertex;
import com.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;

public class VertexHandlerTest {
    @Test
    public void foo() {
        TinkerGraph graph = TinkerGraph.open();
        Vertex vertex = graph.addVertex("name", "My Shelter", "address", "Street 1, City");

        AnimalShelter animalShelter = map(vertex, AnimalShelter.class);
        assertThat(animalShelter, notNullValue());
        assertThat(animalShelter.getName(), equalTo("My Shelter"));
        assertThat(animalShelter.getAddress(), equalTo("Street 1, City"));
        assertThat(getChanges(animalShelter).size(), equalTo(0));

        animalShelter.setName("My Shelter");
        assertThat(getChanges(animalShelter).size(), equalTo(0));

        animalShelter.setName("My Awesome Shelter");
        assertThat(getChanges(animalShelter).size(), equalTo(1));
        assertThat(graph.v(getId(animalShelter, Long.class)).property("name").value(), equalTo("My Shelter"));
    }
}
