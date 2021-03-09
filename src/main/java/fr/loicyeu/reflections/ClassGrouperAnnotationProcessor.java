package fr.loicyeu.reflections;

import fr.loicyeu.reflections.utils.ClassGrouperEnumFactory;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.*;

@SupportedAnnotationTypes("fr.loicyeu.reflections.ClassGrouper")
@SupportedSourceVersion(SourceVersion.RELEASE_15)
public class ClassGrouperAnnotationProcessor extends AbstractProcessor {

    private Filer filer;
    private Messager messager;

    /**
     * Initializes the processor with the processing environment by
     * setting the {@code processingEnv} field to the value of the
     * {@code processingEnv} argument.  An {@code
     * IllegalStateException} will be thrown if this method is called
     * more than once on the same object.
     *
     * @param processingEnv environment to access facilities the tool framework
     *                      provides to the processor
     * @throws IllegalStateException if this method is called more than once.
     */
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        filer = processingEnv.getFiler();
        messager = processingEnv.getMessager();
    }

    /**
     * {@inheritDoc}
     *
     * @param annotations
     * @param roundEnv
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        System.out.println("YOOOOOOOOO");
        messager.printMessage(Diagnostic.Kind.NOTE, "yooooooooooo");
        Map<String, List<TypeElement>> classes = new HashMap<>();

        for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(ClassGrouper.class)) {
            if(annotatedElement.getKind() != ElementKind.CLASS) {
                error("Only classes could be annotated with "
                        + ClassGrouper.class.getSimpleName(), annotatedElement);
                return true;
            }

            TypeElement typeElement = (TypeElement) annotatedElement;
            String groupName = typeElement.getAnnotation(ClassGrouper.class).groupName();
            if(classes.containsKey(groupName)) {
                classes.get(groupName).add(typeElement);
            }else {
                List<TypeElement> list = new LinkedList<>();
                list.add(typeElement);
                classes.put(groupName, list);
            }
        }

        if(!validateMap(classes)) {
            return true;
        }


        ClassGrouperEnumFactory enumFactory = new ClassGrouperEnumFactory(classes, this.filer);
        if(enumFactory.generateCode()) {
            messager.printMessage(Diagnostic.Kind.NOTE, "ALL GOOD");
            return false;
        }else {
            error("Unexpected exception during creation of registry.", null);
            return true;
        }
    }

    /**
     * Méthode permettant de signaler au compilateur d'une erreur fatale.
     *
     * @param message Le message a émettre.
     * @param element L'élément ayant causé l'erreur.
     */
    private void error(String message, Element element) {
        if(element==null) {
            messager.printMessage(Diagnostic.Kind.ERROR, message);
        }else {
            messager.printMessage(Diagnostic.Kind.ERROR, message, element);
        }
    }

    /**
     * Méthode permettant de valider que les classes dans la map respectes les contraintes lié a l'annotation.
     * Si la map n'est pas valide lance une erreur et retourne faux.
     *
     * @param classes La map contenant toutes les classes portant l'annotation.
     * @return Vrai si la map est valide, faux sinon.
     */
    private boolean validateMap(Map<String, List<TypeElement>> classes) {
        Set<String> keys = classes.keySet();
        for (String key : keys) {
            List<TypeElement> typeElementList = classes.get(key);
            String pkgDest = typeElementList.get(0).getAnnotation(ClassGrouper.class).packageDest();
            for (TypeElement typeElement : typeElementList) {
                String pkg = typeElement.getAnnotation(ClassGrouper.class).packageDest();
                if(!pkgDest.equals(pkg)) {
                    error("Element " + typeElement.getSimpleName()
                            + " hasn't same package name (" + pkg
                            + ") as other in his groups ("+pkgDest+").", typeElement);
                    return false;
                }
            }
        }
        return true;
    }
}
