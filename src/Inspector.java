/**
 * CPSC 501 Assignment 2
 * @author Johnny Chung
 *
 * Inspector class to recursively introspect on objects
 */
import java.lang.reflect.*;

public class Inspector {

    /**
     * object info to find:
     * - name of declaring class
     * - name of immediate superclass
     * - name of interfaces the class implements
     * - methods the class declares
     *      (includes: exceptions thrown, parameter types, return type, modifiers)
     * - constructors the class declares
     *      (includes: parameter types, modifiers)
     * - the fields the class declares
     *      (includes: type, modifiers)
     * - the current value of each field
     *      - if field is object reference, print object's class  and "ID hash code"
     *
     * Must also traverse inheritance hierarchy to find all constructors/methods/fields/field values
     * that each superclass/superinterface declares
     *
     * Also print name, component type, length  and contents of any arrays
     *
     */

    //introspect on the passed object and print info as standard output
    //if recursive boolean true, also fully inspect every field that is an object
    public void inspect(Object obj, boolean recursive){

        //get metaobject for instantiated base level object
        Class classObject = null;
        classObject = obj.getClass();

        //check if need to introspect recursively on field objects
        if(recursive){

        }
        else if(!recursive){

            //get declaring class
            Class declaringClassObject = classObject.getDeclaringClass();
                //if have toString method, should invoke dynamically for name

            //get superclass and interfaces
            Class superClassObject =  classObject.getSuperclass();

            //get methods

            //get fields

        }



    }
}
