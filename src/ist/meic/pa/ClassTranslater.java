package ist.meic.pa;

import javassist.*;

/**
 * Created by miguelcruz on 21-03-2017.
 */
public class ClassTranslater implements Translator {

    @Override
    public void start(ClassPool classPool) throws NotFoundException, CannotCompileException {

    }

    @Override
    public void onLoad(ClassPool classPool, String s) throws NotFoundException, CannotCompileException {
        CtClass ctClass = classPool.get(s);
        assignConstructors(ctClass);
    }

    public void assignConstructors(CtClass ctClass) {
        for (CtConstructor ctConstructor: ctClass.getConstructors()){
            if (ctConstructor.hasAnnotation(KeywordArgs.class)){
                try {
                    assigner(ctConstructor);
                } catch (ClassNotFoundException e){
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void assigner(CtConstructor ctConstructor) throws ClassNotFoundException{

        KeywordArgs annotation = (KeywordArgs) ctConstructor.getAnnotation(KeywordArgs.class);
        String value = annotation.value();
        String[] splitString = value.split(",");
        for (String s: splitString){
            try {
                ctConstructor.insertBeforeBody(s + ";\n");
            } catch (CannotCompileException e){
                System.err.println("Cannot compile assignment: " + e);
            }
        }
    }
}
