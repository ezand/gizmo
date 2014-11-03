package com.ezand.tinkerpop.gizmo;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.Test;

import com.ezand.tinkerpop.gizmo.helpers.beans.AnimalShelter;
import com.ezand.tinkerpop.gizmo.helpers.repository.AnimalShelterRepository;
import com.ezand.tinkerpop.gizmo.helpers.repository.NonManageableRepository;
import com.google.common.collect.Sets;
import com.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;

public class GizmoRepositoryTest extends AbstractGizmoRepositoryTest<AnimalShelter> {
    public static final String SHELTER_NAME = "My Shelter";

    private AnimalShelterRepository repository;
    private NonManageableRepository nonManageableRepository;

    public GizmoRepositoryTest() {
        TinkerGraph graph = TinkerGraph.open();
        repository = new AnimalShelterRepository(graph);
        nonManageableRepository = new NonManageableRepository(graph);
    }

    @Override
    protected GizmoRepository<AnimalShelter, Long> getRepository() {
        return repository;
    }

    @Test(expected = RuntimeException.class)
    public void should_throw_exception_when_saving_non_manageable_bean() throws Exception {
        nonManageableRepository.save("Some string");
    }

    @Test
    public void should_not_store_relationship_fields_as_element_property() throws Exception {
        AnimalShelter shelter = repository.save(new AnimalShelter(null, SHELTER_NAME, null, 1, Sets.newHashSet()));

        assertThat(repository.getGraph().v(shelter.getId()).property("inhabitants").isPresent(), equalTo(false));
    }

    @Override
    protected AnimalShelter createBean() {
        return new AnimalShelter(null, SHELTER_NAME, "Street 1, City", 1, null);
    }

    @Override
    protected String[] getExampleBeanKeyValue() {
        return new String[]{"name", SHELTER_NAME};
    }
}
