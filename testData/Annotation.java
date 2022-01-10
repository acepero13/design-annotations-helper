@ImplicitDependency(dependsOn = DependencyA.class, description = "This is a test", fileDependencies="dependency.json<caret>")
public class Test {
    private int test;
}