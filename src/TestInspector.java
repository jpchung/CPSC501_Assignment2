/**
 * Created by Owner on 2017-10-16.
 */

import org.junit.Test;
import static org.junit.Assert.*;
import java.lang.reflect.Array;
import java.util.HashSet;

public class TestInspector {

    @Test
    public void testInspectorObject(){
        assertNotNull(new Inspector());

        Inspector inspector = new Inspector();
        Class inspectorClass = inspector.getClass();
        assertNotNull(inspectorClass);

        assertNotNull(inspector.getObjectHash());
    }

    @Test
    public void testInspect(){
        Inspector inspector = new Inspector();

        try{
            Object obj1 = new ClassA();
            assertFalse(inspector.alreadyInspected(obj1));
            inspector.inspect(obj1, false);

            HashSet<Integer> objectHash = inspector.getObjectHash();
            assertEquals(1, objectHash.size());
            assertTrue(inspector.alreadyInspected(obj1));

            Object obj2 = new ClassA(12);
            assertFalse(inspector.alreadyInspected(obj2));
            inspector.inspect(obj2, false);
            assertEquals(2, objectHash.size());
            assertTrue(inspector.alreadyInspected(obj2));

            Object obj3 = new ClassB();
            assertFalse(inspector.alreadyInspected(obj3));
            inspector.inspect(obj3, false);
            assertEquals(3, objectHash.size());
            assertTrue(inspector.alreadyInspected(obj3));
            //classB should recurse twice, but HashSet should already have classB hashcode
            inspector.inspect(obj3, true);
            assertEquals(5, objectHash.size());

            Object obj4 = new ClassD();
            assertFalse(inspector.alreadyInspected(obj4));
            inspector.inspect(obj4, false);
            assertEquals(6, objectHash.size());
            assertTrue(inspector.alreadyInspected(obj4));

            Object obj5 = new ClassB[10];
            assertFalse(inspector.alreadyInspected(obj5));
            inspector.inspect(obj5, false);
            assertTrue(inspector.alreadyInspected(obj5));

            String testString = "Da na na na na, Inspector Gadget!";
            assertFalse(inspector.alreadyInspected(testString));
            inspector.inspect(testString, true);
            assertTrue(inspector.alreadyInspected(testString));
        }
        catch(Exception e){
            e.printStackTrace();
        }

    }

    @Test
    public void testGetObjectArray(){
        Inspector inspector = new Inspector();

        Object arrayObj1 = new ClassB[10];
        assertNotNull(inspector.getObjectArray(arrayObj1));

        Object arrayOutput1[] = inspector.getObjectArray(arrayObj1);
        assertEquals(10, Array.getLength(arrayOutput1));
        for(Object o: arrayOutput1){
            assertNull(o);
        }

        Object arrayObj2 = new char[]{'j', 'u', 'n', 'i', 't', ' ','p', 'l', 's'};
        assertNotNull(inspector.getObjectArray(arrayObj2));

        Object arrayOutput2[] = inspector.getObjectArray(arrayObj2);
        assertEquals(9, Array.getLength(arrayOutput2));
        for(Object o: arrayOutput2){
            assertNotNull(o);
            assertTrue(o instanceof Character);
        }

        Object arrayObj3 = new ClassB[5][5];
        assertNotNull(inspector.getObjectArray(arrayObj3));

        Object arrayOutput3[] = inspector.getObjectArray(arrayObj3);
        assertEquals(5, Array.getLength(arrayOutput3));
        for(Object o : arrayOutput3){
            assertNotNull(o);
            assertEquals(5, Array.getLength(o));
            assertTrue(o.getClass().isArray());
            Object arrayOutput4[] = inspector.getObjectArray(o);
            for(Object nestedO: arrayOutput4){
                assertNull(nestedO);
            }
        }

    }

}
