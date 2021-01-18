/*
 * Copyright 2002-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.security.access.method;

import java.util.function.Supplier;

import org.aopalliance.intercept.MethodInvocation;

import org.springframework.aop.ClassFilter;
import org.springframework.aop.Pointcut;
import org.springframework.security.core.Authentication;

/**
 * An Authorization advice that can determine if an {@link Authentication} has access to
 * the returned object from the {@link MethodInvocation}. The {@link #getMethodMatcher()}
 * describes when the advice applies for the method.
 *
 * @param <T> the type of object that the authorization check is being done one.
 * @author Evgeniy Cheban
 * @since 5.5
 */
public interface AuthorizationMethodAfterAdvice<T> extends Pointcut {

	/**
	 * Returns the default {@link ClassFilter}.
	 * @return the {@link ClassFilter#TRUE} to use
	 */
	@Override
	default ClassFilter getClassFilter() {
		return ClassFilter.TRUE;
	}

	/**
	 * Determines if an {@link Authentication} has access to the returned object from the
	 * {@link MethodInvocation}.
	 * @param authentication the {@link Supplier} of the {@link Authentication} to check
	 * @param object the {@link T} object to check
	 * @param returnedObject the returned object from the {@link MethodInvocation} to
	 * check
	 * @return the <code>Object</code> that will ultimately be returned to the caller (if
	 * an implementation does not wish to modify the object to be returned to the caller,
	 * the implementation should simply return the same object it was passed by the
	 * <code>returnedObject</code> method argument)
	 */
	Object after(Supplier<Authentication> authentication, T object, Object returnedObject);

}