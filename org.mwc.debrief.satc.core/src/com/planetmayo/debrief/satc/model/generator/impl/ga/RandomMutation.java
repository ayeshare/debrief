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

package com.planetmayo.debrief.satc.model.generator.impl.ga;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.uncommons.maths.random.Probability;

import com.planetmayo.debrief.satc.model.legs.StraightLeg;
import com.planetmayo.debrief.satc.model.legs.StraightRoute;

public class RandomMutation extends AbstractMutation {
	private final RoutesCandidateFactory candidateFactory;

	public RandomMutation(final List<StraightLeg> legs, final Probability mutationProbability,
			final RoutesCandidateFactory candidateFactory) {
		super(legs, mutationProbability);
		this.candidateFactory = candidateFactory;
	}

	@Override
	protected List<StraightRoute> mutate(final List<StraightRoute> candidate, final Random rng) {
		List<StraightRoute> random = candidateFactory.generateRandomCandidate(rng);
		final List<StraightRoute> mutated = new ArrayList<StraightRoute>();
		for (int i = 0; i < candidate.size(); i++) {
			StraightRoute res = candidate.get(i);
			if (!mutationProbability.nextValue().nextEvent(rng)) {
				for (int repeat = 0; repeat < 5; repeat++) {
					res = random.get(i);
					legs.get(i).decideAchievableRoute(res);
					if (res.isPossible()) {
						break;
					}
					random = candidateFactory.generateRandomCandidate(rng);
				}
				if (!res.isPossible()) {
					res = candidate.get(i);
				}
			}
			mutated.add(res);
		}
		return mutated;
	}
}
