package fr.loicyeu.reflections.utils;

import fr.loicyeu.reflections.ClassGrouper;

import javax.annotation.processing.Filer;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

public class ClassGrouperEnumFactory {

    private final Map<String, List<TypeElement>> classes;
    private final Filer filer;

    public ClassGrouperEnumFactory(Map<String, List<TypeElement>> classes, Filer filer) {
        this.classes = classes;
        this.filer = filer;
    }

    public boolean generateCode() {
        for (Map.Entry<String, List<TypeElement>> entry : classes.entrySet()) {
            if(!generateClass(entry.getValue())) {
                return false;
            }
        }
        return true;
    }

    private boolean generateClass(List<TypeElement> groupClasses) {
        ClassGrouper classGrouper = groupClasses.get(0).getAnnotation(ClassGrouper.class);
        String packageName = classGrouper.packageDest();
        String simpleClassName = classGrouper.groupName()+"Registry";
        simpleClassName = simpleClassName.substring(0, 1).toUpperCase()+simpleClassName.substring(1);
        String className = packageName + "." + simpleClassName;

        try {
            JavaFileObject builderFile = filer.createSourceFile(className);

            try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {

                out.print("package ");
                out.print(packageName);
                out.println(";");
                out.println();

                out.print("public enum ");
                out.print(simpleClassName);
                out.println(" {");
                out.println();

                for(TypeElement typeElement : groupClasses) {
                    out.print("\t"+typeElement.getSimpleName().toString().toUpperCase());
                    out.println("("+typeElement.getQualifiedName()+".class),");
                }
                out.println("\t;");

                out.println("\tprivate final Class<?> clazz;");
                out.println();
                out.println("\t"+simpleClassName+"(Class<?> clazz) {");
                out.println("\t\tthis.clazz = clazz;");
                out.println("\t}");
                out.println();
                out.println("\tpublic Class<?> getClazz() {");
                out.println("\t\treturn this.clazz;");
                out.println("\t}");
                out.println();
                out.println('}');
            }

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }


}
