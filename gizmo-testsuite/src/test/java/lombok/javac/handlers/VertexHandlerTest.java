package lombok.javac.handlers;

import com.ezand.tinkerpop.gizmo.AbstractGizmoElementTest;
import com.ezand.tinkerpop.gizmo.helpers.beans.AnimalShelter;

public class VertexHandlerTest extends AbstractGizmoElementTest<AnimalShelter> {
    @Override
    public Class<AnimalShelter> getClazz() {
        return AnimalShelter.class;
    }
}
