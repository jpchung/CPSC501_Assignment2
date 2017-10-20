/**
 * CPSC 501 Assignment 2
 * @author Johnny Chung
 *
 * Inspector class to recursively introspect on objects
 */
import java.lang.reflect.*;
import java.util.Arrays;

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
            if(obj != null){
                classObject = obj.getClass();

                //get fields declared by the class
                Field fieldObjects[] = classObject.getDeclaredFields();

                //get class
                inspectClass(obj, classObject, fieldObjects);

                //check if need to introspect recursively on field objects
                if(recursive){
                    System.out.printf("\n---- RECURSION ON %s: START ----\n", classObject.getName());
                    inspectFields(fieldObjects, obj, recursive);
                    System.out.printf("\n---- RECURSION ON %s: END ----\n\n", classObject.getName());
                }
            }
            else
                System.out.println("object is null, can't inspect...\n");

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

            //query Method object for exception types
            Class methodExceptionTypes[] = m.getExceptionTypes();

            //query Method object for parameter types
            Class methodParameterTypes[] = m.getParameterTypes();

            //query Method object for return type
            Class returnType = m.getReturnType();

            //query Method object for modifiers
            int modifiers = m.getModifiers();
            String modifierString = Modifier.toString(modifiers);

            //display Method object as single line output (method signature)
            System.out.print("Method: " +
                    modifierString + " " +
                    returnType.getName() + " " +
                    m.getName() + "(");
            displayClassTypeObjects(methodParameterTypes);
            System.out.print(")");
            //check if need to print exceptions
            if(methodExceptionTypes.length > 0){
                System.out.print(" throws ");
                displayClassTypeObjects(methodExceptionTypes);
            }
            System.out.println();

        }

        //get constructors
        Constructor constructorObjects[] = classObject.getConstructors();
        for(Constructor c : constructorObjects){
            //query Constructor objects for name, parameter types, and modifier
            Class constructorParameterTypes[] = c.getParameterTypes();

            int modifiers = c.getModifiers();
            String modifierString = Modifier.toString(modifiers);

            //display Constructor as single line output (method signature)
            System.out.print("Constructor: " + modifierString + " " + c.getName() +"(");
            displayClassTypeObjects(constructorParameterTypes);
            System.out.println(")");
        }

        //get fields
        for(Field f : fieldObjects){
            try{
                f.setAccessible(true);
                Object fieldValue = f.get(obj);

                System.out.println("Field: " + f.getName());
                int modifiers = f.getModifiers();
                System.out.println("      Modifiers: " + Modifier.toString((modifiers)));

                //query Field object for type
                Class fieldType = f.getType();
                if(fieldType.isArray()){
                    System.out.println("      Type: Array");
                    Class arrayType= fieldType.getComponentType();
                    System.out.println("      Component Type: " + arrayType.getName());
                    int arrayLength = Array.getLength(f.get(obj));
                    System.out.println("      Length: " + arrayLength);

                    //print contents of array
                    Object arrayElements[] = (Object[]) fieldValue;
                    for(int i =0; i < arrayLength; i++){

                        Object element = arrayElements[i];

                        String elementDisplay = null;
                        if(element != null)
                             elementDisplay = element.toString();

                        System.out.println("      index " + i + ": " + elementDisplay);
                    }
                }
                else if(fieldType.isPrimitive()){
                    System.out.println("      Type: " + fieldType);
                    System.out.println("      Value: " + fieldValue);
                }
                else{
                    System.out.println("      Type: Object");

                    //let value be object's class and id hashcode
                    if(fieldValue != null)
                        System.out.println("      Value: " + fieldValue.getClass().getName() + " " + fieldValue.hashCode());
                }



            } catch(Exception e){
                e.printStackTrace();
            }

        }

    }

    //method to recursively introspect on Field objects and Array elements (if objects)
    public void inspectFields(Field[] fieldObjects, Object obj, boolean recursive){

        System.out.println();
        for(Field f : fieldObjects){
            try{

                Class fieldType = f.getType();
                if(fieldType.isArray()){
                    System.out.println(f.getName() + ": Array");
                    //should check each item in array to see if object


                }
                else if(!fieldType.isPrimitive()){
                    System.out.println(f.getName() + ": Object");
                    inspect(f.get(obj), recursive);
                }
                else
                    System.out.println(f.getName() + ": Primitive");

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
                System.out.print(c.getName());

                //add a comma if not last element in array
                if(classTypeObjects[classTypeObjects.length -1] != c)
                    System.out.print(", ");
            }
        }
        else
            System.out.print("");
    }


}
