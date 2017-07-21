package fr.santa.akachan.middleware.authentification;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.ws.rs.NameBinding;

/**
 * création d'une annotation @Securise 
 *
 */
@NameBinding
@Retention(RUNTIME)
@Target({ TYPE, METHOD })
public @interface Securise {

}
