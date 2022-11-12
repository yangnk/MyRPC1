package test;

public class ClassLoaderTest {
    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        Class<ClassLoaderTest> clazz = ClassLoaderTest.class;
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        System.out.println("classLoader = " + classLoader);
        Class<?> aClass = classLoader.loadClass("test.ClassLoaderTest");
        System.out.println("aClass = " + aClass);
        ClassLoaderTest classLoaderTest = (ClassLoaderTest) aClass.newInstance();
        System.out.println("classLoaderTest = " + classLoaderTest);
    }
}
