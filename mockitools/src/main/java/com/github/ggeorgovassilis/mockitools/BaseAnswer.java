package com.github.ggeorgovassilis.mockitools;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * <p>
 * Utility for writing simpler mockito {@link Answer}s. Just extend this class
 * and implement a method that matches the mocked method name and arguments. For
 * example, if you want to mock
 * </p>
 * <p>
 * <code>BankService.checkBalance(String accountId, Callback<BankAccount> callback)</code>
 * </p>
 * 
 * then do it like this:
 * <p>
 * 
 * <pre>
 * when(bankSerivce.checkBalance(eq("1234"), any(Callback.class)).thenAnswer(
 * new BaseAnswer(){
 * void checkBalance(String id, Callback<BankAccount> callback){
 * 		callback.onSuccess(new BankAccount("1234","Scrooge McDuck", 1000))
 * }
 * }
 * );
 * </pre>
 * </p>
 * 
 * 
 * @author george georgovassilis
 *
 * @param <T>
 */
public abstract class BaseAnswer<T> implements Answer<T> {

	private Class<?>[] getClasses(Object[] arguments) {
		Class<?>[] argClasses = new Class[arguments.length];
		for (int i = 0; i < arguments.length; i++) {
			if (arguments[i] != null)
				argClasses[i] = arguments[i].getClass();
		}
		return argClasses;
	}

	private double rateMatch(Class<?>[] providedClasses, Class<?>[] declaredClasses) {
		double score = -1;
		if (providedClasses.length != declaredClasses.length)
			return -1;
		for (int i = 0; i < providedClasses.length; i++) {
			if (providedClasses[i] == null)
				score += 0.5;
			else {
				if (!declaredClasses[i].isAssignableFrom(providedClasses[i]))
					return -1;
				score += 1;
			}
		}
		score = score / (double) providedClasses.length;
		return score;
	}

	private Method findMethodWithArguments(Object[] arguments) {
		Class<?>[] argClasses = getClasses(arguments);
		Method[] methods = getClass().getDeclaredMethods();
		Method bestMethod = null;
		double bestMatch = -1;
		for (Method method : methods) {
			double match = rateMatch(argClasses, method.getParameterTypes());
			if (match > bestMatch) {
				bestMatch = match;
				bestMethod = method;
			}
		}
		return bestMethod;
	}

	private String argTypesToString(Object args[]) {
		Class<?>[] classes = getClasses(args);
		String s = "";
		String prefix = ",";
		for (Class<?> c : classes) {
			s += prefix + c.getName();
			prefix = ",";
		}
		return s;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T answer(InvocationOnMock invocation) throws Throwable {
		Object[] arguments = invocation.getArguments();
		Method method = findMethodWithArguments(arguments);
		if (method == null)
			throw new RuntimeException(
					"This answer does not declare a method with these argument types: " + argTypesToString(arguments));
		method.setAccessible(true);
		try {
			return (T) method.invoke(this, arguments);
		} catch (InvocationTargetException e) {
			throw e.getCause();
		}
	}
}