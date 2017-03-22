package ist.meic.pa;

import javassist.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by miguelcruz on 21-03-2017.
 */
public class ClassTranslator implements Translator {

    @Override
    public void start(ClassPool classPool)
            throws NotFoundException, CannotCompileException {

    }

    @Override
    public void onLoad(ClassPool classPool, String s)
            throws NotFoundException, CannotCompileException {
        CtClass ctClass = classPool.get(s);
        assignConstructors(ctClass);
    }

    public void assignConstructors(CtClass ctClass) {
        for (CtConstructor ctConstructor: ctClass.getConstructors()){
            if (ctConstructor.hasAnnotation(KeywordArgs.class)){
                try {
                    assigner(ctClass, ctConstructor);
                } catch (ClassNotFoundException | CannotCompileException | NotFoundException e){
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void assigner(CtClass ctClass, CtConstructor ctConstructor)
            throws ClassNotFoundException, CannotCompileException, NotFoundException{

        KeywordArgs annotation = (KeywordArgs) ctConstructor.getAnnotation(KeywordArgs.class);

        String value = annotation.value();
        Map<String, String> pairs = annotationParser(value);

        if (!pairs.isEmpty()) {
            for (String s : pairs.keySet()) {
                if (pairs.get(s) != null)
                    ctConstructor.insertBefore(s + "=" + pairs.get(s) + ";");
            }
        }


        ctConstructor.insertAfter("for (int i = 0; i < $1.length - 1; i=i+2){" +
                                            "$0.getClass().getDeclaredField((String)$1[i]).set($0, $1[i+1]);" +
                                        "}");


    }

    public Map<String, String> annotationParser(String args){
        Map<String, String> argsMap = new HashMap<>();
        String[] splitString = args.split(",");
        for (String pair: splitString){
            String[] splitPair = pair.split("=");
            if (splitPair.length == 2){
                //Object result = evaluate(splitPair[1]);
                argsMap.put(splitPair[0], splitPair[1]);
            }
            else{
                argsMap.put(splitPair[0], null);
            }
        }
        return argsMap;
    }

}
