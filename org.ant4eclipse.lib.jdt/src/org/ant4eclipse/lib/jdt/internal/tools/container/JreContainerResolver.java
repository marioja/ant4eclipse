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
package org.ant4eclipse.lib.jdt.internal.tools.container;

import org.ant4eclipse.lib.core.logging.A4ELogging;
import org.ant4eclipse.lib.core.service.ServiceRegistryAccess;
import org.ant4eclipse.lib.jdt.model.ClasspathEntry;
import org.ant4eclipse.lib.jdt.model.ContainerTypes;
import org.ant4eclipse.lib.jdt.model.jre.JavaRuntime;
import org.ant4eclipse.lib.jdt.model.jre.JavaRuntimeRegistry;
import org.ant4eclipse.lib.jdt.tools.ResolvedClasspathEntry;
import org.ant4eclipse.lib.jdt.tools.ResolvedClasspathEntry.AccessRestrictions;
import org.ant4eclipse.lib.jdt.tools.container.ClasspathContainerResolver;
import org.ant4eclipse.lib.jdt.tools.container.ClasspathResolverContext;

import java.io.File;
import java.util.LinkedHashSet;
import java.util.Set;

public class JreContainerResolver implements ClasspathContainerResolver {

  /**
   * {@inheritDoc}
   */
  public boolean canResolveContainer(ClasspathEntry classpathEntry) {
    return classpathEntry.getPath().startsWith(ContainerTypes.JRE_CONTAINER);
  }

  /**
   * @param resolver
   */
  public void resolveContainer(ClasspathEntry classpathEntry, ClasspathResolverContext context) {
    //

    if (!context.isCurrentProjectRoot()) {
      return;
    }

    JavaRuntimeRegistry javaRuntimeRegistry = ServiceRegistryAccess.instance().getService(JavaRuntimeRegistry.class);

    String path = classpathEntry.getPath();

    // issue AE-116: re-replace '%' with ' '
    if (path.contains("%")) {
      path = path.replace('%', ' ');
    }

    JavaRuntime javaRuntime = null;
    if (path.equals(ContainerTypes.JRE_CONTAINER)) {
      javaRuntime = javaRuntimeRegistry.getDefaultJavaRuntime();
    } else if (path.startsWith(ContainerTypes.VMTYPE_PREFIX)) {
      String key = path.substring(ContainerTypes.VMTYPE_PREFIX.length());
      javaRuntime = javaRuntimeRegistry.getJavaRuntime(key);
      if (javaRuntime == null) {
        // TODO
        javaRuntime = javaRuntimeRegistry.getDefaultJavaRuntime();
        A4ELogging.warn("Could not find JRE for %s. Using default JRE (Version: %s, Location: %s).", path, javaRuntime
            .getJavaVersion(), javaRuntime.getLocation());

        // the default runtime has been chosen since there's no other possibility.
        // registering the runtime prevents successive fallbacks and annoying redundant warnings.
        javaRuntimeRegistry.registerJavaRuntime(key, javaRuntime.getLocation());
      }
    } else {
      javaRuntime = javaRuntimeRegistry.getDefaultJavaRuntime();
    }

    // TODO
    AccessRestrictions accessRestrictions = null;
    File[] libraries = javaRuntime.getLibraries();

    if (!path.equals(ContainerTypes.JRE_CONTAINER)) {
      String profileKey = path.substring(ContainerTypes.VMTYPE_PREFIX.length());
      if (javaRuntimeRegistry.hasJavaProfile(profileKey)) {
        Set<String> publicPackages = new LinkedHashSet<String>();
        publicPackages.add("java");
        publicPackages.addAll(javaRuntimeRegistry.getJavaProfile(profileKey).getSystemPackages());
        accessRestrictions = new AccessRestrictions(publicPackages, new LinkedHashSet<String>(), true);
      }
    }

    context.setBootClasspathEntry(new ResolvedClasspathEntry(libraries, accessRestrictions));
  }
}
