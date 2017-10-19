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

        System.out.println();
        try{
            classObject = obj.getClass();

            //get fields declared by the class
            Field fieldObjects[] = classObject.getDeclaredFields();

            //get class
            inspectClass(obj, classObject, fieldObjects);

            //check if need to introspect recursively on field objects
            if(recursive){
                System.out.printf("---- RECURSION ON %s: START ----\n", obj);
                inspectFields(fieldObjects, obj, recursive);
                System.out.printf("---- RECURSION ON %s: END ----\n", obj);
            }

        } catch(Exception e){
            e.printStackTrace();
            return;
        }


    }

    public void inspectClass(Object obj, Class classObject, Field[] fieldObjects){
        //get name of declaring class
        System.out.println("Declaring class: " + obj);

        //get superclass
        Class superClassObject =  classObject.getSuperclass();
        System.out.println("Superclass: " + superClassObject.getName());

        //get interfaces
        Class interfaceObjects[] = classObject.getInterfaces();
        System.out.print("Interfaces: ");
        displayClassTypeObjects(interfaceObjects);
        System.out.println();

        //get methods
        Method methodObjects[] = classObject.getDeclaredMethods();
        for(Method m : methodObjects){
            //query Method object for name
            System.out.println("Method: " + m.getName());

            //query Method object for exception types
            System.out.print("      Exception types: ");
            Class methodExceptionTypes[] = m.getExceptionTypes();
            displayClassTypeObjects(methodExceptionTypes);
            System.out.println();

            //query Method object for parameter types
            System.out.print("      Parameter types: ");
            Class methodParameterTypes[] = m.getParameterTypes();
            displayClassTypeObjects(methodParameterTypes);
            System.out.println();

            //query Method object for return type
            Class returnType = m.getReturnType();
            System.out.println("      Return type: " + returnType.getName());

            //query Method object for modifiers
            int modifiers = m.getModifiers();
            System.out.println("      Modifiers: " + Modifier.toString((modifiers)));

        }

        //get constructors
        Constructor constructorObjects[] = classObject.getConstructors();
        for(Constructor c : constructorObjects){
            //query Constructor objects for name, parameter types, and modifier
            System.out.println("Constructor: " + c.getName());

            System.out.print("      Parameter types: ");
            Class constructorParameterTypes[] = c.getParameterTypes();
            displayClassTypeObjects(constructorParameterTypes);
            System.out.println();

            int modifiers = c.getModifiers();
            System.out.println("      Modifiers: " + Modifier.toString((modifiers)));
        }

        //get fields
        for(Field f : fieldObjects){
            try{
                System.out.println("Field: " + f.getName());

                //query Field object for type
                Class fieldType = f.getType();
                if(fieldType.isArray()){
                    System.out.println("      FIELD IS AN ARRAY");
                }
                else if(fieldType.isPrimitive()){
                    System.out.println("      Type: " + fieldType);
                }
                else
                    System.out.println("      FIELD IS AN OBJECT");


                int modifiers = f.getModifiers();
                System.out.println("      Modifiers: " + Modifier.toString((modifiers)));
                //System.out.println("      Modifiers: " +

                //query for current value of field
                f.setAccessible(true);
                Object fieldValue = f.get(obj);
                System.out.println("      Value: " + fieldValue);


            } catch(Exception e){
                e.printStackTrace();
            }

        }

    }

    public void inspectFields(Field[] fieldObjects, Object obj, boolean recursive){

        for(Field f : fieldObjects){
            try{

                Class fieldType = f.getType();
                if(!fieldType.isPrimitive()){
                    System.out.println("\nRecursion on: " + f.getName());

                    inspect(f.get(obj), recursive);
                }

                else
                    System.out.println(f.getName() + " is Primitive");
            }
            catch(Exception e){
                e.printStackTrace();
            }

        }
    }

    //general method to display names of items in class Object arrays
    private void displayClassTypeObjects(Class[] classTypeObjects){
        if(classTypeObjects.length > 0){
            for(Class c : classTypeObjects){
                System.out.print(" " + c.getName());

                //add a comma if not last element in array
                if(classTypeObjects[classTypeObjects.length -1] != c)
                    System.out.print(",");
            }
        }
        else
            System.out.print("none");
    }


}
