package ist.meic.pa;

import javassist.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import static javassist.CtNewConstructor.defaultConstructor;

/**
 * Created by miguelcruz on 21-03-2017.
 */
public class ClassTranslator implements Translator {

    @Override
    public void start(ClassPool classPool)
            throws NotFoundException, CannotCompileException {

    }

    @Override
    public void onLoad(ClassPool classPool, String s) throws NotFoundException {
        CtClass ctClass = classPool.get(s);
        assignConstructors(ctClass);
    }

    public void assignConstructors(CtClass ctClass) {
        for (CtConstructor ctConstructor: ctClass.getConstructors()){
            if (ctConstructor.hasAnnotation(KeywordArgs.class)){
                try {
                    ctClass.addConstructor(defaultConstructor(ctClass));
                    assigner(ctClass, ctConstructor);
                } catch (ClassNotFoundException | CannotCompileException | NotFoundException e){
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void assigner(CtClass ctClass, CtConstructor ctConstructor)
            throws ClassNotFoundException, CannotCompileException, NotFoundException, RuntimeException{

        KeywordArgs annotation;
        Map<String, String> pairs = new HashMap<>();
        LinkedList<String> sortedArgs = new LinkedList<>();
        String template= "{";

        while (ctClass != null) {
            for (CtConstructor constructor: ctClass.getConstructors()) {
                if (constructor.hasAnnotation(KeywordArgs.class)) {
                    annotation = (KeywordArgs) constructor.getAnnotation(KeywordArgs.class);
                    String value = annotation.value();
                    annotationParser(value, pairs);
                    sortedArgs = argsSorter(pairs);
                }
            }
            ctClass = ctClass.getSuperclass();
        }

        if (!pairs.isEmpty()) {
            for (String s : sortedArgs) {
                if (pairs.get(s) != null) {
                    template = template + s + "=" + pairs.get(s) + ";\n";
                }
            }
        }

        template = template +   "for (int i = 0; i < $1.length - 1; i=i+2){\n" +
                                    "Class myClass = $0.getClass();\n" +
                                    "while (myClass != Object.class) {\n" +
                                        "try {\n" +
                                            "myClass.getDeclaredField((String) $1[i]).setAccessible(true);\n" +
                                            "myClass.getDeclaredField((String) $1[i]).set($0, $1[i+1]);\n" +
                                            "break;\n" +
                                        "} catch (NoSuchFieldException e) {\n" +
                                            "myClass = myClass.getSuperclass();\n" +
                                            "if (myClass == Object.class) {\n" +
                                                "throw new RuntimeException(\"Unrecognized keyword: \" + $1[i]);\n" +
                                            "}" +
                                        "} catch (IllegalAccessException e) {\n" +
                                            "throw new RuntimeException(e);\n" +
                                        "}\n" +
                                    "}\n" +
                                "}";

        template = template + "}";
        ctConstructor.setBody(template);

    }

    public void annotationParser(String args, Map<String, String> argsMap){
        String[] splitString = args.split(",");
        for (String pair: splitString){
            String[] splitPair = pair.split("=");
            if (!argsMap.containsKey(splitPair[0]) || argsMap.get(splitPair[0]) == null){
                if (splitPair.length == 2)
                    argsMap.put(splitPair[0], splitPair[1]);
                else{
                    if (!argsMap.containsKey(splitPair[0]))
                        argsMap.put(splitPair[0], null);
                }
            }
        }
    }

    public LinkedList<String> argsSorter(Map<String, String> pairs){
        LinkedList<String> result = new LinkedList<>();

        result.addAll(pairs.keySet());
        boolean changed = true;
        while (changed){
            changed=false;
            for (String s : result) {
                int leftIndex = result.indexOf(s);
                int rightIndex = result.indexOf(pairs.get(s)); //indexOf return -1 if there is not any match
                if (leftIndex < rightIndex) {
                    String aux = pairs.get(s);
                    result.set(rightIndex, s);
                    result.set(leftIndex, aux);
                    changed = true;
                }
            }
        }
        return result;
    }
}
