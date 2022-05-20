package hw;

import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DemoApp {
    public static void main(String[] args) {
        GreetingService helloService = createMethodLoggingProxy(GreetingService.class);
        helloService.hello(); // logs nothing to the console
        helloService.gloryToUkraine(); // logs method invocation to the console

        System.out.println("==================================");

        List<Integer> integers = new ArrayList<>(List.of(5, 35, 6, 232, 6, 8, 32, 1, 8, 2, 0, 8));
//        mergeSort(integers);
        insertionSort(integers);
        integers.forEach(System.out::println);

    }

    /**
     * Creates a proxy of the provided class that logs its method invocations. If a method that
     * is marked with {@link LogInvocation} annotation is invoked, it prints to the console the following statement:
     * "[PROXY: Calling method '%s' of the class '%s']%n", where the params are method and class names accordingly.
     *
     * @param targetClass a class that is extended with proxy
     * @param <T>         target class type parameter
     * @return an instance of a proxy class
     */
    public static <T> T createMethodLoggingProxy(Class<T> targetClass) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(GreetingService.class);

        enhancer.setCallback((MethodInterceptor) (obj, method, arguments, proxy) -> {
            if (method.getDeclaredAnnotations().length > 0
                    && method.getDeclaredAnnotations()[0].annotationType().equals(LogInvocation.class)) {
                System.out.printf("[PROXY: Calling method '%s' of the class '%s']%n", method.getName(), targetClass.getSimpleName());
                return proxy.invokeSuper(obj, arguments);
            }
            return obj;
        });
        return (T) enhancer.create();
    }


    private static <T extends Comparable<T>> void mergeSort(List<T> elements) {
        Objects.requireNonNull(elements, "The [elements] should be not null");
        if (elements.size() <= 1){
            return;
        }

        List<T> left = new ArrayList<>(elements.subList(0, elements.size() / 2));
        List<T> right = new ArrayList<>(elements.subList(elements.size() / 2, elements.size()));

        mergeSort(left);
        mergeSort(right);
        merge(elements, left, right);
    }

    private static <T extends Comparable<T>> void merge(List<T> elements, List<T> left, List<T> right) {
        int elementsIndex = 0, leftIndex = 0, rightIndex = 0;

        while (leftIndex < left.size() && rightIndex < right.size()) {
            if (left.get(leftIndex).compareTo(right.get(rightIndex)) < 0) {
                elements.set(elementsIndex, left.get(leftIndex++));
            } else {
                elements.set(elementsIndex, right.get(rightIndex++));
            }
            elementsIndex++;
        }

        while (leftIndex < left.size()) {
            elements.set(elementsIndex++, left.get(leftIndex++));
        }

        while (rightIndex < right.size()) {
            elements.set(elementsIndex++, right.get(rightIndex++));
        }
    }

    private static <T extends Comparable<T>> void insertionSort(List<T> elements){
        for (int i = 0; i < elements.size(); i++) {
            int n = i;
            while (n > 0 && elements.get(n).compareTo(elements.get(n - 1)) < 0){
                T tmp = elements.get(n - 1);
                elements.set(n - 1 , elements.get(n));
                elements.set(n , tmp);
                n--;
            }
        }
    }
}
