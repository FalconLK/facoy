import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.NoSuchElementException;

//((getMethod)|(getDeclaredMethod))
//invoke
public class CallMethodUsingReflection {
	public static Object callMethod1(Object object, String methodName, Object args[]) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		//Caveat: this solution doesn't support some method signatures (e.g., those with primitive types)
		Class<?> myClass = object.getClass();
		Class<?>[] ptypes = new Class[args.length];
		for(int i = 0; i < args.length; i++) {
			ptypes[i] = args[i].getClass();
		}
		Method method = myClass.getMethod(methodName, ptypes);
		Object returnVal = method.invoke(object, args);
		return returnVal;
	}
	
	public static Object callMethod2(Object object, String methodName, Object params[], Class[] types) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		// Uses getMethod
		Class<?> myClass = object.getClass();
		Method method = myClass.getMethod(methodName, types);
		Object returnVal = method.invoke(object, params);
		return returnVal;
	}
	
	public static Object callMethod3(Object object, String methodName, Object params[], Class[] types) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		// Uses getDeclaredMethod and climbs up the superclasses
		Method method = null;
		Class<?> myClass = object.getClass();
		NoSuchMethodException ex = null;
		while(method == null && myClass != null) {
			try {
				method = myClass.getDeclaredMethod(methodName, types);
			} catch (NoSuchMethodException e) {
				ex = e;
				myClass = myClass.getSuperclass();
			}
		}
		if(method == null)
			throw ex;
		Object returnVal = method.invoke(object, params);
		return returnVal;
	}
	
	public static void main(String args[]) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Integer[] params = {};
		Class[] types = {};
		Object retval = CallMethodUsingReflection.callMethod3(new GCD(), "getClass", (Object []) params, types);
		System.out.println(retval);
	}
}
