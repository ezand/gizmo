package com.ezand.tinkerpop.gizmo;

import static com.ezand.tinkerpop.gizmo.utils.GizmoUtil.getChanges;
import static com.ezand.tinkerpop.gizmo.utils.GizmoUtil.getId;
import static com.ezand.tinkerpop.gizmo.utils.GizmoUtil.isManaged;
import static java.beans.Introspector.getBeanInfo;
import static java.util.Arrays.stream;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.beans.PropertyDescriptor;
import java.util.Set;

import org.junit.Test;

import com.google.common.reflect.TypeToken;
import com.tinkerpop.gremlin.process.T;
import com.tinkerpop.gremlin.structure.VertexProperty;

public abstract class AbstractGizmoRepositoryTest<B> {
    protected final Class<? super B> beanClass = new TypeToken<B>(getClass()) {
    }.getRawType();

    protected abstract GizmoRepository<B, Long> getRepository();

    @Test
    public void should_have_correct_bean_class() throws Exception {
        assertThat(getRepository().beanClass, equalTo(beanClass));
    }

    @Test
    public void label_should_be_bean_class() throws Exception {
        assertThat(getRepository().getLabel(), equalTo(beanClass.getName()));
    }

    @Test
    public void should_find_all() throws Exception {
        createPersistedBean();
        Set<B> beans = getRepository().find();

        assertThat(beans.size(), greaterThan(0));
        beans.stream().forEach(s -> assertThat(isManaged(s), equalTo(true)));
    }

    @Test
    public void should_not_find_any() throws Exception {
        assertThat(getRepository().find().isEmpty(), equalTo(true));
    }

    @Test
    public void should_find_by_id() {
        B bean = getRepository().find(getId(getRepository().save(createBean()), Long.class));

        assertThat(bean, notNullValue());
        assertThat(getId(bean, Long.class), notNullValue());
    }

    @Test
    public void should_not_find_any_by_id() throws Exception {
        assertThat(getRepository().find(0L), nullValue());
    }

    @Test
    public void should_find_by_key_value() throws Exception {
        B persistedBean = createPersistedBean();
        Set<B> beans = getRepository().find(T.id.getAccessor(), getId(persistedBean, Long.class));

        assertThat(beans.size(), equalTo(1));
    }

    @Test
    public void should_not_find_any_by_key_value() throws Exception {
        assertThat(getRepository().find(T.id.getAccessor(), 0L).isEmpty(), equalTo(true));
    }

    @Test
    public void should_save_new_bean() throws Exception {
        B bean = getRepository().save(createBean());

        assertThat(bean, notNullValue());
        assertThat(getId(bean, Long.class), notNullValue(Long.class));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void should_save_new_beans() throws Exception {
        Set<B> beans = getRepository().save(createBean(), createBean());

        assertThat(beans.size(), equalTo(2));
        assertThat(getRepository().count(), equalTo(2L));
    }

    @Test
    public void should_update_existing_instead_of_saving_new() throws Exception {
        B bean = createPersistedBean();

        getRepository().save(bean);
        assertThat(getRepository().count(), equalTo(1L));
    }

    @Test
    public void should_update_bean() throws Exception {
        B bean = createPersistedBean();
        getRepository().update(bean);

        assertThat(getRepository().count(), equalTo(1L));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void should_update_beans() throws Exception {
        Set<B> beans = getRepository().update(createPersistedBean(), createPersistedBean());

        assertThat(beans.size(), equalTo(2));
        assertThat(getRepository().count(), equalTo(2L));
    }

    @Test
    public void should_save_non_existing_beans_instead_of_updating() throws Exception {
        B bean = getRepository().update(createBean());

        assertThat(bean, notNullValue());
        assertThat(getRepository().count(), equalTo(1L));
    }

    @Test
    public void should_count_all() throws Exception {
        createPersistedBean();

        assertThat(getRepository().count(), equalTo(1L));
    }

    @Test
    public void should_count_by_key() throws Exception {
        createPersistedBean();

        assertThat(getRepository().count(getExampleBeanKeyValue()[0]), equalTo(1L));
    }

    @Test
    public void should_count_by_key_value() throws Exception {
        createPersistedBean();

        assertThat(getRepository().count(getExampleBeanKeyValue()[0], getExampleBeanKeyValue()[1]), equalTo(1L));
    }

    @Test
    public void should_remove_property_when_value_is_set_to_null() throws Exception {
        B bean = createPersistedBean();

        stream(getBeanInfo(bean.getClass()).getPropertyDescriptors())
                .filter(pd -> pd.getName().equals(getExampleBeanKeyValue()[0]))
                .findFirst()
                .orElseThrow(RuntimeException::new)
                .getWriteMethod().invoke(bean, new Object[]{null});

        getRepository().update(bean);
        VertexProperty<Object> property = getRepository().getGraph().v(getId(bean, Long.class)).property(getExampleBeanKeyValue()[0]);
        assertThat(property.isPresent(), equalTo(false));
    }

    protected B createPersistedBean() {
        return getRepository().save(createBean());
    }

    protected abstract B createBean();

    protected abstract String[] getExampleBeanKeyValue();
}
