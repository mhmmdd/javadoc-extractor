package me.tomassetti.javadocextractor;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.google.common.base.Strings;
import me.tomassetti.javadocextractor.support.DirExplorer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Iterate over all the Javadoc comments and print them together with a description of the commented element.
 */
public class AllJavadocExtractor {

    public static void main(String[] args) {
        File projectDir = new File("source_to_parse/");
        System.out.println(Strings.repeat("=", 200));

        new DirExplorer((level, path, file) -> path.endsWith(".java"), (level, path, file) -> {
            try {
                new VoidVisitorAdapter<Object>() {
                    @Override
                    public void visit(JavadocComment comment, Object arg) {
                        super.visit(comment, arg);
                        String title = null;

                        Pattern p = Pattern.compile(" \\* (.*)");
                        Matcher m = p.matcher(comment.getContent());

                        if (comment.getCommentedNode().isPresent() && m.find()) {
                            System.out.println(describe(comment.getCommentedNode().get(), m.group(1)));
                        }

                    }
                }.visit(JavaParser.parse(file), null);
            } catch (IOException e) {
                new RuntimeException(e);
            }
        }).explore(projectDir);
        System.out.println(Strings.repeat("=", 200));
    }

    private static String describe(Node node, String comment) {
        if (node instanceof MethodDeclaration) {
            MethodDeclaration methodDeclaration = (MethodDeclaration)node;

            List<String> parameters = new ArrayList<>();
            for( Parameter parameter: methodDeclaration.getParameters()) {
                parameters.add(parameter.getType().toString() + " " +parameter.getName().getIdentifier());
            }

            String title = String.format("%s|%s|%s|%s", methodDeclaration.getName(), comment, String.join(", ", parameters), methodDeclaration.getType());

            return title;
        }

//        if (node instanceof ClassOrInterfaceDeclaration) {
//            ClassOrInterfaceDeclaration classOrInterfaceDeclaration = (ClassOrInterfaceDeclaration)node;
//            if (classOrInterfaceDeclaration.isInterface()) {
//                return "Interface " + classOrInterfaceDeclaration.getName();
//            } else {
//                return "Class " + classOrInterfaceDeclaration.getName();
//            }
//        }

        return null;
    }

}
