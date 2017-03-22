package ist.meic.pa;

import javassist.*;

import java.lang.reflect.Field;

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
                } catch (ClassNotFoundException | CannotCompileException e){
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void assigner(CtClass ctClass, CtConstructor ctConstructor)
            throws ClassNotFoundException, CannotCompileException{

        KeywordArgs annotation = (KeywordArgs) ctConstructor.getAnnotation(KeywordArgs.class);
        /* FIXME Insert parser
        String value = annotation.value();
        String[] splitString = value.split(",");
        */
        CtField ctField = CtField.make("for (int i = 0; i < $1.length - 1; i=i+2){\n" +
                "            this.getClass().getDeclaredField($1[i]).set($0, $1[i+1]);            \n" +
                "        }", ctClass);

    }
}
