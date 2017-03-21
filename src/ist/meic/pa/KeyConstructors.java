/**
 * Created by miguelcruz on 15-03-2017.
 */
package ist.meic.pa;

import javassist.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class KeyConstructors {

    public static void main (String[] args) throws NotFoundException,
            ClassNotFoundException, CannotCompileException{
        if (args.length != 1){
            System.err.println("Usage: java ist.meic.pa.KeyConstructors <Class>");
            System.exit(1);
        }
        Translator translator = new ClassTranslater();
        ClassPool classPool = ClassPool.getDefault();
        Loader classLoader = new Loader(classPool);
        classLoader.addTranslator(classPool, translator);
        try{
            classLoader.run(args[0], null);
        } catch (Throwable throwable){
            System.err.println("Target class error: " + throwable);
        }


        /* SHIT THAT MAY BE USED FIXME
        CtConstructor[] ctConstructors = ctClass.getConstructors();
        for (CtConstructor ctConstructor: ctConstructors){
            if (ctConstructor.hasAnnotation(KeywordArgs.class)){
                assigner(ctConstructor);
            }
        }
        */

    }



}
