package fr.loicyeu.reflections;

import java.lang.annotation.*;

/**
 * Annotation ClassGrouper.<br>
 * Permet à la compilation de créer un registre regroupant toutes les classes portant cette annotation et ayant le
 * même nom de groupe.<br>
 * Le package de destination du registre contenant toutes les classes doit être le même pour toutes les classes portant
 * Cette annotation. Le nom du registre créer est de la forme {@code groupeNameRegistry}.
 *
 * @author Loïc HENRY
 * @version 1.0.0
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface ClassGrouper {

    String groupName();
    String packageDest();

}