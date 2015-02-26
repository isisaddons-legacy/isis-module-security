package org.isisaddons.module.security.dom;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.isisaddons.module.security.dom.permission.ApplicationPermissionValueSetTest;

/**
 * Created by Dan on 26/02/2015.
 */
public class SerializationContractTest extends ApplicationPermissionValueSetTest {
    protected <T> T roundtripSerialization(final T obj) throws IOException, ClassNotFoundException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(obj);

        final ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
        return (T) ois.readObject();
    }
}
