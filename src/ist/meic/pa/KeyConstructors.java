/**
 * Created by miguelcruz on 15-03-2017.
 */
package ist.meic.pa;

import javassist.*;

public class KeyConstructors {

    public static void main (String[] args) throws NotFoundException, ClassNotFoundException{
        if (args.length != 1){
            System.err.println("Usage: java ist.meic.pa.KeyConstructors <Class>");
            System.exit(1);
        }

        ClassPool classPool = ClassPool.getDefault();
        CtClass ctClass = classPool.get(args[0]);
        CtConstructor[] ctConstructors = ctClass.getConstructors();
        for (CtConstructor ctConstructor: ctConstructors){
            if (ctConstructor.hasAnnotation(KeywordArgs.class)){
                assigner(ctConstructor);
            }
        }

    }

    public static void assigner(CtConstructor ctConstructor) throws ClassNotFoundException{
        KeywordArgs annotation = (KeywordArgs) ctConstructor.getAnnotation(KeywordArgs.class);
        String value = annotation.value();
        String[] splitString = value.split(",");
        for (String s: splitString){
            try {
                ctConstructor.insertBeforeBody(s + ";\n");
            } catch (CannotCompileException e){
                System.err.println("Cannot compile annotation: " + e);
            }
        }
    }

}
