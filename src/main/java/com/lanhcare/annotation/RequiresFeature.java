package com.lanhcare.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a controller method as requiring a specific subscription feature.
 * The FeatureGateAspect will intercept calls and check if the user's
 * subscription includes the required feature.
 *
 * For quota-limited features (MEAL_LOG, EXERCISE_LOG, AI_CHAT),
 * the aspect also checks and increments usage count.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresFeature {
    /**
     * The feature code to check, e.g. "MEAL_LOG", "EXERCISE_LOG", "FORUM_POST"
     */
    String value();
}
