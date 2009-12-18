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
package org.ant4eclipse;

import org.ant4eclipse.jdt.ant.ClasspathContainersTest;
import org.ant4eclipse.jdt.ant.ClasspathVariablesTest;
import org.ant4eclipse.jdt.ant.ExecuteJdtProjectTest;
import org.ant4eclipse.jdt.ant.GetJdtClassPathTest;
import org.ant4eclipse.jdt.ant.GetJdtClassPath_UnkownContainerTest;
import org.ant4eclipse.jdt.ant.UserLibrariesTest;

import org.ant4eclipse.lib.jdt.tools.BuildOrderResolverTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses( { GetJdtClassPathTest.class, GetJdtClassPath_UnkownContainerTest.class,
    ExecuteJdtProjectTest.class, ClasspathVariablesTest.class, ClasspathContainersTest.class,
    BuildOrderResolverTest.class, UserLibrariesTest.class })
public class AllTests {
}
