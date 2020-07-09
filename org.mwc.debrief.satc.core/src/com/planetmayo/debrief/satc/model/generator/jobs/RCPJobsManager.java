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

import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.planetmayo.debrief.satc.log.LogFactory;
import com.planetmayo.debrief.satc.model.generator.IJobsManager;
import com.planetmayo.debrief.satc_rcp.SATC_Activator;

public class RCPJobsManager implements IJobsManager {
	@SuppressWarnings("rawtypes")
	private final ConcurrentHashMap<Job, org.eclipse.core.runtime.jobs.Job> jobs = new ConcurrentHashMap<Job, org.eclipse.core.runtime.jobs.Job>();

	@Override
	public synchronized <T, P> void cancel(final Job<T, P> job) {
		if (!job.isComplete()) {
			final org.eclipse.core.runtime.jobs.Job eclipseJob = jobs.get(job);
			if (eclipseJob != null) {
				eclipseJob.cancel();
			}
		}
	}

	@Override
	@SuppressWarnings("rawtypes")
	public synchronized void cancelGroup(final String group) {
		if (group == null) {
			return;
		}
		for (final Entry<Job, org.eclipse.core.runtime.jobs.Job> entry : jobs.entrySet()) {
			if (group.equals(entry.getKey().getGroup())) {
				entry.getValue().cancel();
			}
		}
	}

	@Override
	public <T, P> Job<T, P> schedule(final Job<T, P> job) {
		return scheduleAfter(job, null);
	}

	@Override
	public synchronized <T, P, E> Job<T, P> scheduleAfter(final Job<T, P> job, final Job<P, E> previous) {
		if (job == null) {
			throw new IllegalArgumentException("job can't be null");
		}
		org.eclipse.core.runtime.jobs.Job eclipseJob;
		eclipseJob = new org.eclipse.core.runtime.jobs.Job(job.getName()) {

			@Override
			protected IStatus run(final IProgressMonitor monitor) {
				final org.eclipse.core.runtime.jobs.Job oldEclipseJob = previous == null ? null : jobs.get(previous);
				try {
					if (oldEclipseJob != null) {
						oldEclipseJob.join();
					}
					if (previous != null && !previous.isComplete()) {
						LogFactory.getLog().error("Previous job: " + previous.getName() + " wasn't scheduled");
						monitor.setCanceled(true);
					}
					job.startJob(monitor, previous);
					return Status.OK_STATUS;
				} catch (final InterruptedException ex) {
					return Status.CANCEL_STATUS;
				} catch (final Throwable e) {
					return new Status(IStatus.ERROR, SATC_Activator.PLUGIN_ID, e.getMessage(), e);
				} finally {
					jobs.remove(job);
				}
			}
		};
		jobs.put(job, eclipseJob);
		eclipseJob.schedule();
		return job;
	}

	@Override
	public <T, P> void waitFor(final Job<T, P> job) throws InterruptedException {
		if (!job.isComplete()) {
			final org.eclipse.core.runtime.jobs.Job eclipseJob = jobs.get(job);
			if (eclipseJob != null) {
				eclipseJob.join();
			}
		}
	}
}
