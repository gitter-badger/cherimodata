/**
 * Copyright (C) 2013 cherimojava (http://github.com/cherimojava/cherimodata)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.cherimojava.data.mongo.entity;

import static com.google.common.base.Preconditions.checkArgument;
import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.isAllUpperCase;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.uncapitalize;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.Locale;

import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;

import com.github.cherimojava.data.mongo.entity.annotation.Id;

/**
 * Utility Class holding commonly used functionality to work with Entities
 *
 * @author philnate
 * @since 1.0.0
 */
public class EntityUtils {
	// TODO add validation that the given methods are really are what they're supposed to be
	/**
	 * Utility class no Need for Instance
	 */
	private EntityUtils() {
	}

	/**
	 * decapitalizes a String. E.g. CamelCase will become camelCase while URL will stay URL, but URLe becomes uRLe. For
	 * inversion see {@link #capitalize(String)}.
	 *
	 * @param name
	 *            string to decapitalize}
	 * @return decapitalized string
	 * @see #capitalize(String)
	 */
	public static String decapitalize(String name) {
		if (name.length() == 1) {
			return name.toLowerCase(Locale.ENGLISH);
		}
		if (!isAllUpperCase(name)) {
			return uncapitalize(name);
		} else {
			return name;
		}
	}

	/**
	 * Capitalizes a String. I.e. the first Letter will be converted into uppercase, all other letters will stay as is.
	 * For inversion see {@link #decapitalize(String)}.
	 *
	 * @param name
	 *            string to capitalize.
	 * @return capitalized string
	 * @see #decapitalize(String)
	 */
	public static String capitalize(String name) {
		return StringUtils.capitalize(name);
	}

	/**
	 * Retrieves the pojo name from the given method if this is a valid set/get/add method
	 *
	 * @param m
	 *            method to retrieve name from
	 * @return propertyname derived from the given method
	 */
	static String getPojoNameFromMethod(Method m) {
		String methodName = m.getName();
		checkArgument((methodName.startsWith("set") || methodName.startsWith("get") || methodName.startsWith("add"))
				&& m.getName().length() > 3, "Don't know how to retrieve name from this method, got [%s]", m.getName());
		return decapitalize(m.getName().substring(3));
	}

	/**
	 * retrieves the name of the property which is used on mongodb side. Accepts only get methods, will throw an
	 * exception for all other methods
	 *
	 * @param m
	 *            method to retrieve mongo name from
	 * @return mongo name for the given getter method (parameter)
	 */
	static String getMongoNameFromMethod(Method m) {
		checkArgument(m.getName().startsWith("get"), "Mongo name can only be retrieved from get methods, but was %s",
				m.getName());
		checkArgument(!(m.isAnnotationPresent(Named.class) && m.isAnnotationPresent(Id.class)),
				"You can not annotate a property with @Name and @Id");
		if (m.isAnnotationPresent(Named.class)) {
			checkArgument(!Entity.ID.equals(m.getAnnotation(Named.class).value()),
					"It's not allowed to use @Name annotation to declare id field, instead use @Id annotation");
			return m.getAnnotation(Named.class).value();
		}
		if (m.isAnnotationPresent(Id.class) || "id".equals(getPojoNameFromMethod(m).toLowerCase(Locale.US))) {
			return Entity.ID;
		}
		return decapitalize(m.getName().substring(3));
	}

	/**
	 * retrieves the name for the Entity, which might be different from the clazz name if the @Named annotation is
	 * present
	 *
	 * @param clazz
	 *            Entity from which to retrieve the collection name
	 * @return the name of the Entity as declared with the Named annotation or the clazz name if annotation isn't
	 *         present as plural
	 */
	public static String getCollectionName(Class<? extends Entity> clazz) {
		Named name = clazz.getAnnotation(Named.class);
		if (name != null && isNotEmpty(name.value())) {
			return name.value();
		}
		return uncapitalize(clazz.getSimpleName() + "s");
	}

	/**
	 * returns the setter method of the given getter method or throws an exception if no such method exists
	 *
	 * @param m
	 *            getter method for which a matching setter method shall be found
	 * @return setter method belonging to the given getter method
	 * @throws java.lang.IllegalArgumentException
	 *             if the given getter Method has no setter method
	 */
	static Method getSetterFromGetter(Method m) {
		try {
			return m.getDeclaringClass().getMethod(m.getName().replaceFirst("g", "s"), m.getReturnType());
		} catch (NoSuchMethodException e) {
			throw new IllegalArgumentException(format("Method %s has no corresponding setter method", m.getName()));
		}
	}

	/**
	 * returns the getter method matching the given Adder method or throws an exception if no such method exists
	 *
	 * @param m
	 *            adder method for which the matching getter method shall be found
	 * @return getter method belonging to the given Adder method
	 * @throws java.lang.IllegalArgumentException
	 *             if the given Adder method has no corresponding getter method
	 */
	static Method getGetterFromAdder(Method m) {
		try {
			return m.getDeclaringClass().getMethod(m.getName().replaceFirst("add", "get"));
		} catch (NoSuchMethodException e) {
			throw new IllegalArgumentException(format("Method %s has no corresponding getter method", m.getName()));
		}
	}

	/**
	 * returns the adder method matching the given Getter method or throws an exception if no such method exists
	 *
	 * @param m
	 *            getter method for which a matching adder method shall be found
	 * @return adder method belongign to the given Getter method
	 * @throws java.lang.IllegalArgumentException
	 *             if the given method has no matching adder method
	 */
	static Method getAdderFromGetter(Method m) {
		try {
			return m.getDeclaringClass().getMethod(m.getName().replaceFirst("get", "add"),
					(Class) ((ParameterizedType) m.getGenericReturnType()).getActualTypeArguments()[0]);
		} catch (Exception e) {
			throw new IllegalArgumentException(format("Method %s has no corresponding adder method", m.getName()));
		}
	}

	/**
	 * returns the getter method belonging to the given setter method
	 *
	 * @param m
	 *            setter method for which a matching getter method shall be found
	 * @return getter method belonging to the given Setter method
	 * @throws java.lang.IllegalArgumentException
	 *             if the given method has no matching adder method
	 */
	static Method getGetterFromSetter(Method m) {
		try {
			Method getter = m.getDeclaringClass().getMethod(m.getName().replaceFirst("s", "g"));
			checkArgument(getter.getReturnType().equals(m.getParameterTypes()[0]),
					"You can only declare setter methods if there's a matching getter. Found %s without getter",
					m.getName());
			return getter;
		} catch (NoSuchMethodException e) {
			throw new IllegalArgumentException(format("Method %s has no corresponding getter method", m.getName()));
		}
	}

	/**
	 * checks if the given method has a return type which is assignable from the declaring class
	 *
	 * @return true if the given method return type is assignable from the declaring class, false otherwise
	 */
	static boolean isAssignableFromClass(Method m) {
		return m.getReturnType().isAssignableFrom(m.getDeclaringClass());
	}

	/**
	 * marks an entity as persisted, which means that certain things might not be changed after on.
	 * 
	 * @param e
	 */
	public static void persist(Entity e) {
		EntityInvocationHandler.getHandler(e).persist();
	}

	/**
	 * returns if the given Entity is already persisted or not
	 * 
	 * @param e
	 * @return
	 */
	public static boolean isPersisted(Entity e) {
		return EntityInvocationHandler.getHandler(e).persisted;
	}

    /**
     * returns true if this getter methods return type is either an entity or a list of entities. Otherwise false
     * @param getter
     * @return true if return type is entity or list of entities
     */
	public static boolean isValidReferenceClass(Method getter) {
		return (Entity.class.isAssignableFrom(getter.getReturnType()) || (Collection.class.isAssignableFrom(getter.getReturnType()) && Entity.class.isAssignableFrom((Class) ((ParameterizedType) getter.getGenericReturnType()).getActualTypeArguments()[0])));
	}
}
