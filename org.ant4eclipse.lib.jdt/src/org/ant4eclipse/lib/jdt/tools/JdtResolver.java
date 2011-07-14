/**********************************************************************
 * Copyright (c) 2005-2009 ant4eclipse project team.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Nils Hartmann, Daniel Kasmeroglu, Gerd Wuetherich
 **********************************************************************/
package org.ant4eclipse.lib.jdt.tools;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.ant4eclipse.lib.core.Assure;
import org.ant4eclipse.lib.core.util.PerformanceLogging;
import org.ant4eclipse.lib.jdt.internal.tools.ClasspathEntryResolverExecutor;
import org.ant4eclipse.lib.jdt.internal.tools.ClasspathResolverContextImpl;
import org.ant4eclipse.lib.jdt.internal.tools.ResolvedClasspathImpl;
import org.ant4eclipse.lib.jdt.internal.tools.ResolverJob;
import org.ant4eclipse.lib.jdt.internal.tools.classpathentry.ClasspathEntryResolver;
import org.ant4eclipse.lib.jdt.internal.tools.classpathentry.ContainerClasspathEntryResolver;
import org.ant4eclipse.lib.jdt.internal.tools.classpathentry.LibraryClasspathEntryResolver;
import org.ant4eclipse.lib.jdt.internal.tools.classpathentry.OutputClasspathEntryResolver;
import org.ant4eclipse.lib.jdt.internal.tools.classpathentry.ProjectClasspathEntryResolver;
import org.ant4eclipse.lib.jdt.internal.tools.classpathentry.SourceClasspathEntryResolver;
import org.ant4eclipse.lib.jdt.internal.tools.classpathentry.VariableClasspathEntryResolver;
import org.ant4eclipse.lib.jdt.tools.container.JdtClasspathContainerArgument;
import org.ant4eclipse.lib.platform.model.resource.EclipseProject;

/**
 * <p>
 * Helper class. Use this class to resolve the class path of a given eclipse project.
 * </p>
 * 
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 */
public class JdtResolver {

  /**
   * System-Property that allows disabling the Jdt-Resolver Cache
   */
  private static final boolean                        DISABLE_CACHE   = Boolean
                                                                          .getBoolean("ant4eclipse.disableJdtResolverCache");

  private static final Map<String, ResolvedClasspath> _classpathCache = new Hashtable<String, ResolvedClasspath>();

  /**
   * <p>
   * Resolves the class path of the given eclipse project.
   * </p>
   * 
   * @param project
   *          the eclipse project that should be resolved
   * @param resolveRelative
   *          indicates if the class path should be resolved relative to the workspace or not.
   * @param isRuntimeClasspath
   *          indicates if the class path is a runtime class path or not
   * @param classpathContainerArguments
   *          an optional list with class path container arguments
   * @return the resolved class path
   */
  public static final ResolvedClasspath resolveProjectClasspath(EclipseProject project, boolean resolveRelative,
      boolean isRuntimeClasspath, List<JdtClasspathContainerArgument> classpathContainerArguments) {

    if (DISABLE_CACHE) {
      // cache is disabled, always re-resolve classpath
      return doResolveProjectClasspath(project, resolveRelative, isRuntimeClasspath, classpathContainerArguments);
    }

    // determine the key for the cached classpath
    String cacheKey = getCacheKey(project, resolveRelative, isRuntimeClasspath);

    // try to get ResolvedClasspath from the cache
    ResolvedClasspath resolvedClasspath = _classpathCache.get(cacheKey);

    if (resolvedClasspath == null) {
      // Classpath has not been resolved yet -> resolve it now
      resolvedClasspath = doResolveProjectClasspath(project, resolveRelative, isRuntimeClasspath,
          classpathContainerArguments);

      // add the resolved classpath to the cache
      _classpathCache.put(cacheKey, resolvedClasspath);
    }

    // return the classpath
    return resolvedClasspath;
  }

  private static final ResolvedClasspath doResolveProjectClasspath(EclipseProject project, boolean resolveRelative,
      boolean isRuntimeClasspath, List<JdtClasspathContainerArgument> classpathContainerArguments) {

    Assure.notNull("project", project);

    // Start performance logging
    PerformanceLogging.start(JdtResolver.class, "doResolveProjectClasspath");

    // create a ResolverJob
    ResolverJob job = new ResolverJob(project, project.getWorkspace(), resolveRelative, isRuntimeClasspath,
        classpathContainerArguments);

    // create the ClasspathEntryResolverExecutor
    ClasspathEntryResolverExecutor executor = new ClasspathEntryResolverExecutor(true);

    // create the ClasspathEntryResolvers
    ClasspathEntryResolver[] resolvers = new ClasspathEntryResolver[] { new VariableClasspathEntryResolver(),
        new ContainerClasspathEntryResolver(), new SourceClasspathEntryResolver(), new ProjectClasspathEntryResolver(),
        new LibraryClasspathEntryResolver(), new OutputClasspathEntryResolver() };

    // create the result object
    ResolvedClasspathImpl resolvedClasspath = new ResolvedClasspathImpl();

    // execute the job
    executor.resolve(job.getRootProject(), resolvers,
        new ClasspathResolverContextImpl(executor, job, resolvedClasspath));

    // stop performance logging
    PerformanceLogging.stop(JdtResolver.class, "doResolveProjectClasspath");

    // return the ResolvedClasspath
    return resolvedClasspath;
  }

  private static String getCacheKey(EclipseProject project, boolean resolveRelative, boolean runtimeClasspath) {
    // TODO include classpathContainerArguments in key
    return project.getSpecifiedName() + "." + resolveRelative + "." + runtimeClasspath;
  }
}
