package lombok.javac.handlers;

import static com.ezand.tinkerpop.gizmo.utils.GizmoUtil.getElement;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.lang.reflect.Field;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.ezand.tinkerpop.gizmo.AbstractGizmoElementTest;
import com.ezand.tinkerpop.gizmo.helpers.beans.AnimalShelter;
import com.ezand.tinkerpop.gizmo.helpers.beans.Dog;
import com.ezand.tinkerpop.gizmo.helpers.beans.Owner;
import com.ezand.tinkerpop.gizmo.helpers.repository.AnimalShelterRepository;
import com.ezand.tinkerpop.gizmo.helpers.repository.DogRepository;
import com.ezand.tinkerpop.gizmo.helpers.repository.OwnerRepository;
import com.ezand.tinkerpop.gizmo.utils.GizmoMapper;
import com.tinkerpop.gremlin.structure.Vertex;
import com.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;

public class VertexHandlerTest extends AbstractGizmoElementTest<AnimalShelter> {

    private TinkerGraph graph;
    private DogRepository dogRepository;
    private OwnerRepository ownerRepository;
    private AnimalShelterRepository animalShelterRepository;

    @Before
    public void init() {
        this.graph = TinkerGraph.open();
        this.dogRepository = new DogRepository(graph);
        this.ownerRepository = new OwnerRepository(graph);
        this.animalShelterRepository = new AnimalShelterRepository(graph);
    }

    @Override
    public Class<AnimalShelter> getClazz() {
        return AnimalShelter.class;
    }

    @Test
    public void should_fetch_multiple_relationship() throws Exception {
        AnimalShelter shelter = createAnimalShelter();
        Dog fido = createDog("Fido");
        Dog lassie = createDog("Lassie");
        Dog buddy = createDog("Buddy");

        Vertex shelterVertex = getElement(shelter, Vertex.class);
        Vertex fidoVertex = getElement(fido, Vertex.class);
        Vertex lassieVertex = getElement(lassie, Vertex.class);
        Vertex buddyVertex = getElement(buddy, Vertex.class);

        fidoVertex.addEdge("inhabits", shelterVertex);
        lassieVertex.addEdge("inhabits", shelterVertex);
        buddyVertex.addEdge("inhabits", shelterVertex);

        AnimalShelter fetchedShelter = animalShelterRepository.find(shelter.getId());

        Field inhabitants = AnimalShelter.class.getDeclaredField("inhabitants");
        inhabitants.setAccessible(true);

        Field alreadyFetched = AnimalShelter.class.getDeclaredField("$alreadyFetched");
        alreadyFetched.setAccessible(true);

        assertThat(inhabitants.get(fetchedShelter), nullValue());
        fetchedShelter.getInhabitants();
        assertThat(((Set) alreadyFetched.get(fetchedShelter)).size(), equalTo(1));
        assertThat((Set) inhabitants.get(fetchedShelter), notNullValue(Set.class));
        assertThat(((Set) inhabitants.get(fetchedShelter)).size(), equalTo(3));
    }

    @Test
    public void should_fetch_single_eager_relationship() throws Exception {
        AnimalShelter shelter = createAnimalShelter();
        Dog dog = createDog("Fido");

        Vertex dogVertex = getElement(dog, Vertex.class);
        Vertex shelterVertex = getElement(shelter, Vertex.class);

        dogVertex.addEdge("inhabits", shelterVertex);

        Dog fetchedDog = dogRepository.find(dog.getId());

        Field field = Dog.class.getDeclaredField("animalShelter");
        field.setAccessible(true);

        Field alreadyFetched = Dog.class.getDeclaredField("$alreadyFetched");
        alreadyFetched.setAccessible(true);

        assertThat(field.get(fetchedDog), notNullValue());
        assertThat(((Set) alreadyFetched.get(fetchedDog)).size(), equalTo(1));
    }

    @Test
    public void should_fetch_deep_eager_relationships() throws Exception {
        Owner owner = createOwner();
        AnimalShelter shelter = createAnimalShelter();
        Dog dog = createDog("Fido");

        Vertex ownerVertex = getElement(owner, Vertex.class);
        Vertex dogVertex = getElement(dog, Vertex.class);
        Vertex shelterVertex = getElement(shelter, Vertex.class);

        shelterVertex.addEdge("owned_by", ownerVertex);
        dogVertex.addEdge("inhabits", shelterVertex);

        Dog fetchedDog = dogRepository.find(dog.getId());

        Field ownerField = AnimalShelter.class.getDeclaredField("owner");
        ownerField.setAccessible(true);

        assertThat(ownerField.get(fetchedDog.getAnimalShelter()), notNullValue());
    }

    private Owner createOwner() {
        return ownerRepository.save(new Owner(null, "Winnie", "The Pooh"));
    }

    private Dog createDog(String name) {
        return dogRepository.save(new Dog(null, name, "Labrador", null));
    }

    private AnimalShelter createAnimalShelter() {
        return animalShelterRepository.save(new AnimalShelter(null, "My shelter", "Street 1", 0, null, null));
    }
}
