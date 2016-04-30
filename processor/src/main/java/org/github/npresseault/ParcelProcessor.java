package org.github.npresseault;


import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import org.parceler.Parcel;

import java.io.IOException;
import java.util.Set;

import javax.annotation.Resource;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

@SupportedSourceVersion(SourceVersion.RELEASE_7)
@SupportedAnnotationTypes({"javax.annotation.Resource"})
public class ParcelProcessor extends AbstractProcessor {

    private Messager messager;
    private Filer filer;

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        messager = processingEnv.getMessager();
        filer = processingEnv.getFiler();

        for (Element element : roundEnv.getElementsAnnotatedWith(Resource.class)) {
            TypeElement resourceElement = (TypeElement) element;
            ClassName className = ClassName.bestGuess(resourceElement.getQualifiedName().toString());
            TypeSpec typeSpec = TypeSpec.classBuilder(className.simpleName() + "$$Resource")
                    .addAnnotation(AnnotationSpec.builder(Parcel.class).build())
                    .build();
            try {
                JavaFile.builder(className.packageName(), typeSpec).build().writeTo(filer);
            } catch (IOException e) {
                messager.printMessage(Diagnostic.Kind.ERROR, "Unable to write generated java file: " + e.getMessage());
            }
        }
        return true;
    }
}
