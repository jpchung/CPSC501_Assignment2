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
            classObject = obj.getClass();


            //get fields declared by the class
            Field fieldObjects[] = classObject.getDeclaredFields();

            //get class
            inspectClass(obj, classObject, fieldObjects);

            //check if need to introspect recursively on field objects
            if(recursive){
                System.out.printf("\n---- RECURSION ON FIELD OBJECTS IN %s: START ----\n", classObject.getName());
                inspectFields(fieldObjects, obj, recursive);
                System.out.printf("\n---- RECURSION ON FIELD OBJECTS IN %s: END ----\n\n", classObject.getName());
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
        inspectMethods(methodObjects);

        //get constructors
        Constructor constructorObjects[] = classObject.getConstructors();
        inspectConstructors(constructorObjects);

        //get fields
        inspectFieldValues(obj, fieldObjects);

        //display contents if initial object is an array
        if(classObject.isArray()){

            Class arrayType = classObject.getComponentType();
            System.out.println("Component Type: " + arrayType.getName());

            Object arrayElements[]  = getObjectArray(obj);
            displayArrayElements(arrayElements);

        }

        //traverse hierarchy get constructors/methods/field values that superclass declares
        inspectSuperclass(obj, superClassObject);
        if(superClassObject.getSuperclass() != null){

            Class nextSuperClass = superClassObject.getSuperclass();
            System.out.printf("superclass %s has a superclass %s!\n",superClassObject.getName(), nextSuperClass.getName() );

            inspectSuperclass(obj, nextSuperClass);
        }

    }

    //method to recursively introspect on Field objects and Array elements (if objects)
    public void inspectFields(Field[] fieldObjects, Object obj, boolean recursive){

        System.out.println();
        for(Field f : fieldObjects){
            try{
                Class fieldType = f.getType();

                //field is an array, check array if elements are objects to recurse on
                if(fieldType.isArray()){
                    System.out.print("Field: " + f.getName() + " - Array");

                    Object fieldValue = f.get(obj);
                    int arrayLength = Array.getLength(fieldValue);
                    Object arrayElements[] = getObjectArray(fieldValue);
                    Class arrayType= fieldType.getComponentType();

                    //array consists of objects, recurse if not empty on non-null elements
                    if(!arrayType.isPrimitive()){
                        System.out.printf(" (%s)\n", arrayType.getTypeName());

                        //check if array has  non-null elements to recurse on
                        if(arrayElements.length >0) {
                            for(int i = 0; i < arrayLength; i++){
                                Object element =  arrayElements[i];
                                String elementTypeString = null;

                                //recurse on non-null element
                                if(element != null)
                                    inspect(element, recursive);
                                else
                                    System.out.println("      object is null...");
                            }
                        }
                        else
                            System.out.println("      Array is empty...");


                    }
                    //otherwise primitive array, don't recurse on elements
                    else
                        System.out.println(" (Primitive)");

                }
                //field is an object, recurse on it if not null
                else if(!fieldType.isPrimitive()){
                    System.out.println("Field: " + f.getName() + " - Object");

                    if(f.get(obj) != null)
                        inspect(f.get(obj), recursive);
                    else
                        System.out.println("      object is null...\n");

                }
                //otherwise field is primitive, don't recurse
                else
                    System.out.println("Field: " + f.getName() + " - Primitive");

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

    //inspect and display method objects
    private void inspectMethods(Method[] methodObjects){
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
    }

    //inspect and display Constructor objects
    public void inspectConstructors(Constructor[] constructorObjects){
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
    }

    //inspect and display type and values for Fields
    public void inspectFieldValues(Object obj, Field[] fieldObjects){
        for(Field f : fieldObjects){
            try{
                f.setAccessible(true);
                Object fieldValue = f.get(obj);

                //query for field modifiers
                int modifiers = f.getModifiers();
                String modifierString = Modifier.toString(modifiers);

                String fieldTypeString = null;
                String fieldValueString = null;

                //query Field object for type, and value if not an array
                Class fieldType = f.getType();
                if(fieldType.isArray()){
                    Class arrayType= fieldType.getComponentType();
                    int arrayLength = Array.getLength(fieldValue);
                    fieldTypeString = arrayType.getName() + "[" + arrayLength + "]";

                }
                else{
                    fieldTypeString = fieldType.toString();
                    if(fieldType.isPrimitive()){
                        fieldValueString = fieldValue.toString();
                    }
                    else if(fieldValue != null){
                        //let value be object's class and id hashcode if instantiated
                        fieldValueString = fieldValue.getClass().getName() + " " + fieldValue.hashCode();

                        //otherwise defaults to null
                    }
                }
                System.out.println("Field: " + modifierString + " " + fieldTypeString + " " + f.getName());

                //print contents if field is an array
                if(fieldType.isArray()){

                    Object arrayElements[] = getObjectArray(fieldValue);
                    displayArrayElements(arrayElements);
                }
                //otherwise just print value
                else
                    System.out.println("      Value: " + fieldValueString);

            } catch(Exception e){
                e.printStackTrace();
            }

        }
    }

    //generic method to display elements in an object array
    public void displayArrayElements(Object[] arrayElements){

        for(int i =0; i < arrayElements.length; i++){
            Object element = arrayElements[i];

            String elementDisplay = null;
            if(element != null){
                elementDisplay = element.toString();

                //value of element if object
                Class elementClass = element.getClass();
                if(!elementClass.isPrimitive() && !elementClass.isArray())
                //if(!elementClass.isPrimitive())
                    elementDisplay = elementClass.getName() + " " + element.hashCode();
            }

            System.out.println("      index " + i + ": " + elementDisplay);
        }
    }

    //get array from Object that has Array class type
    private Object[] getObjectArray(Object obj){

        Object[] objArray;
        //if obj is already an object array, just cast and return
        if(obj instanceof Object[]){
            objArray =  (Object[]) obj;
        }
        //otherwise obj is a primitive array, so get elements by index and wrap primitives
        else{
            int arrayLength = Array.getLength(obj);
            objArray = new Object[arrayLength];
            for(int i = 0; i < arrayLength; i++){
                objArray[i] = Array.get(obj, i);
            }
        }
        return objArray;

    }

    //method to inspect an object's superclass
    private void inspectSuperclass(Object obj,Class superClass){
        System.out.printf("\n>>>>>> INSPECTING SUPERCLASS %s: START\n\n", superClass.getName());

        Constructor superConstructors[] = superClass.getConstructors();
        inspectConstructors(superConstructors);

        Method superMethods[] = superClass.getDeclaredMethods();
        inspectMethods(superMethods);

        Field superFields[] = superClass.getDeclaredFields();

        inspectFieldValues(obj,superFields);

        System.out.printf("\n>>>>>> INSPECTING SUPERCLASS %s: END\n\n", superClass.getName());

    }


}
