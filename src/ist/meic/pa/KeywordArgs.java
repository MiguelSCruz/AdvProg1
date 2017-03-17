package ist.meic.pa;

import java.lang.annotation.*;

/**
 * Created by miguelcruz on 17-03-2017.
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface KeywordArgs {
    String value();
}
