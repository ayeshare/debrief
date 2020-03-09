/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/

package com.planetmayo.debrief.satc.model.generator.jobs;

import org.eclipse.core.runtime.IProgressMonitor;

public abstract class Job<T, P> {
	private final String name;

	private final String group;

	private volatile T result = null;

	private volatile Throwable exception = null;

	private volatile boolean complete = false;

	public Job(final String name) {
		this(name, null);
	}

	public Job(final String name, final String group) {
		this.name = name;
		this.group = group;
	}

	public final Throwable getException() {
		if (!complete) {
			return null;
		}
		return exception;
	}

	public String getGroup() {
		return group;
	}

	public String getName() {
		return name;
	}

	public final T getResult() {
		if (!complete) {
			return null;
		}
		return result;
	}

	public final boolean isComplete() {
		return complete;
	}

	public final boolean isFinishedCorrectly() {
		return complete && getException() == null;
	}

	/**
	 * this method executes when jobManager processed the job (job goes to complete
	 * state)
	 *
	 * Override this method in case you need to know when this job was processed,
	 * this is necessary, because Job.run method may not be executed in some cases
	 * (for example previous to this job was canceled or finished with errors), but
	 * you still need to do some work after job was processed (for example: close
	 * resources)
	 *
	 */
	protected void onComplete() {
	}

	/**
	 * Every job must override this method and put there job logic
	 *
	 * @throws InterruptedException when job is canceled
	 */
	protected abstract <E> T run(IProgressMonitor monitor, Job<P, E> previous) throws InterruptedException;

	/**
	 * Runs the job, must be invoked from IJobsManager implementation
	 *
	 * @throws InterruptedException when job is canceled
	 */
	public final <E> void startJob(final IProgressMonitor monitor, final Job<P, E> previous)
			throws InterruptedException {
		try {
			if (previous != null && !previous.isFinishedCorrectly()) {
				throw previous.getException();
			}
			if (monitor.isCanceled()) {
				throw new InterruptedException();
			}
			result = run(monitor, previous);
			if (monitor.isCanceled()) {
				result = null;
				throw new InterruptedException();
			}
		} catch (final InterruptedException e) {
			exception = e;
			throw e;
		} catch (final Throwable e) {
			exception = e;
			if (e instanceof RuntimeException) {
				throw (RuntimeException) e;
			}
			throw new RuntimeException(e);
		} finally {
			complete = true;
			onComplete();
		}
	}
}
