package com.ezand.tinkerpop.gizmo.utils;

import static com.ezand.tinkerpop.gizmo.utils.GizmoUtil.assertManageableBean;
import static com.ezand.tinkerpop.gizmo.utils.GizmoUtil.getChanges;
import static com.ezand.tinkerpop.gizmo.utils.GizmoUtil.getElement;
import static com.ezand.tinkerpop.gizmo.utils.GizmoUtil.getId;
import static com.ezand.tinkerpop.gizmo.utils.GizmoUtil.getManageable;
import static com.ezand.tinkerpop.gizmo.utils.GizmoUtil.isManageable;
import static com.ezand.tinkerpop.gizmo.utils.GizmoUtil.isManaged;
import static com.ezand.tinkerpop.gizmo.utils.GizmoUtil.prependLabelArguments;
import static com.ezand.tinkerpop.gizmo.utils.GizmoUtil.removeEmptyArguments;
import static com.ezand.tinkerpop.gizmo.utils.GizmoUtil.resolveBeanClass;
import static com.ezand.tinkerpop.gizmo.utils.GizmoUtil.validateArguments;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.notNullValue;

import java.util.Map;

import org.junit.Test;

import com.ezand.tinkerpop.gizmo.helpers.beans.AnimalShelter;
import com.ezand.tinkerpop.gizmo.helpers.repository.AnimalShelterRepository;
import com.tinkerpop.gremlin.process.T;
import com.tinkerpop.gremlin.structure.Element;
import com.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;

public class GizmoUtilTest {
    @Test
    public void should_get_property_changes() throws Exception {
        AnimalShelter animalShelter = getManagedBean();
        Map<String, Object> changes = getChanges(animalShelter);

        assertThat(changes, notNullValue());
        assertThat(changes.isEmpty(), equalTo(true));
    }

    @Test
    public void should_get_id() throws Exception {
        assertThat(getId(getManagedBean(), Long.class), notNullValue());
    }

    @Test
    public void should_get_tinkerpop_element() throws Exception {
        Element element = getElement(getManagedBean());
        assertThat(element, notNullValue());
        assertThat(element.id(), notNullValue());
    }

    @Test
    public void should_be_manageable() throws Exception {
        assertThat(isManageable(getNonManagedBean()), equalTo(true));
    }

    @Test
    public void should_not_be_manageable() throws Exception {
        assertThat(isManageable("Some string"), equalTo(false));
    }

    @Test
    public void should_be_managed() throws Exception {
        assertThat(isManaged(getManagedBean()), equalTo(true));
    }

    @Test
    public void should_not_be_managed() throws Exception {
        assertThat(isManaged(getNonManagedBean()), equalTo(false));
    }

    @Test(expected = RuntimeException.class)
    public void should_throw_exception_when_asserting_non_manageable_bean() throws Exception {
        assertManageableBean("Some string");
    }

    @Test
    public void should_get_manageable() throws Exception {
        assertThat(getManageable(getManagedBean()), notNullValue());
    }

    @Test(expected = ClassCastException.class)
    public void should_throw_exception_when_getting_manageable_from_non_manageable_bean() throws Exception {
        getManageable("Some string");
    }

    @Test
    public void should_prepend_label_arguments() throws Exception {
        Object[] arguments = prependLabelArguments(getManageable(getNonManagedBean()).$toKeyValues(), AnimalShelter.class.getName());

        assertThat(arguments.length, greaterThan(2));
        assertThat(arguments[0], equalTo(T.label));
        assertThat(arguments[1], equalTo(AnimalShelter.class.getName()));
    }

    @Test
    public void should_filter_out_arguments_with_null_value() throws Exception {
        Object[] arguments = new Object[]{"name", "My shelter", "address", null, "inhabitantCount", 1};
        Object[] filteredArguments = removeEmptyArguments(arguments);

        assertThat(filteredArguments.length, equalTo(arguments.length - 2));
    }

    @Test(expected = RuntimeException.class)
    public void argument_validation_should_fail_with_integer_key() throws Exception {
        validateArguments(new Object[]{"name", "My shelter", "address", null, 1, 1});
    }

    @Test
    public void argument_validation_should_pass_for_T_type_argument_keys() throws Exception {
        validateArguments(new Object[]{T.label, AnimalShelter.class.getName()});
    }

    @Test
    public void should_load_class_from_element_label() throws Exception {
        TinkerGraph graph = TinkerGraph.open();
        AnimalShelter save = new AnimalShelterRepository(graph).save(getNonManagedBean());

        Element element = getElement(save);
        Class<?> elementClass = resolveBeanClass(element);

        assertThat(elementClass, notNullValue());
        assertThat(elementClass.getName(), equalTo(AnimalShelter.class.getName()));
    }

    private AnimalShelter getManagedBean() {
        return new AnimalShelterRepository(TinkerGraph.open()).save(getNonManagedBean());
    }

    private AnimalShelter getNonManagedBean() {
        return new AnimalShelter(null, "My shelter", "Street 1, City", 1, null, null);
    }
}
