package parser;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.utils.CodeGenerationUtils;
import com.github.javaparser.utils.SourceRoot;
import com.yourorganization.maven_sample.LogicPositivizer;

import java.nio.file.Paths;

public class CodeParser {
    public static void main(String[] args) throws Exception {
        SourceRoot sourceRoot = new SourceRoot(CodeGenerationUtils.mavenModuleRoot(LogicPositivizer.class).resolve("src/main/resources"));

        CompilationUnit cu = sourceRoot.parse("", "OrderServiceImpl.java");
        cu.accept(new MethodVisitor(), null);

        sourceRoot.saveAll(
                CodeGenerationUtils.mavenModuleRoot(LogicPositivizer.class)
                        .resolve(Paths.get("output")));
    }

    private static class MethodVisitor extends VoidVisitorAdapter<Void> {
        @Override
        public void visit(MethodDeclaration n, Void arg) {
            System.out.println("-----------method-----------");
//            System.out.println("method:" + n.getName());
//            n.setName(n.getName() + "_Modified");
//            System.out.println("method children:" + n.getChildNodes());
//            for (Node node : n.getChildNodes()) {
//                System.out.println("child:" + node.toString());
//            }
            if(n.getName().toString().contains("queryForStationId")) {
                System.out.println(n.getBody());

                Statement stmt = null;
                int index = 0;

                NodeList nl = n.getBody().get().getStatements();
                for (int i = 0; i < nl.size(); i++) {
                    Statement s = (Statement) nl.get(i);
                    if(s.toString().contains("restTemplate")){
                        stmt = s;
                        index = i;
                        break;
                    }
                }
                String stmtString = stmt.toString();
                n.getBody().get().getStatements().add(index, new ExpressionStmt(
                        new AssignExpr(
                                new NameExpr(stmtString.substring(0, stmtString.indexOf("="))),
                                new NameExpr("null"),
                                AssignExpr.Operator.ASSIGN
                        ))
                );
                n.getBody().get().getStatements().add(index+1, new ExpressionStmt(
                        new AssignExpr(
                                new NameExpr("CompletableFuture<String> f1"),
                                new NameExpr("CompletableFuture.supplyAsync(() -> {" + stmtString.substring(stmtString.indexOf("=")-3) + "}, executor)"),
                                AssignExpr.Operator.ASSIGN
                        ))
                );
                n.getBody().get().getStatements().remove(index+2);

            }

            super.visit(n, arg);
        }

        public void visit(ExpressionStmt n, Void arg) {
//            System.out.println("-----------statement-----------");
//            System.out.println("method:" + n.getParentNode());
//            System.out.println("method:" + n.getExpression());
//            System.out.println("method:" + n.getExpression().toString() + "_" + n.getExpression().isAssignExpr());
//            if(n.getExpression().toString().contains("ResponseEntity")){
//                System.out.println(n.getExpression().toString());
//                n.setExpression("ResponseEntity<QueryByIdBatchResult> re");
//
//            }
            if(n.getExpression().toString().contains("restTemplate")){
//                System.out.println(n.getExpression().isAssignExpr());
//                System.out.println(n.getExpression().toString());
//                Expression restExpr = n.getExpression().clone();
//                System.out.println(n.getBegin().get().line);
//                n.setExpression("re = restTemplate.exchange(\"http://ts-station-service:12345/station/queryByIdBatch\", " +
//                        "HttpMethod.POST, requestEntity, QueryByIdBatchResult.class)");

            }

            super.visit(n, arg);
        }

        public void visit(IfStmt n, Void arg) {
            n.getCondition().ifBinaryExpr(binaryExpr -> {
                if (binaryExpr.getOperator() == BinaryExpr.Operator.NOT_EQUALS && n.getElseStmt().isPresent()) {
                    Statement thenStmt = n.getThenStmt().clone();
                    Statement elseStmt = n.getElseStmt().get().clone();
                    n.setThenStmt(elseStmt);
                    n.setElseStmt(thenStmt);
                    binaryExpr.setOperator(BinaryExpr.Operator.EQUALS);
                }
            });
            super.visit(n, arg);
        }

        @Override
        public void visit(ClassOrInterfaceDeclaration n, Void arg) {
            System.out.println("class:" + n.getName());
            System.out.println("extends:" + n.getExtendedTypes());
            System.out.println("implements:" + n.getImplementedTypes());
            super.visit(n, arg);
        }

        @Override
        public void visit(PackageDeclaration n, Void arg) {
            System.out.println("package:" + n.getName());
            super.visit(n, arg);
        }
    }

}
