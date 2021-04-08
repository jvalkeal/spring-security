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

package org.springframework.security.authorization.method;

import java.util.List;
import java.util.function.Supplier;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.aop.Pointcut;
import org.springframework.aop.support.ComposablePointcut;
import org.springframework.core.log.LogMessage;
import org.springframework.security.core.Authentication;
import org.springframework.util.Assert;

/**
 * An {@link AuthorizationMethodBeforeAdvice} which delegates to a specific
 * {@link AuthorizationMethodBeforeAdvice} and grants access if all
 * {@link AuthorizationMethodBeforeAdvice}s granted or abstained. Denies access only if
 * one of the {@link AuthorizationMethodBeforeAdvice}s denied.
 *
 * @author Evgeniy Cheban
 * @author Josh Cummings
 * @since 5.5
 */
public final class DelegatingAuthorizationMethodBeforeAdvice<T> implements AuthorizationMethodBeforeAdvice<T> {

	private final Log logger = LogFactory.getLog(getClass());

	private final Pointcut pointcut;

	private final List<AuthorizationMethodBeforeAdvice<T>> delegates;

	/**
	 * Creates an instance.
	 * @param delegates the {@link AuthorizationMethodBeforeAdvice}s to use
	 */
	public DelegatingAuthorizationMethodBeforeAdvice(List<AuthorizationMethodBeforeAdvice<T>> delegates) {
		Assert.notEmpty(delegates, "delegates cannot be empty");
		this.delegates = delegates;
		ComposablePointcut pointcut = null;
		for (AuthorizationMethodBeforeAdvice<?> advice : delegates) {
			if (pointcut == null) {
				pointcut = new ComposablePointcut(advice.getPointcut());
			}
			else {
				pointcut.union(advice.getPointcut());
			}
		}
		this.pointcut = pointcut;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Pointcut getPointcut() {
		return this.pointcut;
	}

	/**
	 * Delegate to a series of {@link AuthorizationMethodBeforeAdvice}s
	 *
	 * Advices may be of type {@link AuthorizationManagerMethodBeforeAdvice} in which
	 * case, they will throw an
	 * {@link org.springframework.security.access.AccessDeniedException} in the event that
	 * they deny access.
	 * @param authentication the {@link Supplier} of the {@link Authentication} to check
	 * @param object the {@link MethodAuthorizationContext} to check
	 * @throws org.springframework.security.access.AccessDeniedException if any delegate
	 * advices deny access
	 */
	@Override
	public void before(Supplier<Authentication> authentication, T object) {
		if (this.logger.isTraceEnabled()) {
			this.logger.trace(LogMessage.format("Pre Authorizing %s", object));
		}
		for (AuthorizationMethodBeforeAdvice<T> delegate : this.delegates) {
			if (this.logger.isTraceEnabled()) {
				this.logger.trace(LogMessage.format("Checking authorization on %s using %s", object, delegate));
			}
			delegate.before(authentication, object);
		}
	}

}